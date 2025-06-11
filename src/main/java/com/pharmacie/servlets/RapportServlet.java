package com.pharmacie.servlets;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacie.util.ConnectDb;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.sql.*;
import java.util.Map;

@WebServlet("/rapport")
public class RapportServlet extends HttpServlet {

    private static final String API_KEY = "YOUR_API_KEY_HERE";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + API_KEY;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StringBuilder data = new StringBuilder();

        try (Connection conn = ConnectDb.getConnection()) {
            Statement st = conn.createStatement();

            // Produits
            ResultSet rs = st.executeQuery("SELECT nom, stock_actuel, prix_vente FROM Produit LIMIT 5");
            data.append("Produits:\n");
            while (rs.next()) {
                data.append("- ").append(rs.getString("nom"))
                        .append(" (Stock: ").append(rs.getInt("stock_actuel"))
                        .append(", Prix: ").append(rs.getDouble("prix_vente")).append(" €)\n");
            }

            // Ventes
            rs = st.executeQuery("SELECT COUNT(*) AS total, SUM(montant_total) AS total_euros FROM Vente");
            if (rs.next()) {
                data.append("\nVentes:\n")
                    .append("Total: ").append(rs.getInt("total")).append(" ventes\n")
                    .append("Montant: ").append(rs.getDouble("total_euros")).append(" €\n");
            }

            // Achats
            rs = st.executeQuery("SELECT COUNT(*) AS total, SUM(montant_total) AS total_euros FROM Achat");
            if (rs.next()) {
                data.append("\nAchats:\n")
                    .append("Total: ").append(rs.getInt("total")).append(" achats\n")
                    .append("Montant: ").append(rs.getDouble("total_euros")).append(" €\n");
            }

        } catch (SQLException e) {
            throw new ServletException("Erreur SQL", e);
        }

        // Appel à l’API Gemini
        try {
            String prompt = "Fais un rapport synthétique et professionnel sur la pharmacie avec ces données :\n" + data;

            String jsonInput = """
            {
              "contents": [
                {
                  "parts": [
                    {
                      "text": %s
                    }
                  ]
                }
              ]
            }
            """.formatted(new ObjectMapper().writeValueAsString(prompt));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Map<String, Object> jsonResponse = new ObjectMapper().readValue(response.body(), Map.class);

            // Extraire la réponse textuelle
            String rapportTexte = ((Map)((Map)((java.util.List) jsonResponse.get("candidates")).get(0)).get("content")).get("parts").toString();
            req.setAttribute("rapportTexte", rapportTexte);

        } catch (Exception e) {
            throw new ServletException("Erreur avec l’API Gemini", e);
        }

        // Transfert vers la JSP
        req.getRequestDispatcher("/rapport.jsp").forward(req, resp);
    }
}
