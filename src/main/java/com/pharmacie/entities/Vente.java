package com.pharmacie.entities;

import java.sql.Date;
import java.util.List;

public class Vente {
    private int id;
    private Date dateVente;
    private double montantTotal;
    // idUtilisateur et nomUtilisateur sont retirés comme demandé.

    // Une vente contient plusieurs lignes de vente
    private List<LigneVente> lignesVente;

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateVente() {
        return dateVente;
    }

    public void setDateVente(Date dateVente) {
        this.dateVente = dateVente;
    }

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public List<LigneVente> getLignesVente() {
        return lignesVente;
    }

    public void setLignesVente(List<LigneVente> lignesVente) {
        this.lignesVente = lignesVente;
    }
}