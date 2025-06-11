package com.pharmacie.entities;

import java.sql.Date;
import java.util.List;

public class Achat {
    private int id;
    private Date dateAchat;
    private double montantTotal;
    private int idFournisseur;
    private String nomFournisseur; // Pour afficher le nom du fournisseur

    // Un achat contient plusieurs lignes d'achat
    private List<LigneAchat> lignesAchat;

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(Date dateAchat) {
        this.dateAchat = dateAchat;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public int getIdFournisseur() {
        return idFournisseur;
    }

    public void setIdFournisseur(int idFournisseur) {
        this.idFournisseur = idFournisseur;
    }

    public String getNomFournisseur() {
        return nomFournisseur;
    }

    public void setNomFournisseur(String nomFournisseur) {
        this.nomFournisseur = nomFournisseur;
    }

    public List<LigneAchat> getLignesAchat() {
        return lignesAchat;
    }

    public void setLignesAchat(List<LigneAchat> lignesAchat) {
        this.lignesAchat = lignesAchat;
    }
}