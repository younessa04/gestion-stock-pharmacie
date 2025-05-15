package com.pharmacie.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectDb {

    private final String url = "jdbc:postgresql://aws-0-eu-west-2.pooler.supabase.com:6543/postgres";
    private final String username = "postgres.wndhyfijfzqqcoqeypev";
    private final String password = "Pharmacy1234@#YM2";

    private static Connection connection;

    private ConnectDb() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Connexion réussie !");
        } catch (Exception e) {
            System.out.println("❌ Erreur de connexion à la base de données");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        if (connection == null) {
            new ConnectDb();
        }
        return connection;
    }
}
