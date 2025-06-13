package com.pharmacie.services;

import com.pharmacie.entities.StockAlert;
import com.pharmacie.entities.Produit;

import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {

    // Configuration SMTP
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USERNAME = "younes213saka@gmail.com";
    private static final String SMTP_PASSWORD = "jhaz qkap xnxq aidj"; // Mot de passe d'application
    private static final String FROM_EMAIL = "younes213saka@gmail.com";
    private static final String TO_EMAIL = "younes213saka@gmail.com";

    // 1. Envoi d’alertes stock
    public static void sendStockAlerts(List<StockAlert> alerts) {
        if (alerts == null || alerts.isEmpty()) return;

        String subject = "⚠ Alerte Stock Pharmacie - " + alerts.size() + " produit(s) concerné(s)";
        String htmlContent = buildEmailContent(alerts);
        sendHtmlEmail(subject, htmlContent);
    }

    // 2. Envoi générique d’un email HTML (rapport ou alerte)
    public static void sendHtmlEmail(String subject, String htmlContent) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.trust", SMTP_HOST);

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO_EMAIL));
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("✅ Email envoyé avec succès à " + TO_EMAIL);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 3. Génération du contenu HTML des alertes
    private static String buildEmailContent(List<StockAlert> alerts) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><style>")
            .append("body { font-family: Arial, sans-serif; }")
            .append(".container { max-width: 800px; margin: 0 auto; }")
            .append(".header { background-color: #d9534f; color: white; padding: 15px; text-align: center; }")
            .append(".alert-section { margin: 20px 0; }")
            .append("table { width: 100%; border-collapse: collapse; }")
            .append("th, td { padding: 8px; text-align: left; border-bottom: 1px solid #ddd; }")
            .append(".rupture { color: #d9534f; }")
            .append(".faible { color: #f0ad4e; }")
            .append("</style></head><body><div class='container'>")
            .append("<div class='header'><h2>⚠ Alertes Stock Pharmacie</h2></div>")
            .append("<p>Bonjour,</p><p>Voici les produits nécessitant votre attention :</p>");

        long ruptureCount = alerts.stream().filter(a -> "RUPTURE".equals(a.getAlertType())).count();
        if (ruptureCount > 0) {
            html.append("<div class='alert-section'><h3>Produits en rupture (")
                .append(ruptureCount).append(")</h3><table>")
                .append("<tr><th>Produit</th><th>Dernière alerte</th></tr>");
            alerts.stream().filter(a -> "RUPTURE".equals(a.getAlertType())).forEach(a ->
                html.append("<tr class='rupture'><td>").append(a.getProduitNom())
                    .append("</td><td>").append(a.getAlertDate()).append("</td></tr>")
            );
            html.append("</table></div>");
        }

        long faibleCount = alerts.stream().filter(a -> "FAIBLE".equals(a.getAlertType())).count();
        if (faibleCount > 0) {
            html.append("<div class='alert-section'><h3>Produits avec stock faible (")
                .append(faibleCount).append(")</h3><table>")
                .append("<tr><th>Produit</th><th>Message</th><th>Dernière alerte</th></tr>");
            alerts.stream().filter(a -> "FAIBLE".equals(a.getAlertType())).forEach(a ->
                html.append("<tr class='faible'><td>").append(a.getProduitNom())
                    .append("</td><td>").append(a.getMessage())
                    .append("</td><td>").append(a.getAlertDate()).append("</td></tr>")
            );
            html.append("</table></div>");
        }

        html.append("<p>Merci de réagir rapidement pour assurer la disponibilité des médicaments.</p>")
            .append("<p>Cordialement,<br>Le système de gestion</p></div></body></html>");
        return html.toString();
    }

    // 4. Génération du rapport complet HTML depuis liste de produits
    public static String buildFullReport(List<Produit> produits) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><style>")
            .append("body { font-family: Arial, sans-serif; }")
            .append("table { width: 100%; border-collapse: collapse; }")
            .append("th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }")
            .append("th { background-color: #f2f2f2; }")
            .append("</style></head><body>")
            .append("<h2>📊 Rapport Complet du Stock</h2>")
            .append("<table><tr><th>Nom</th><th>Code Barre</th><th>Forme</th><th>Dosage</th><th>Stock Actuel</th><th>Stock Min</th><th>Date Expiration</th></tr>");

        for (Produit p : produits) {
            html.append("<tr>")
                .append("<td>").append(p.getNom()).append("</td>")
                .append("<td>").append(p.getCodeBarre()).append("</td>")
                .append("<td>").append(p.getForme()).append("</td>")
                .append("<td>").append(p.getDosage()).append("</td>")
                .append("<td>").append(p.getStockActuel()).append("</td>")
                .append("<td>").append(p.getStockMin()).append("</td>")
                .append("<td>").append(p.getDateExpiration()).append("</td>")
                .append("</tr>");
        }

        html.append("</table>")
            .append("<p>Rapport généré automatiquement par le système.</p>")
            .append("</body></html>");
        return html.toString();
    }
}
