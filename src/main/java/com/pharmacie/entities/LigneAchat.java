package com.pharmacie.entities;

public class LigneAchat {
    private int idLigneAchat;
    private int idAchat; // Clé étrangère vers la table Achat
    private int idProduit; // Clé étrangère vers la table Produit
    private int quantite;
    private double prixAchat; // Prix d'achat unitaire
    private String nomProduit; // Pour afficher le nom du produit

    // Getters et Setters
    public int getIdLigneAchat() {
        return idLigneAchat;
    }

    public void setIdLigneAchat(int idLigneAchat) {
        this.idLigneAchat = idLigneAchat;
    }

    public int getIdAchat() {
        return idAchat;
    }

    public void setIdAchat(int idAchat) {
        this.idAchat = idAchat;
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

    public double getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(double prixAchat) {
        this.prixAchat = prixAchat;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }
}