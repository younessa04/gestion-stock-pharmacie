package com.pharmacie.services;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class GeminiService {

    private static final String API_KEY = "AIzaSyBU9R_lFYwpp7g7q4KTZ76qczsIcyTN6Wk";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + API_KEY;

    public static String genererRapportStock(int totalProduits, int totalVentes, int totalAchats) throws IOException {
        String prompt = String.format("""
        Génère un rapport simple de stock avec ces données :
        - Produits : %d
        - Ventes : %d
        - Achats : %d
        Résume en 5 lignes de manière professionnelle.
        """, totalProduits, totalVentes, totalAchats);

        String requestBody = """
        {
          "contents": [{
            "parts": [{
              "text": "%s"
            }]
          }]
        }
        """.formatted(prompt.replace("\"", "\\\""));

        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes());
        }

        // Lire la réponse
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        conn.disconnect();

        // Extraire le texte généré (naïvement)
        String json = response.toString();
        int start = json.indexOf("\"text\":\"") + 8;
        int end = json.indexOf("\"", start);
        if (start > 0 && end > start) {
            return json.substring(start, end).replace("\\n", "\n");
        } else {
            return "Erreur d’analyse de la réponse Gemini";
        }
    }
}
