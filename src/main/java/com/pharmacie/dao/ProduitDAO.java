package com.pharmacie.dao;

import com.pharmacie.entities.Produit;
import com.pharmacie.util.ConnectDb;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {
    private Connection connection;

    public ProduitDAO() {
        this.connection = ConnectDb.getConnection();
    }

    // Méthode pour obtenir tous les produits
    public List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, f.Nom as NomFournisseur FROM Produit p LEFT JOIN Fournisseur f ON p.ID_Fournisseur = f.ID_Fournisseur";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                produits.add(mapProduit(rs));
            }
        } catch (SQLException e) {
            handleSQLException("Erreur lors de la récupération des produits", e);
        }
        return produits;
    }

    // Méthode pour obtenir un produit par son ID
    public Produit getProduitById(int id) {
        String sql = "SELECT p.*, f.Nom as NomFournisseur FROM Produit p LEFT JOIN Fournisseur f ON p.ID_Fournisseur = f.ID_Fournisseur WHERE p.ID_Produit = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapProduit(rs);
            }
        } catch (SQLException e) {
            handleSQLException("Erreur lors de la récupération du produit ID: " + id, e);
        }
        return null;
    }

    // Méthode pour ajouter un produit
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
            handleSQLException("Erreur lors de l'ajout du produit", e);
        }
        return false;
    }

    // Méthode pour mettre à jour un produit
    public boolean updateProduit(Produit produit) {
        String sql = "UPDATE Produit SET Nom=?, CodeBarre=?, Forme=?, Dosage=?, PrixVente=?, StockActuel=?, StockMin=?, DateExpiration=?, ID_Fournisseur=? WHERE ID_Produit=?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setProduitParameters(pstmt, produit);
            pstmt.setInt(10, produit.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleSQLException("Erreur lors de la mise à jour du produit ID: " + produit.getId(), e);
        }
        return false;
    }

    // Méthode pour supprimer un produit (version optimisée)
    public boolean deleteProduit(int id) {
        Connection conn = null;
        boolean isDeleted = false;
        
        try {
            conn = ConnectDb.getConnection();
            conn.setAutoCommit(false);

            // Désactiver le cache des prepared statements pour cette connexion
            if (conn.unwrap(org.postgresql.PGConnection.class) != null) {
                conn.unwrap(org.postgresql.PGConnection.class).setPrepareThreshold(-1);
            }

            // Suppression en cascade des dépendances
            deleteDependencies(conn, id);
            
            // Suppression du produit principal
            isDeleted = deleteProduct(conn, id);
            
            if (isDeleted) {
                conn.commit();
                System.out.println("✅ Produit ID " + id + " supprimé avec succès");
            } else {
                conn.rollback();
                System.out.println("⚠ Aucun produit trouvé avec ID " + id);
            }
        } catch (SQLException e) {
            rollbackSilently(conn);
            handleSQLException("Erreur lors de la suppression du produit ID: " + id, e);
        } finally {
            closeSilently(conn);
        }
        return isDeleted;
    }

    private void deleteDependencies(Connection conn, int productId) throws SQLException {
        String[] deleteQueries = {
            "DELETE FROM stock_alerts WHERE produit_id = ?",
            "DELETE FROM LigneVente WHERE ID_Produit = ?",
            "DELETE FROM LigneAchat WHERE ID_Produit = ?"
        };
        
        for (String sql : deleteQueries) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productId);
                int affectedRows = ps.executeUpdate();
                System.out.println("⌛ " + affectedRows + " dépendance(s) supprimée(s) - " + sql.split(" ")[3]);
            }
        }
    }

    private boolean deleteProduct(Connection conn, int productId) throws SQLException {
        String sql = "DELETE FROM Produit WHERE ID_Produit = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        }
    }

    // Méthodes utilitaires
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

    // Méthodes de vérification des dépendances
    public boolean hasDependencies(int productId) {
        return hasAchatDependencies(productId) || hasVenteDependencies(productId);
    }

    public boolean hasAchatDependencies(int productId) {
        return checkDependency("SELECT COUNT(*) FROM LigneAchat WHERE ID_Produit = ?", productId);
    }

    public boolean hasVenteDependencies(int productId) {
        return checkDependency("SELECT COUNT(*) FROM LigneVente WHERE ID_Produit = ?", productId);
    }

    private boolean checkDependency(String sql, int productId) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            handleSQLException("Erreur vérification dépendances", e);
            return false;
        }
    }

    // Méthodes de recherche
    public List<Produit> searchProduits(String searchTerm) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, f.Nom as NomFournisseur FROM Produit p " +
                     "LEFT JOIN Fournisseur f ON p.ID_Fournisseur = f.ID_Fournisseur " +
                     "WHERE p.Nom ILIKE ? OR p.CodeBarre ILIKE ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String likeTerm = "%" + searchTerm + "%";
            pstmt.setString(1, likeTerm);
            pstmt.setString(2, likeTerm);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    produits.add(mapProduit(rs));
                }
            }
        } catch (SQLException e) {
            handleSQLException("Erreur recherche produits", e);
        }
        return produits;
    }

    // Méthodes pour l'état du stock
    public List<Produit> getProduitsEnRupture() {
        return getProduitsByStockStatus("WHERE p.StockActuel <= 0");
    }

    public List<Produit> getProduitsStockFaible() {
        return getProduitsByStockStatus("WHERE p.StockActuel > 0 AND p.StockActuel <= p.StockMin");
    }

    public List<Produit> getProduitsStockNormal() {
        return getProduitsByStockStatus("WHERE p.StockActuel > p.StockMin");
    }

    private List<Produit> getProduitsByStockStatus(String whereClause) {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, f.Nom as NomFournisseur FROM Produit p " +
                     "LEFT JOIN Fournisseur f ON p.ID_Fournisseur = f.ID_Fournisseur " +
                     whereClause + " ORDER BY p.Nom";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                produits.add(mapProduit(rs));
            }
        } catch (SQLException e) {
            handleSQLException("Erreur récupération état stock", e);
        }
        return produits;
    }

    // Gestion des erreurs et des ressources
    private void handleSQLException(String message, SQLException e) {
        System.err.println("❌ " + message);
        e.printStackTrace();
    }

    private void rollbackSilently(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
                System.out.println("🔙 Transaction annulée");
            } catch (SQLException ex) {
                System.err.println("❌ Erreur lors du rollback: " + ex.getMessage());
            }
        }
    }

    private void closeSilently(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.getAutoCommit()) {
                    conn.setAutoCommit(true);
                }
                conn.close();
            } catch (SQLException e) {
                System.err.println("❌ Erreur fermeture connexion: " + e.getMessage());
            }
        }
    }
}