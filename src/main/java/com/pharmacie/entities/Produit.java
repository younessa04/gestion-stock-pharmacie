package com.pharmacie.entities;

import java.sql.Date;

public class Produit {
    private int id;
    private String nom;
    private String codeBarre;
    private String forme;
    private String dosage;
    private double prixVente;
    private int stockActuel;
    private int stockMin;
    private Date dateExpiration;
    private int idFournisseur;
    private String nomFournisseur;

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getCodeBarre() { return codeBarre; }
    public void setCodeBarre(String codeBarre) { this.codeBarre = codeBarre; }
    public String getForme() { return forme; }
    public void setForme(String forme) { this.forme = forme; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public double getPrixVente() { return prixVente; }
    public void setPrixVente(double prixVente) { this.prixVente = prixVente; }
    public int getStockActuel() { return stockActuel; }
    public void setStockActuel(int stockActuel) { this.stockActuel = stockActuel; }
    public int getStockMin() { return stockMin; }
    public void setStockMin(int stockMin) { this.stockMin = stockMin; }
    public Date getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(Date dateExpiration) { this.dateExpiration = dateExpiration; }
    public int getIdFournisseur() { return idFournisseur; }
    public void setIdFournisseur(int idFournisseur) { this.idFournisseur = idFournisseur; }
    public String getNomFournisseur() { return nomFournisseur; }
    public void setNomFournisseur(String nomFournisseur) { this.nomFournisseur = nomFournisseur; }
}