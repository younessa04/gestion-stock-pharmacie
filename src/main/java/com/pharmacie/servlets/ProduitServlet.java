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
import java.util.List;

@WebServlet(name = "ProduitServlet", urlPatterns = {"/produits"})
public class ProduitServlet extends HttpServlet {
    private final ProduitDAO produitDAO = new ProduitDAO();
    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Chargement systématique des fournisseurs
            List<Fournisseur> fournisseurs = fournisseurDAO.getAllFournisseurs();
            request.setAttribute("fournisseurs", fournisseurs);

            String action = request.getParameter("action");
            
            if ("edit".equals(action)) {
                handleEditRequest(request, response);
            } else if ("add".equals(action)) {
                handleAddRequest(request, response);
            } else if ("delete".equals(action)) {
                handleDeleteRequest(request, response);
            } else {
                handleListRequest(request, response);
            }
        } catch (Exception e) {
            handleError(request, response, "Erreur lors du chargement: " + e.getMessage(), e);
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
        request.getRequestDispatcher("/editMedicament.jsp").forward(request, response);
    }

    private void handleAddRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("produit", new Produit());
        request.getRequestDispatcher("/editMedicament.jsp").forward(request, response);
    }

    private void handleDeleteRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        produitDAO.deleteProduit(id);
        response.sendRedirect("produits");
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
}