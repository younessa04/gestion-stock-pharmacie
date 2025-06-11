package com.pharmacie.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacie.util.ConnectDb;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.*;

public class GeminiReportService {

    private static final String API_KEY = "AIzaSyBU9R_lFYwpp7g7q4KTZ76qczsIcyTN6Wk";
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + API_KEY;

    public static void main(String[] args) {
        try {
            // 1. Lire les données de la base
            StringBuilder dataToSummarize = new StringBuilder();
            try (Connection conn = ConnectDb.getConnection()) {

                // Résumé des produits
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT nom, stock_actuel, prix_vente FROM Produit LIMIT 5");
                dataToSummarize.append("Liste de produits:\n");
                while (rs.next()) {
                    dataToSummarize.append("- ")
                            .append(rs.getString("nom"))
                            .append(", Stock: ").append(rs.getInt("stock_actuel"))
                            .append(", Prix: ").append(rs.getDouble("prix_vente"))
                            .append("€\n");
                }

                // Résumé des ventes
                rs = st.executeQuery("SELECT COUNT(*) AS total_ventes, SUM(montant_total) AS total_vente_euros FROM Vente");
                if (rs.next()) {
                    dataToSummarize.append("\nStatistiques des ventes:\n")
                            .append("Nombre total de ventes: ").append(rs.getInt("total_ventes")).append("\n")
                            .append("Montant total des ventes: ").append(rs.getDouble("total_vente_euros")).append("€\n");
                }

                // Résumé des achats
                rs = st.executeQuery("SELECT COUNT(*) AS total_achats, SUM(montant_total) AS total_achat_euros FROM Achat");
                if (rs.next()) {
                    dataToSummarize.append("\nStatistiques des achats:\n")
                            .append("Nombre total d'achats: ").append(rs.getInt("total_achats")).append("\n")
                            .append("Montant total des achats: ").append(rs.getDouble("total_achat_euros")).append("€\n");
                }

            } catch (SQLException e) {
                System.err.println("❌ Erreur SQL");
                e.printStackTrace();
                return;
            }

            // 2. Préparer la requête JSON
            String prompt = "Génère un rapport clair et professionnel en français à partir des données suivantes :\n" + dataToSummarize;

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

            // 3. Requête vers Gemini
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 4. Affichage de la réponse
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonResponse = mapper.readValue(response.body(), Map.class);

            System.out.println("📊 Rapport généré par Gemini :\n");
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonResponse));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
