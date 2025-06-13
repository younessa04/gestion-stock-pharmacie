package com.pharmacie.servlets;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/generate-stock-report")
public class RapportServlet extends HttpServlet {

    private static final String API_KEY = "AIzaSyBU9R_lFYwpp7g7q4KTZ76qczsIcyTN6Wk";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ServletException("Driver JDBC MySQL non trouvé", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/pharmacy", "root", "");
    }

    private List<String> getAchats() throws SQLException {
        List<String> achats = new ArrayList<>();
        String sql = """
            SELECT p.Nom, la.Quantite, a.date_achat FROM Achat a
            JOIN LigneAchat la ON a.ID_Achat = la.ID_Achat
            JOIN Produit p ON la.ID_Produit = p.ID_Produit
            ORDER BY a.date_achat DESC LIMIT 10
        """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ligne = rs.getString("Nom") + " - " + rs.getInt("Quantite") + " unités le " + rs.getDate("date_achat");
                achats.add(ligne);
            }
        }
        return achats;
    }

    private List<String> getVentes() throws SQLException {
        List<String> ventes = new ArrayList<>();
        String sql = """
            SELECT p.Nom, lv.Quantite, v.datevente FROM Vente v
            JOIN LigneVente lv ON v.ID_Vente = lv.ID_Vente
            JOIN Produit p ON lv.ID_Produit = p.ID_Produit
            ORDER BY v.datevente DESC LIMIT 10
        """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ligne = rs.getString("Nom") + " - " + rs.getInt("Quantite") + " unités le " + rs.getDate("datevente");
                ventes.add(ligne);
            }
        }
        return ventes;
    }
    
    private List<String> getProduits() throws SQLException {
        List<String> ventes = new ArrayList<>();
        String sql = """
            SELECT * from produit
        """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ligne = rs.getString("Nom")  + " - " +  + rs.getInt("stockactuel") + " - "  + rs.getInt("stockmin");
                ventes.add(ligne);
            }
        }
        return ventes;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=UTF-8");
        
        try {
        	List<String> produits = getProduits();
            List<String> achats = getAchats();
            List<String> ventes = getVentes();

            String prompt = """
                Voici des données de stock :
                - Produit : %s
                - Achats : %s
                - Ventes : %s

                Génère un rapport explicatif pour les donnees de cette pharmacie. la reponse doit sous forme de text simple    16px
            """.formatted(String.join(", ", achats), String.join(", ", ventes), String.join(", ", produits));

            String jsonBody = """
                {
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": "%s"
                        }
                      ]
                    }
                  ]
                }
            """.formatted(prompt.replace("\"", "\\\"")); // échappe les guillemets si besoin

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            String body = httpResponse.body();
            String rapport = extractTextFromGeminiResponse(body);

            request.setAttribute("rapport", rapport);
            request.getRequestDispatcher("/rapportStock.jsp").forward(request, response);

        } catch (SQLException e) {
            response.getWriter().write("Erreur base de données : " + e.getMessage());
        } catch (Exception e) {
            response.getWriter().write("Erreur inattendue : " + e.getMessage());
            e.printStackTrace(response.getWriter());
        }
    }

    private String extractTextFromGeminiResponse(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        JsonNode candidates = root.get("candidates");
        if (candidates != null && candidates.isArray() && candidates.size() > 0) {
            JsonNode textNode = candidates.get(0).path("content").path("parts").get(0).path("text");
            if (!textNode.isMissingNode()) {
                return textNode.asText().replace("\n", "<br>");
            }
        }
        return "Erreur d'analyse de la réponse Gemini.";
    }
}
