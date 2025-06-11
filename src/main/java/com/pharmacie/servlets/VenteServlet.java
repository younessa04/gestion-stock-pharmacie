package com.pharmacie.servlets;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
// Assurez-vous que cette importation est là
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pharmacie.dao.ProduitDAO;
import com.pharmacie.dao.VenteDAO;
import com.pharmacie.entities.LigneVente;
import com.pharmacie.entities.Produit;
import com.pharmacie.entities.Vente;

@SuppressWarnings("serial")
@WebServlet(name = "VenteServlet", urlPatterns = {"/ventes"})
public class VenteServlet extends HttpServlet {
    private final VenteDAO venteDAO = new VenteDAO();
    private final ProduitDAO produitDAO = new ProduitDAO();
    // UtilisateurDAO est retiré.

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Load products for dropdown
            List<Produit> produits = produitDAO.getAllProduits();
            request.setAttribute("produits", produits);

            String action = request.getParameter("action");

            if (action == null || action.isEmpty()) {
                handleListRequest(request, response);
                return;
            }

            switch (action) {
                case "add":
                    showAddForm(request, response);
                    break;
                case "details":
                    handleDetailsRequest(request, response);
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String action = request.getParameter("action");
            if ("add".equals(action)) {
                processAddVente(request, response);
            } else {
                handleListRequest(request, response);
            }
        } catch (Exception e) {
            handleError(request, response, "Erreur lors de l'opération POST: " + e.getMessage(), e);
        }
    }

    private void handleListRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        List<Vente> ventes = venteDAO.getAllVentes();
        request.setAttribute("ventes", ventes);
        request.getRequestDispatcher("/ventes.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        Vente nouvelleVente = new Vente();
        nouvelleVente.setDateVente(new Date(System.currentTimeMillis()));
        request.setAttribute("vente", nouvelleVente);
        request.setAttribute("mode", "add");
        request.getRequestDispatcher("/addVente.jsp").forward(request, response);
    }

    private void handleDetailsRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        int id = Integer.parseInt(request.getParameter("id"));
        Vente vente = venteDAO.getVenteById(id);
        if (vente != null) {
            request.setAttribute("vente", vente);
            request.getRequestDispatcher("/venteDetails.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Vente non trouvée avec l'ID : " + id);
            handleListRequest(request, response);
        }
    }

    private void handleDeleteRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        int id = Integer.parseInt(request.getParameter("id"));
        try {
            if (venteDAO.deleteVente(id)) {
                response.sendRedirect(request.getContextPath() + "/ventes?deleted=true");
            } else {
                request.setAttribute("error", "La suppression de la vente a échoué.");
                handleListRequest(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Erreur lors de la suppression de la vente : " + e.getMessage());
            handleListRequest(request, response);
        }
    }

    private void processAddVente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        try {
            // Récupérer les données de la vente
            Date dateVente = Date.valueOf(request.getParameter("dateVente"));
            // idUtilisateur est retiré.

            Vente nouvelleVente = new Vente();
            nouvelleVente.setDateVente(dateVente);

            // Récupérer les lignes de vente
            List<LigneVente> lignesVente = new ArrayList<>();
            String[] produitIds = request.getParameterValues("produitId");
            String[] quantities = request.getParameterValues("quantite");
            String[] prixUnitaires = request.getParameterValues("prixUnitaire");

            double montantTotal = 0.0;

            if (produitIds != null && produitIds.length > 0) {
                for (int i = 0; i < produitIds.length; i++) {
                    int productId = Integer.parseInt(produitIds[i]);
                    int quantite = Integer.parseInt(quantities[i]);
                    double prixUnitaire = Double.parseDouble(prixUnitaires[i]);

                    Produit produitEnStock = produitDAO.getProduitById(productId);
                    if (produitEnStock == null || produitEnStock.getStockActuel() < quantite) {
                        throw new Exception("Stock insuffisant pour le produit : " + (produitEnStock != null ? produitEnStock.getNom() : "ID " + productId) + ". Stock actuel : " + (produitEnStock != null ? produitEnStock.getStockActuel() : "N/A") + ", Quantité demandée : " + quantite);
                    }

                    LigneVente ligne = new LigneVente();
                    ligne.setIdProduit(productId);
                    ligne.setQuantite(quantite);
                    ligne.setPrixUnitaire(prixUnitaire);
                    lignesVente.add(ligne);

                    montantTotal += (quantite * prixUnitaire);
                }
            } else {
                 throw new Exception("Aucun produit sélectionné pour la vente.");
            }

            nouvelleVente.setLignesVente(lignesVente);
            nouvelleVente.setMontantTotal(montantTotal);

            boolean added = venteDAO.addVente(nouvelleVente);
            if (added) {
                response.sendRedirect(request.getContextPath() + "/ventes?added=true");
            } else {
                request.setAttribute("error", "Échec de l'ajout de la vente.");
                showAddForm(request, response);
            }

        } catch (NumberFormatException e) {
            handleError(request, response, "Données de formulaire invalides (numérique attendu) : " + e.getMessage(), e);
        } catch (Exception e) {
            handleError(request, response, "Erreur lors de l'ajout de la vente : " + e.getMessage(), e);
        }
    }

    private void handleSearchRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        String searchTerm = request.getParameter("searchTerm");
        List<Vente> ventes = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            try {
                int idVente = Integer.parseInt(searchTerm);
                ventes = venteDAO.searchVentesById(idVente);
                if (ventes.isEmpty()) {
                    request.setAttribute("warning", "Aucune vente trouvée avec l'ID : " + searchTerm);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("warning", "Le terme de recherche doit être un ID de vente valide.");
            }
        } else {
            request.setAttribute("warning", "Veuillez entrer un terme de recherche.");
            handleListRequest(request, response);
            return;
        }

        request.setAttribute("ventes", ventes);
        request.setAttribute("searchTerm", searchTerm);
        request.getRequestDispatcher("/ventes.jsp").forward(request, response);
    }


    private void handleError(HttpServletRequest request, HttpServletResponse response,
                             String errorMessage, Exception e)
            throws ServletException, IOException {
        e.printStackTrace();
        request.setAttribute("error", errorMessage);

        request.setAttribute("produits", produitDAO.getAllProduits());
		// UtilisateurDAO.getAllUtilisateurs() est retiré.

        if ("add".equals(request.getParameter("action")) || request.getMethod().equalsIgnoreCase("POST")) {
            Vente venteEnCours = new Vente();
            try {
                venteEnCours.setDateVente(Date.valueOf(request.getParameter("dateVente")));
                // idUtilisateur est retiré.
                
                List<LigneVente> lignesSaisies = new ArrayList<>();
                String[] produitIds = request.getParameterValues("produitId");
                String[] quantities = request.getParameterValues("quantite");
                String[] prixUnitaires = request.getParameterValues("prixUnitaire");

                if (produitIds != null) {
                    for (int i = 0; i < produitIds.length; i++) {
                        LigneVente ligne = new LigneVente();
                        try {
                            ligne.setIdProduit(Integer.parseInt(produitIds[i]));
                            ligne.setQuantite(Integer.parseInt(quantities[i]));
                            ligne.setPrixUnitaire(Double.parseDouble(prixUnitaires[i]));
                        } catch (NumberFormatException ignored) {
                        }
                        lignesSaisies.add(ligne);
                    }
                }
                venteEnCours.setLignesVente(lignesSaisies);

            } catch (Exception exForm) {
                venteEnCours = new Vente();
                venteEnCours.setDateVente(new Date(System.currentTimeMillis()));
            }
            request.setAttribute("vente", venteEnCours);
            request.setAttribute("mode", "add");
            request.getRequestDispatcher("/addVente.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/ventes.jsp").forward(request, response);
        }
    }
}