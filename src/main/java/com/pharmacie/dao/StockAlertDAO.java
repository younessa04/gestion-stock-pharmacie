package com.pharmacie.dao;

import com.pharmacie.entities.StockAlert;
import com.pharmacie.util.ConnectDb;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockAlertDAO {
    public void saveAlerts(List<StockAlert> alerts) {
        String sql = "INSERT INTO stock_alerts (produit_id, produit_nom, alert_type, message, alert_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (StockAlert alert : alerts) {
                pstmt.setInt(1, alert.getProduitId());
                pstmt.setString(2, alert.getProduitNom());
                pstmt.setString(3, alert.getAlertType());
                pstmt.setString(4, alert.getMessage());
                pstmt.setTimestamp(5, new Timestamp(alert.getAlertDate().getTime()));
                pstmt.setString(6, alert.getStatus());
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<StockAlert> getActiveAlerts() {
        List<StockAlert> alerts = new ArrayList<>();
        String sql = "SELECT * FROM stock_alerts WHERE status = 'ACTIVE' ORDER BY alert_date DESC";
        
        try (Connection conn = ConnectDb.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                StockAlert alert = new StockAlert();
                alert.setId(rs.getInt("id"));
                alert.setProduitId(rs.getInt("produit_id"));
                alert.setProduitNom(rs.getString("produit_nom"));
                alert.setAlertType(rs.getString("alert_type"));
                alert.setMessage(rs.getString("message"));
                alert.setAlertDate(rs.getTimestamp("alert_date"));
                alert.setStatus(rs.getString("status"));
                alerts.add(alert);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return alerts;
    }
}