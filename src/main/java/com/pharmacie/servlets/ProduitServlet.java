package com.pharmacie.servlets;

import com.pharmacie.dao.ProduitDAO;
import com.pharmacie.dao.FournisseurDAO;
import com.pharmacie.entities.Produit;
import com.pharmacie.entities.Fournisseur;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ProduitServlet", urlPatterns = {"/produits"})
public class ProduitServlet extends HttpServlet {
    private final ProduitDAO produitDAO = new ProduitDAO();
    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Chargement systématique des fournisseurs (utilisé pour les formulaires)
            List<Fournisseur> fournisseurs = fournisseurDAO.getAllFournisseurs();
            request.setAttribute("fournisseurs", fournisseurs);

            String action = request.getParameter("action");
            
            if (action == null || action.isEmpty()) {
                // Action par défaut si aucun paramètre
                handleListRequest(request, response);
                return;
            }

            switch (action) {
                case "edit":
                    handleEditRequest(request, response);
                    break;
                    
                case "add":
                    handleAddRequest(request, response);
                    break;
                    
                case "delete":
                    handleDeleteRequest(request, response);
                    break;
                    
                case "search":
                    handleSearchRequest(request, response);
                    break;
                    
                default:
                    handleListRequest(request, response);
                    break;
            }
            
        } catch (NumberFormatException e) {
            handleError(request, response, "ID invalide : " + e.getMessage(), e);
            
        } catch (SQLException e) {
            handleError(request, response, "Erreur base de données : " + e.getMessage(), e);
            
        } catch (Exception e) {
            handleError(request, response, "Erreur inattendue : " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            Produit produit = createProduitFromRequest(request);
            
            if (produit.getId() > 0) {
                produitDAO.updateProduit(produit);
            } else {
                produitDAO.addProduit(produit);
            }
            response.sendRedirect("produits");
        } catch (Exception e) {
            handleError(request, response, "Erreur lors de l'enregistrement: " + e.getMessage(), e);
        }
    }

    private void handleEditRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Produit produit = produitDAO.getProduitById(id);
        
        request.setAttribute("produit", produit);
        request.setAttribute("mode", "edit"); // Ajoutez ce flag
        request.getRequestDispatcher("/editMedicament.jsp").forward(request, response);
    }

    private void handleAddRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Crée un nouveau produit vide
        Produit nouveauProduit = new Produit();
        nouveauProduit.setDateExpiration(new Date(System.currentTimeMillis())); // Date du jour par défaut
        
        request.setAttribute("produit", nouveauProduit);
        request.setAttribute("mode", "add"); // Ajoutez ce flag
        request.getRequestDispatcher("/editMedicament.jsp").forward(request, response);
    }

    private void handleDeleteRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        
        try {
            if (produitDAO.deleteProduit(id)) {
                // Ajoutez ceci pour forcer le rafraîchissement
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "0");
                
                // Redirection avec paramètre de succès
                response.sendRedirect(request.getContextPath() + "/produits?deleted=true");
            } else {
                request.setAttribute("error", "La suppression a échoué.");
                handleListRequest(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
            handleListRequest(request, response);
        }
    }

    private void handleListRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Produit> produits = produitDAO.getAllProduits();
        request.setAttribute("produits", produits);
        request.getRequestDispatcher("/medicament.jsp").forward(request, response);
    }

    private Produit createProduitFromRequest(HttpServletRequest request) {
        Produit produit = new Produit();
        
        if (request.getParameter("id") != null && !request.getParameter("id").isEmpty()) {
            produit.setId(Integer.parseInt(request.getParameter("id")));
        }
        
        produit.setNom(request.getParameter("nom"));
        produit.setCodeBarre(request.getParameter("codeBarre"));
        produit.setForme(request.getParameter("forme"));
        produit.setDosage(request.getParameter("dosage"));
        produit.setPrixVente(Double.parseDouble(request.getParameter("prix")));
        produit.setStockActuel(Integer.parseInt(request.getParameter("stock")));
        produit.setStockMin(Integer.parseInt(request.getParameter("stockMin")));
        produit.setDateExpiration(Date.valueOf(request.getParameter("dateExpiration")));
        produit.setIdFournisseur(Integer.parseInt(request.getParameter("fournisseur")));
        
        return produit;
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, 
                           String errorMessage, Exception e) 
            throws ServletException, IOException {
        e.printStackTrace();
        request.setAttribute("error", errorMessage);
        
        // Réafficher le formulaire avec les erreurs
        List<Fournisseur> fournisseurs = fournisseurDAO.getAllFournisseurs();
        request.setAttribute("fournisseurs", fournisseurs);
        
        if (request.getParameter("id") != null) {
            request.setAttribute("produit", createProduitFromRequest(request));
        }
        
        request.getRequestDispatcher("/editMedicament.jsp").forward(request, response);
    }
    
 // Méthodes supplémentaires à ajouter à votre servlet
    private void handleSearchRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        String searchTerm = request.getParameter("searchTerm");
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            request.setAttribute("warning", "Veuillez entrer un terme de recherche");
            handleListRequest(request, response);
            return;
        }
        
        List<Produit> produits = produitDAO.searchProduits(searchTerm);
        request.setAttribute("produits", produits);
        request.setAttribute("searchTerm", searchTerm); // Pour pré-remplir le champ
        
        request.getRequestDispatcher("/medicament.jsp").forward(request, response);
    }
    
    
    
    
    
}