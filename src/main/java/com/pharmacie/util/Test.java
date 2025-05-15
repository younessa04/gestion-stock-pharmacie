package com.pharmacie.util;

import java.sql.*;

public class Test {

    public static void main(String[] args) {
        Connection connection = ConnectDb.getConnection();

        if (connection != null) {
            System.out.println("✅ Connexion réussie à la base de données !");

            try {
                String myQuery = "SELECT Nom, Login, Role FROM Utilisateur";
                PreparedStatement ps = connection.prepareStatement(myQuery);
                ResultSet resultat = ps.executeQuery();

                while (resultat.next()) {
                    String nom = resultat.getString("Nom");
                    String login = resultat.getString("Login");
                    String role = resultat.getString("Role");

                    System.out.println(nom + " (" + login + ") - Rôle : " + role);
                }

                ps.close();
                connection.close();
            } catch (SQLException e) {
                System.out.println("❌ Erreur lors de l'exécution de la requête:");
                e.printStackTrace();
            }

        } else {
            System.out.println("❌ Échec de la connexion !");
        }
    }
}
