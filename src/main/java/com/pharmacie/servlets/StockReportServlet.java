package com.pharmacie.servlets;

import com.pharmacie.dao.ProduitDAO;
import com.pharmacie.dao.StockAlertDAO;
import com.pharmacie.entities.Produit;
import com.pharmacie.entities.StockAlert;
import com.pharmacie.services.EmailService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/stock-report")
public class StockReportServlet extends HttpServlet {
    private final ProduitDAO produitDAO = new ProduitDAO();
    private final StockAlertDAO alertDAO = new StockAlertDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            String reportType = request.getParameter("type");
            String action = request.getParameter("action");
            
            // Gestion de l'envoi d'alertes
            if ("send-alerts".equals(action)) {
                List<StockAlert> generatedAlerts = generateStockAlerts();
                if (!generatedAlerts.isEmpty()) {
                    alertDAO.saveAlerts(generatedAlerts);
                    sendEmailNotifications(generatedAlerts);
                    request.setAttribute("success", generatedAlerts.size() + " alertes générées et envoyées !");
                } else {
                    request.setAttribute("info", "Aucune alerte à envoyer");
                }
            }
            
            // Charger les alertes actives pour affichage
            List<StockAlert> activeAlerts = alertDAO.getActiveAlerts();
            request.setAttribute("activeAlerts", activeAlerts);
            request.setAttribute("now", new Date());
            // Gestion du rapport de stock
            List<Produit> produits;
            String title;
            
            switch(reportType != null ? reportType : "") {
                case "rupture":
                    produits = produitDAO.getProduitsEnRupture();
                    title = "Produits en rupture de stock";
                    break;
                    
                case "faible":
                    produits = produitDAO.getProduitsStockFaible();
                    title = "Produits avec stock faible";
                    break;
                    
                case "normal":
                    produits = produitDAO.getProduitsStockNormal();
                    title = "Produits avec stock normal";
                    break;
                    
                default:
                    produits = produitDAO.getAllProduits();
                    title = "Rapport complet de stock";
            }
            
            // Ajout des compteurs pour le menu
            request.setAttribute("countRupture", produitDAO.getProduitsEnRupture().size());
            request.setAttribute("countFaible", produitDAO.getProduitsStockFaible().size());
            request.setAttribute("countNormal", produitDAO.getProduitsStockNormal().size());
            
            request.setAttribute("produits", produits);
            request.setAttribute("reportTitle", title);
            request.getRequestDispatcher("/stockReport.jsp").forward(request, response);
            
        } catch (SQLException e) {
            request.setAttribute("error", "Erreur base de données: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Erreur système: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private List<StockAlert> generateStockAlerts() throws SQLException {
        List<StockAlert> alerts = new ArrayList<>();
        
        // Alertes pour produits en rupture
        List<Produit> produitsRupture = produitDAO.getProduitsEnRupture();
        for (Produit p : produitsRupture) {
            StockAlert alert = new StockAlert();
            alert.setProduitId(p.getId());
            alert.setProduitNom(p.getNom());
            alert.setAlertType("RUPTURE");
            alert.setMessage("Rupture de stock - Commander urgemment");
            alert.setAlertDate(new Date());
            alert.setStatus("ACTIVE");
            alerts.add(alert);
        }
        
        // Alertes pour stock faible
        List<Produit> produitsFaible = produitDAO.getProduitsStockFaible();
        for (Produit p : produitsFaible) {
            StockAlert alert = new StockAlert();
            alert.setProduitId(p.getId());
            alert.setProduitNom(p.getNom());
            alert.setAlertType("FAIBLE");
            alert.setMessage("Stock faible (" + p.getStockActuel() + " unités) - Seuil minimum: " + p.getStockMin());
            alert.setAlertDate(new Date());
            alert.setStatus("ACTIVE");
            alerts.add(alert);
        }
        
        return alerts;
    }

    private void sendEmailNotifications(List<StockAlert> alerts) {
        if (alerts == null || alerts.isEmpty()) {
            return;
        }
        
        try {
            // Utilisez la méthode sendStockAlerts de EmailService
            EmailService.sendStockAlerts(alerts);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi des notifications: " + e.getMessage());
            e.printStackTrace();
        }
    }
}