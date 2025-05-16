package com.pharmacie.dao;

import com.pharmacie.entities.Fournisseur;
import com.pharmacie.util.ConnectDb;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FournisseurDAO {
    private Connection connection;

    public FournisseurDAO() {
        this.connection = ConnectDb.getConnection();
    }

    public List<Fournisseur> getAllFournisseurs() {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM Fournisseur ORDER BY Nom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Fournisseur f = new Fournisseur();
                f.setId(rs.getInt("ID_Fournisseur"));
                f.setNom(rs.getString("Nom"));
                f.setAdresse(rs.getString("Adresse"));
                f.setTelephone(rs.getString("Telephone"));
                f.setEmail(rs.getString("Email"));
                fournisseurs.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fournisseurs;
    }

    // Ajoutez d'autres méthodes si nécessaire (getById, create, update, delete)
}