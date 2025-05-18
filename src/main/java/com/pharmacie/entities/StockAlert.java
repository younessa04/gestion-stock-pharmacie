package com.pharmacie.entities;

import java.util.Date;

public class StockAlert {
    private int id;
    private int produitId;
    private String produitNom;
    private String alertType; // RUPTURE ou FAIBLE
    private String message;
    private Date alertDate;
    private String status; // ACTIVE ou RESOLVED
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProduitId() { return produitId; }
    public void setProduitId(int produitId) { this.produitId = produitId; }
    public String getProduitNom() { return produitNom; }
    public void setProduitNom(String produitNom) { this.produitNom = produitNom; }
    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Date getAlertDate() { return alertDate; }
    public void setAlertDate(Date alertDate) { this.alertDate = alertDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}