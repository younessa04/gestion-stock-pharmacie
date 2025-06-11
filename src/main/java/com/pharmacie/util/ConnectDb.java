package com.pharmacie.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDb {

    private static final String URL = "jdbc:mysql://localhost:3306/pharmacy";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // Remplace par ton vrai mot de passe

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Pilote MySQL introuvable !");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.err.println("❌ Échec de la connexion à la base de données MySQL");
            e.printStackTrace();
            return null;
        }
    }
}
