package com.pharmacie.services;

import com.pharmacie.entities.StockAlert;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {
    // Configuration SMTP (à adapter avec vos paramètres)
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USERNAME = "younes213saka@gmail.com";
    private static final String SMTP_PASSWORD = "jhaz qkap xnxq aidj"; // Utiliser un mot de passe d'application
    private static final String FROM_EMAIL = "younes213saka@gmail.com";
    private static final String TO_EMAIL = "younes213saka@gmail.com"; // Email de destination

    public static void sendStockAlerts(List<StockAlert> alerts) {
        if (alerts == null || alerts.isEmpty()) {
            return;
        }

        try {
            // Configuration SMTP
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.trust", SMTP_HOST);

            // Authentification
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
                }
            });

            // Construction du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO_EMAIL));
            message.setSubject("⚠ Alerte Stock Pharmacie - " + alerts.size() + " produit(s) concerné(s)");

            // Construction du contenu HTML
            String htmlContent = buildEmailContent(alerts);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            // Envoi de l'email
            Transport.send(message);
            
            System.out.println("Email d'alerte envoyé avec succès à " + TO_EMAIL);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email d'alerte : " + e.getMessage());
            e.printStackTrace();
        }
    }

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

        // Section Rupture de stock
        long ruptureCount = alerts.stream().filter(a -> "RUPTURE".equals(a.getAlertType())).count();
        if (ruptureCount > 0) {
            html.append("<div class='alert-section'><h3>Produits en rupture de stock (").append(ruptureCount).append(")</h3><table>")
               .append("<tr><th>Produit</th><th>Dernière alerte</th></tr>");
            
            alerts.stream()
                  .filter(a -> "RUPTURE".equals(a.getAlertType()))
                  .forEach(a -> html.append("<tr class='rupture'><td>").append(a.getProduitNom())
                                  .append("</td><td>").append(a.getAlertDate()).append("</td></tr>"));
            
            html.append("</table></div>");
        }

        // Section Stock faible
        long faibleCount = alerts.stream().filter(a -> "FAIBLE".equals(a.getAlertType())).count();
        if (faibleCount > 0) {
            html.append("<div class='alert-section'><h3>Produits avec stock faible (").append(faibleCount).append(")</h3><table>")
               .append("<tr><th>Produit</th><th>Message</th><th>Dernière alerte</th></tr>");
            
            alerts.stream()
                  .filter(a -> "FAIBLE".equals(a.getAlertType()))
                  .forEach(a -> html.append("<tr class='faible'><td>").append(a.getProduitNom())
                                  .append("</td><td>").append(a.getMessage())
                                  .append("</td><td>").append(a.getAlertDate()).append("</td></tr>"));
            
            html.append("</table></div>");
        }

        html.append("<p>Veuillez prendre les mesures nécessaires pour réapprovisionner ces produits.</p>")
           .append("<p>Cordialement,<br>Le système de gestion de stock</p>")
           .append("</div></body></html>");

        return html.toString();
    }
}