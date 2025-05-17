package com.pharmacie.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDb {

    private static final String URL = "jdbc:postgresql://aws-0-eu-west-2.pooler.supabase.com:6543/postgres";
    private static final String USERNAME = "postgres.wndhyfijfzqqcoqeypev";
    private static final String PASSWORD = "Pharmacy1234@#YM2";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Pilote PostgreSQL introuvable !");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.err.println("❌ Échec de la connexion à la base de données");
            e.printStackTrace();
            return null;
        }
    }
}
