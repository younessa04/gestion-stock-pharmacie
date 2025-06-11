package com.pharmacie.services;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class GeminiReportService {
    private static final String API_KEY = "AIzaSyBU9R_lFYwpp7g7q4KTZ76qczsIcyTN6Wk"; 
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;

    public String generateReport(String prompt) {
        // Préparer le corps de la requête JSON pour l'API Gemini
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

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Gérer la réponse de l'API
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                // Ici, nous faisons une extraction simple du texte.
                // Pour une solution plus robuste, utilisez une bibliothèque JSON (ex: Jackson, Gson)
                // pour parser la réponse et extraire proprement le champ 'text'.
                int textStartIndex = responseBody.indexOf("\"text\": \"");
                if (textStartIndex != -1) {
                    textStartIndex += "\"text\": \"".length();
                    int textEndIndex = responseBody.indexOf("\"", textStartIndex);
                    if (textEndIndex != -1) {
                        String generatedText = responseBody.substring(textStartIndex, textEndIndex);
                        // Remplacer les séquences d'échappement si Gemini les renvoie (ex: \n pour des retours à la ligne)
                        return generatedText.replace("\\n", "\n").replace("\\\"", "\"");
                    }
                }
                return "Erreur: Impossible d'extraire le texte de la réponse Gemini. Réponse brute: " + responseBody;
            } else {
                return "Erreur de l'API Gemini: Code " + response.statusCode() + ", Réponse: " + response.body();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Erreur lors de l'appel à l'API Gemini: " + e.getMessage();
        }
    }
}
