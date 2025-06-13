package com.pharmacie.util;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TestGemini {

    private static final String API_KEY = "AIzaSyBU9R_lFYwpp7g7q4KTZ76qczsIcyTN6Wk";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;

    private static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/pharmacy";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

    private static List<String> getAchats() throws SQLException {
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

    private static List<String> getVentes() throws SQLException {
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

    public static void main(String[] args) {
        try {
            List<String> achats = getAchats();
            List<String> ventes = getVentes();

            String prompt = """
                    Voici des données de stock :
                    - Achats : %s
                    - Ventes : %s

                    Génère un rapport simple résumant l'état du stock et les ventes et les achats. Sois clair et concis.
                    """.formatted(String.join(", ", achats), String.join(", ", ventes));

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
                    """.formatted(URLEncoder.encode(prompt, StandardCharsets.UTF_8));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("\n--- Réponse brute de Gemini ---\n");
            System.out.println(response.body());

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
