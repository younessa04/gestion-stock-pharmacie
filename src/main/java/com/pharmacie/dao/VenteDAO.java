package com.pharmacie.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pharmacie.entities.LigneVente;
import com.pharmacie.entities.Vente;
import com.pharmacie.util.ConnectDb;

public class VenteDAO {
    private static final Logger LOGGER = Logger.getLogger(VenteDAO.class.getName());
    private final Connection connection;

    public VenteDAO() {
        this.connection = ConnectDb.getConnection();
    }

    public boolean addVente(Vente vente) throws SQLException {
        String sqlVente = "INSERT INTO vente (datevente, montanttotal) VALUES (?, ?)";
        String sqlLigneVente = "INSERT INTO lignevente (id_vente, id_produit, quantite, prixvente) VALUES (?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE produit SET stockactuel = stockactuel - ? WHERE id_produit = ? AND stockactuel >= ?";

        Connection conn = null;
        PreparedStatement pstmtVente = null;
        PreparedStatement pstmtLigneVente = null;
        PreparedStatement pstmtUpdateStock = null;
        boolean success = false;

        try {
            conn = ConnectDb.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert the sale
            pstmtVente = conn.prepareStatement(sqlVente, Statement.RETURN_GENERATED_KEYS);
            pstmtVente.setDate(1, vente.getDateVente());
            pstmtVente.setDouble(2, vente.getMontantTotal());
            
            int affectedRows = pstmtVente.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating sale failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmtVente.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vente.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating sale failed, no ID obtained.");
                }
            }

            // 2. Insert line items and update stock
            pstmtLigneVente = conn.prepareStatement(sqlLigneVente);
            pstmtUpdateStock = conn.prepareStatement(sqlUpdateStock);
            
            for (LigneVente ligne : vente.getLignesVente()) {
                // Insert line item
                pstmtLigneVente.setInt(1, vente.getId());
                pstmtLigneVente.setInt(2, ligne.getIdProduit());
                pstmtLigneVente.setInt(3, ligne.getQuantite());
                pstmtLigneVente.setDouble(4, ligne.getPrixUnitaire());
                pstmtLigneVente.addBatch();

                // Update stock with check
                pstmtUpdateStock.setInt(1, ligne.getQuantite());
                pstmtUpdateStock.setInt(2, ligne.getIdProduit());
                pstmtUpdateStock.setInt(3, ligne.getQuantite());
                int stockUpdated = pstmtUpdateStock.executeUpdate();
                
                if (stockUpdated == 0) {
                    throw new SQLException("Stock update failed for product ID: " + ligne.getIdProduit());
                }
            }
            pstmtLigneVente.executeBatch();

            conn.commit();
            success = true;
            LOGGER.log(Level.INFO, "Sale added successfully");

        } catch (SQLException e) {
            rollbackSilently(conn);
            LOGGER.log(Level.SEVERE, "Error adding sale", e);
            throw e;
        } finally {
            // Close resources in reverse order
            closeSilently(pstmtUpdateStock);
            closeSilently(pstmtLigneVente);
            closeSilently(pstmtVente);
            closeSilently(conn);
        }
        return success;
    }

    private void closeSilently(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error closing resource", e);
            }
        }
    }
    public Vente getVenteById(int id) {
        Vente vente = null;
        String sqlVente = "SELECT * FROM vente WHERE id_vente = ?";
        
        try (PreparedStatement pstmtVente = connection.prepareStatement(sqlVente)) {
            pstmtVente.setInt(1, id);
            try (ResultSet rsVente = pstmtVente.executeQuery()) {
                if (rsVente.next()) {
                    vente = mapVente(rsVente);
                    vente.setLignesVente(getLignesVenteByVenteId(id));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving sale ID: " + id, e);
        }
        return vente;
    }

    private List<LigneVente> getLignesVenteByVenteId(int idVente) {
        List<LigneVente> lignesVente = new ArrayList<>();
        String sql = "SELECT lv.*, p.nom as nomproduit FROM lignevente lv " +
                     "LEFT JOIN produit p ON lv.id_produit = p.id_produit " +
                     "WHERE lv.id_vente = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idVente);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lignesVente.add(mapLigneVente(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving line items for sale ID: " + idVente, e);
        }
        return lignesVente;
    }

    public List<Vente> getAllVentes() {
        List<Vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM vente ORDER BY datevente DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ventes.add(mapVente(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all sales", e);
        }
        return ventes;
    }

    public boolean deleteVente(int id) throws SQLException {
        String sqlDeleteLignes = "DELETE FROM lignevente WHERE id_vente = ?";
        String sqlDeleteVente = "DELETE FROM vente WHERE id_vente = ?";

        Connection conn = null;
        boolean success = false;

        try {
            conn = ConnectDb.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtLignes = conn.prepareStatement(sqlDeleteLignes)) {
                pstmtLignes.setInt(1, id);
                pstmtLignes.executeUpdate();
            }

            try (PreparedStatement pstmtVente = conn.prepareStatement(sqlDeleteVente)) {
                pstmtVente.setInt(1, id);
                success = pstmtVente.executeUpdate() > 0;
            }

            conn.commit();
            LOGGER.log(Level.INFO, "Sale ID {0} and its line items deleted successfully.", id);

        } catch (SQLException e) {
            rollbackSilently(conn);
            LOGGER.log(Level.SEVERE, "Error deleting sale ID: " + id, e);
            throw e;
        } finally {
            closeSilently(conn);
        }
        return success;
    }
    
    public List<Vente> searchVentesById(int idVente) {
        List<Vente> ventes = new ArrayList<>();
        String sql = "SELECT * FROM vente WHERE id_vente = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idVente);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ventes.add(mapVente(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching sale by ID", e);
        }
        return ventes;
    }

    private Vente mapVente(ResultSet rs) throws SQLException {
        Vente vente = new Vente();
        vente.setId(rs.getInt("id_vente"));
        vente.setDateVente(rs.getDate("datevente"));
        vente.setMontantTotal(rs.getDouble("montanttotal"));
        return vente;
    }
    
    private LigneVente mapLigneVente(ResultSet rs) throws SQLException {
        LigneVente ligne = new LigneVente();
        ligne.setIdLigneVente(rs.getInt("id_lignevente"));
        ligne.setIdVente(rs.getInt("id_vente"));
        ligne.setIdProduit(rs.getInt("id_produit"));
        ligne.setQuantite(rs.getInt("quantite"));
        ligne.setPrixUnitaire(rs.getDouble("prixunitaire"));
        ligne.setNomProduit(rs.getString("nomproduit")); 
        return ligne;
    }

    private void rollbackSilently(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
                LOGGER.log(Level.INFO, "Transaction rolled back");
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error during rollback", ex);
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
                LOGGER.log(Level.SEVERE, "Error closing connection", e);
            }
        }
    }
}