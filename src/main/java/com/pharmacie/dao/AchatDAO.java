package com.pharmacie.dao;

import com.pharmacie.entities.Achat;
import com.pharmacie.entities.LigneAchat;
import com.pharmacie.entities.Produit; // Pour la mise à jour du stock
import com.pharmacie.util.ConnectDb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AchatDAO {
    private Connection connection;

    public AchatDAO() {
        this.connection = ConnectDb.getConnection();
    }

    // Méthode pour ajouter un nouvel achat (avec transaction)
    public boolean addAchat(Achat achat) throws SQLException {
        String sqlAchat = "INSERT INTO Achat (date_achat, montant_total, id_fournisseur) VALUES (?, ?, ?)";
        String sqlLigneAchat = "INSERT INTO LigneAchat (id_achat, id_produit, quantite, prix_achat) VALUES (?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE Produit SET stockactuel = stockactuel + ? WHERE id_produit = ?";

        Connection conn = null;
        boolean success = false;

        try {
            conn = ConnectDb.getConnection();
            conn.setAutoCommit(false); // Début de la transaction

            // 1. Insérer l'achat
            try (PreparedStatement pstmtAchat = conn.prepareStatement(sqlAchat, Statement.RETURN_GENERATED_KEYS)) {
                pstmtAchat.setDate(1, achat.getDateAchat());
                pstmtAchat.setDouble(2, achat.getMontantTotal());
                pstmtAchat.setInt(3, achat.getIdFournisseur());
                int affectedRows = pstmtAchat.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("La création de l'achat a échoué, aucune ligne affectée.");
                }

                try (ResultSet generatedKeys = pstmtAchat.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        achat.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("La création de l'achat a échoué, aucun ID obtenu.");
                    }
                }
            }

            // 2. Insérer les lignes d'achat et mettre à jour le stock
            try (PreparedStatement pstmtLigneAchat = conn.prepareStatement(sqlLigneAchat);
                 PreparedStatement pstmtUpdateStock = conn.prepareStatement(sqlUpdateStock)) {
                for (LigneAchat ligne : achat.getLignesAchat()) {
                    // Insérer la ligne d'achat
                    pstmtLigneAchat.setInt(1, achat.getId());
                    pstmtLigneAchat.setInt(2, ligne.getIdProduit());
                    pstmtLigneAchat.setInt(3, ligne.getQuantite());
                    pstmtLigneAchat.setDouble(4, ligne.getPrixAchat());
                    pstmtLigneAchat.addBatch();

                    // Mettre à jour le stock du produit (incrémenter)
                    pstmtUpdateStock.setInt(1, ligne.getQuantite());
                    pstmtUpdateStock.setInt(2, ligne.getIdProduit());
                    pstmtUpdateStock.addBatch();
                }
                pstmtLigneAchat.executeBatch();
                pstmtUpdateStock.executeBatch();
            }

            conn.commit();
            success = true;
            System.out.println("✅ Achat et lignes d'achat ajoutés avec succès, stock mis à jour.");

        } catch (SQLException e) {
            rollbackSilently(conn);
            handleSQLException("Erreur lors de l'ajout de l'achat ou la mise à jour du stock", e);
            throw e;
        } finally {
            closeSilently(conn);
        }
        return success;
    }

    // Méthode pour obtenir un achat par son ID (avec ses lignes d'achat)
    public Achat getAchatById(int id) {
        Achat achat = null;
        // Jointure avec Fournisseur pour obtenir le nom
        String sqlAchat = "SELECT a.*, f.nom as nom_fournisseur FROM Achat a LEFT JOIN Fournisseur f ON a.id_fournisseur = f.id_fournisseur WHERE a.id_achat = ?";
        
        try (PreparedStatement pstmtAchat = connection.prepareStatement(sqlAchat)) {
            pstmtAchat.setInt(1, id);
            try (ResultSet rsAchat = pstmtAchat.executeQuery()) {
                if (rsAchat.next()) {
                    achat = mapAchat(rsAchat);
                    List<LigneAchat> lignesAchat = getLignesAchatByAchatId(id);
                    achat.setLignesAchat(lignesAchat);
                }
            }
        } catch (SQLException e) {
            handleSQLException("Erreur lors de la récupération de l'achat ID: " + id, e);
        }
        return achat;
    }

    // Méthode interne pour obtenir les lignes d'achat pour un achat donné
    private List<LigneAchat> getLignesAchatByAchatId(int idAchat) {
        List<LigneAchat> lignesAchat = new ArrayList<>();
        // Jointure avec Produit pour obtenir le nom
        String sql = "SELECT la.*, p.nom as nom_produit FROM LigneAchat la LEFT JOIN Produit p ON la.id_produit = p.id_produit WHERE la.id_achat = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idAchat);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lignesAchat.add(mapLigneAchat(rs));
                }
            }
        } catch (SQLException e) {
            handleSQLException("Erreur lors de la récupération des lignes d'achat pour l'achat ID: " + idAchat, e);
        }
        return lignesAchat;
    }

    // Méthode pour obtenir tous les achats
    public List<Achat> getAllAchats() {
        List<Achat> achats = new ArrayList<>();
        // Jointure avec Fournisseur pour obtenir le nom
        String sql = "SELECT a.*, f.nom as nom_fournisseur FROM Achat a LEFT JOIN Fournisseur f ON a.id_fournisseur = f.id_fournisseur ORDER BY a.date_achat DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Achat achat = mapAchat(rs);
                achats.add(achat);
            }
        } catch (SQLException e) {
            handleSQLException("Erreur lors de la récupération de tous les achats", e);
        }
        return achats;
    }

    // Méthode pour supprimer un achat (et ses lignes d'achat associées)
    public boolean deleteAchat(int id) throws SQLException {
        String sqlDeleteLignes = "DELETE FROM LigneAchat WHERE id_achat = ?";
        String sqlDeleteAchat = "DELETE FROM Achat WHERE id_achat = ?";

        Connection conn = null;
        boolean success = false;

        try {
            conn = ConnectDb.getConnection();
            conn.setAutoCommit(false);

            // 1. Supprimer les lignes d'achat
            try (PreparedStatement pstmtLignes = conn.prepareStatement(sqlDeleteLignes)) {
                pstmtLignes.setInt(1, id);
                pstmtLignes.executeUpdate();
            }

            // 2. Supprimer l'achat
            try (PreparedStatement pstmtAchat = conn.prepareStatement(sqlDeleteAchat)) {
                pstmtAchat.setInt(1, id);
                success = pstmtAchat.executeUpdate() > 0;
            }

            conn.commit();
            System.out.println("✅ Achat ID " + id + " et ses lignes supprimés avec succès.");

        } catch (SQLException e) {
            rollbackSilently(conn);
            handleSQLException("Erreur lors de la suppression de l'achat ID: " + id, e);
            throw e;
        } finally {
            closeSilently(conn);
        }
        return success;
    }
    
    // Méthode pour rechercher des achats (exemple par ID)
    public List<Achat> searchAchatsById(int idAchat) {
        List<Achat> achats = new ArrayList<>();
        String sql = "SELECT a.*, f.nom as nom_fournisseur FROM Achat a LEFT JOIN Fournisseur f ON a.id_fournisseur = f.id_fournisseur WHERE a.id_achat = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idAchat);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Achat achat = mapAchat(rs);
                    achats.add(achat);
                }
            }
        } catch (SQLException e) {
            handleSQLException("Erreur lors de la recherche de l'achat par ID", e);
        }
        return achats;
    }

    // Méthodes de mapping pour les objets
    private Achat mapAchat(ResultSet rs) throws SQLException {
        Achat achat = new Achat();
        achat.setId(rs.getInt("id_achat"));
        achat.setDateAchat(rs.getDate("date_achat"));
        achat.setMontantTotal(rs.getDouble("montant_total"));
        achat.setIdFournisseur(rs.getInt("id_fournisseur"));
        achat.setNomFournisseur(rs.getString("nom_fournisseur")); // Récupérer le nom du fournisseur
        return achat;
    }

    private LigneAchat mapLigneAchat(ResultSet rs) throws SQLException {
        LigneAchat ligne = new LigneAchat();
        ligne.setIdLigneAchat(rs.getInt("id_ligne_achat"));
        ligne.setIdAchat(rs.getInt("id_achat"));
        ligne.setIdProduit(rs.getInt("id_produit"));
        ligne.setQuantite(rs.getInt("quantite"));
        ligne.setPrixAchat(rs.getDouble("prix_achat"));
        ligne.setNomProduit(rs.getString("nom_produit")); // Récupérer le nom du produit
        return ligne;
    }

    // Méthodes utilitaires pour la gestion des erreurs et des connexions
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