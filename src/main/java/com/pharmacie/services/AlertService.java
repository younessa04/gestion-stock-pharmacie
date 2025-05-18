package com.pharmacie.services;

import com.pharmacie.dao.ProduitDAO;
import com.pharmacie.dao.StockAlertDAO;
import com.pharmacie.entities.Produit;
import com.pharmacie.entities.StockAlert;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlertService {
    private final ProduitDAO produitDAO = new ProduitDAO();
    private final StockAlertDAO alertDAO = new StockAlertDAO();
    
    public List<StockAlert> checkAndGenerateAlerts() throws SQLException {
        List<StockAlert> newAlerts = generateStockAlerts();
        if (!newAlerts.isEmpty()) {
            // Envoyer les alertes par email
            EmailService.sendStockAlerts(newAlerts);
        }
        return getActiveAlerts();
    }
    
    public List<StockAlert> generateStockAlerts() throws SQLException {
        List<StockAlert> alerts = new ArrayList<>();
        
        // Produits en rupture
		List<Produit> produitsRupture = produitDAO.getProduitsEnRupture();
		for (Produit p : produitsRupture) {
		    alerts.add(createAlert(p, "RUPTURE", "Produit en rupture de stock"));
		}
		
		// Produits en stock faible
		List<Produit> produitsFaible = produitDAO.getProduitsStockFaible();
		for (Produit p : produitsFaible) {
		    alerts.add(createAlert(p, "FAIBLE", 
		        "Stock faible (" + p.getStockActuel() + "/" + p.getStockMin() + ")"));
		}
		
		// Sauvegarde en base
		alertDAO.saveAlerts(alerts);
        
        return alerts;
    }
    
    public List<StockAlert> getActiveAlerts() {
        return alertDAO.getActiveAlerts();
    }
    
    private StockAlert createAlert(Produit produit, String alertType, String message) {
        StockAlert alert = new StockAlert();
        alert.setProduitId(produit.getId());
        alert.setProduitNom(produit.getNom());
        alert.setAlertType(alertType);
        alert.setMessage(message);
        alert.setAlertDate(new Date());
        alert.setStatus("ACTIVE");
        return alert;
    }
}