package com.pharmacie.util;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.pharmacie.util.ConnectDb;

public class TestInsertion {

    public static void main(String[] args) {
        String sql = "INSERT INTO Utilisateur (Nom, Login, MotDePasse, Role) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "Dupont");
            ps.setString(2, "dupont123");
            ps.setString(3, "password123");
            ps.setString(4, "Pharmacien");

            int rowsInserted = ps.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("✅ Insertion réussie !");
            } else {
                System.out.println("❌ Aucune ligne insérée.");
            }

        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'insertion :");
            e.printStackTrace();
        }
    }
}
