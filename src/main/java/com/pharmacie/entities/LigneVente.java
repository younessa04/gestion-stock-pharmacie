package com.pharmacie.entities;

public class LigneVente {
    private int idLigneVente;
    private int idVente; // Clé étrangère vers la table Vente
    private int idProduit; // Clé étrangère vers la table Produit
    private int quantite;
    private double prixUnitaire;
    private String nomProduit; // Pour afficher le nom du produit

    // Getters et Setters
    public int getIdLigneVente() {
        return idLigneVente;
    }

    public void setIdLigneVente(int idLigneVente) {
        this.idLigneVente = idLigneVente;
    }

    public int getIdVente() {
        return idVente;
    }

    public void setIdVente(int idVente) {
        this.idVente = idVente;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }
}