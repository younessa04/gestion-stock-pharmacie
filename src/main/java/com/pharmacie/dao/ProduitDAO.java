package com.pharmacie.dao;

import com.pharmacie.entities.*;
import com.pharmacie.util.ConnectDb;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {
    private Connection connection;

    public ProduitDAO() {
        this.connection = ConnectDb.getConnection();
    }

    public List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, f.Nom as NomFournisseur FROM Produit p LEFT JOIN Fournisseur f ON p.ID_Fournisseur = f.ID_Fournisseur";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                produits.add(mapProduit(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }

    public Produit getProduitById(int id) {
        String sql = "SELECT p.*, f.Nom as NomFournisseur FROM Produit p LEFT JOIN Fournisseur f ON p.ID_Fournisseur = f.ID_Fournisseur WHERE p.ID_Produit = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapProduit(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addProduit(Produit produit) {
        String sql = "INSERT INTO Produit (Nom, CodeBarre, Forme, Dosage, PrixVente, StockActuel, StockMin, DateExpiration, ID_Fournisseur) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setProduitParameters(pstmt, produit);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        produit.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateProduit(Produit produit) {
        String sql = "UPDATE Produit SET Nom=?, CodeBarre=?, Forme=?, Dosage=?, PrixVente=?, StockActuel=?, StockMin=?, DateExpiration=?, ID_Fournisseur=? WHERE ID_Produit=?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setProduitParameters(pstmt, produit);
            pstmt.setInt(10, produit.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    

    public boolean deleteProduit(int id) {
        Connection conn = null;
        try {
            conn = ConnectDb.getConnection();
            conn.setAutoCommit(false); // Désactive l'autocommit pour gérer la transaction
            
            // 1. D'abord supprimer les lignes de vente associées
            String deleteLignesVente = "DELETE FROM LigneVente WHERE ID_Produit = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteLignesVente)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            
            // 2. Ensuite supprimer les lignes d'achat associées
            String deleteLignesAchat = "DELETE FROM LigneAchat WHERE ID_Produit = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteLignesAchat)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            
            // 3. Finalement supprimer le produit
            String deleteProduit = "DELETE FROM Produit WHERE ID_Produit = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteProduit)) {
                ps.setInt(1, id);
                int affectedRows = ps.executeUpdate();
                
                conn.commit(); // Valide la transaction
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Annule en cas d'erreur
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Réactive l'autocommit
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public boolean hasAchatDependencies(int productId) {
        String sql = "SELECT COUNT(*) FROM LigneAchat WHERE ID_Produit = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasVenteDependencies(int productId) {
        String sql = "SELECT COUNT(*) FROM LigneVente WHERE ID_Produit = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Produit mapProduit(ResultSet rs) throws SQLException {
        Produit produit = new Produit();
        produit.setId(rs.getInt("ID_Produit"));
        produit.setNom(rs.getString("Nom"));
        produit.setCodeBarre(rs.getString("CodeBarre"));
        produit.setForme(rs.getString("Forme"));
        produit.setDosage(rs.getString("Dosage"));
        produit.setPrixVente(rs.getDouble("PrixVente"));
        produit.setStockActuel(rs.getInt("StockActuel"));
        produit.setStockMin(rs.getInt("StockMin"));
        produit.setDateExpiration(rs.getDate("DateExpiration"));
        produit.setIdFournisseur(rs.getInt("ID_Fournisseur"));
        produit.setNomFournisseur(rs.getString("NomFournisseur"));
        return produit;
    }

    private void setProduitParameters(PreparedStatement pstmt, Produit produit) throws SQLException {
        pstmt.setString(1, produit.getNom());
        pstmt.setString(2, produit.getCodeBarre());
        pstmt.setString(3, produit.getForme());
        pstmt.setString(4, produit.getDosage());
        pstmt.setDouble(5, produit.getPrixVente());
        pstmt.setInt(6, produit.getStockActuel());
        pstmt.setInt(7, produit.getStockMin());
        pstmt.setDate(8, new java.sql.Date(produit.getDateExpiration().getTime()));
        pstmt.setInt(9, produit.getIdFournisseur());
    }
    
    
    public List<Produit> searchProduits(String searchTerm) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, f.Nom as NomFournisseur FROM Produit p " +
                     "LEFT JOIN Fournisseur f ON p.ID_Fournisseur = f.ID_Fournisseur " +
                     "WHERE p.Nom LIKE ? OR p.CodeBarre LIKE ?";
        
        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String likeTerm = "%" + searchTerm + "%";
            pstmt.setString(1, likeTerm);
            pstmt.setString(2, likeTerm);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    produits.add(mapProduit(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }
}