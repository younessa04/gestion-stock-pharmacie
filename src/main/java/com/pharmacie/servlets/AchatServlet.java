package com.pharmacie.servlets;

import com.pharmacie.dao.AchatDAO;
import com.pharmacie.dao.ProduitDAO;
import com.pharmacie.dao.FournisseurDAO; // Assurez-vous d'avoir un FournisseurDAO
import com.pharmacie.entities.Achat;
import com.pharmacie.entities.LigneAchat;
import com.pharmacie.entities.Produit;
import com.pharmacie.entities.Fournisseur; // Assurez-vous d'avoir une entité Fournisseur

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@WebServlet(name = "AchatServlet", urlPatterns = {"/achats"})
public class AchatServlet extends HttpServlet {
    private final AchatDAO achatDAO = new AchatDAO();
    private final ProduitDAO produitDAO = new ProduitDAO();
    private final FournisseurDAO fournisseurDAO = new FournisseurDAO(); // Instancier le DAO Fournisseur

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Charger les produits et fournisseurs pour les formulaires de création/édition
            List<Produit> produits = produitDAO.getAllProduits();
            request.setAttribute("produits", produits);

            List<Fournisseur> fournisseurs = fournisseurDAO.getAllFournisseurs();
            request.setAttribute("fournisseurs", fournisseurs);

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
            String action = request.getParameter("action");
            if ("add".equals(action)) {
                processAddAchat(request, response);
            } else {
                handleListRequest(request, response); // Action par défaut si non reconnue
            }
        } catch (Exception e) {
            handleError(request, response, "Erreur lors de l'opération POST: " + e.getMessage(), e);
        }
    }

    private void handleListRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        List<Achat> achats = achatDAO.getAllAchats();
        request.setAttribute("achats", achats);
        request.getRequestDispatcher("/achats.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        Achat nouvelAchat = new Achat();
        nouvelAchat.setDateAchat(new Date(System.currentTimeMillis())); // Date du jour
        request.setAttribute("achat", nouvelAchat);
        request.setAttribute("mode", "add");
        request.getRequestDispatcher("/addAchat.jsp").forward(request, response);
    }

    private void handleDetailsRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        int id = Integer.parseInt(request.getParameter("id"));
        Achat achat = achatDAO.getAchatById(id);
        if (achat != null) {
            request.setAttribute("achat", achat);
            request.getRequestDispatcher("/achatDetails.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Achat non trouvé avec l'ID : " + id);
            handleListRequest(request, response);
        }
    }

    private void handleDeleteRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        int id = Integer.parseInt(request.getParameter("id"));
        try {
            if (achatDAO.deleteAchat(id)) {
                response.sendRedirect(request.getContextPath() + "/achats?deleted=true");
            } else {
                request.setAttribute("error", "La suppression de l'achat a échoué.");
                handleListRequest(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Erreur lors de la suppression de l'achat : " + e.getMessage());
            handleListRequest(request, response);
        }
    }

    private void processAddAchat(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        try {
            Date dateAchat = Date.valueOf(request.getParameter("dateAchat"));
            int idFournisseur = Integer.parseInt(request.getParameter("idFournisseur"));

            Achat nouvelAchat = new Achat();
            nouvelAchat.setDateAchat(dateAchat);
            nouvelAchat.setIdFournisseur(idFournisseur);

            List<LigneAchat> lignesAchat = new ArrayList<>();
            String[] produitIds = request.getParameterValues("produitId");
            String[] quantities = request.getParameterValues("quantite");
            String[] prixAchats = request.getParameterValues("prixAchat");

            double montantTotal = 0.0;

            if (produitIds != null && produitIds.length > 0) {
                for (int i = 0; i < produitIds.length; i++) {
                    int productId = Integer.parseInt(produitIds[i]);
                    int quantite = Integer.parseInt(quantities[i]);
                    double prixAchat = Double.parseDouble(prixAchats[i]);

                    LigneAchat ligne = new LigneAchat();
                    ligne.setIdProduit(productId);
                    ligne.setQuantite(quantite);
                    ligne.setPrixAchat(prixAchat);
                    lignesAchat.add(ligne);

                    montantTotal += (quantite * prixAchat);
                }
            } else {
                 throw new Exception("Aucun produit sélectionné pour l'achat.");
            }

            nouvelAchat.setLignesAchat(lignesAchat);
            nouvelAchat.setMontantTotal(montantTotal);

            boolean added = achatDAO.addAchat(nouvelAchat);
            if (added) {
                response.sendRedirect(request.getContextPath() + "/achats?added=true");
            } else {
                request.setAttribute("error", "Échec de l'ajout de l'achat.");
                showAddForm(request, response);
            }

        } catch (NumberFormatException e) {
            handleError(request, response, "Données de formulaire invalides (numérique attendu) : " + e.getMessage(), e);
        } catch (Exception e) {
            handleError(request, response, "Erreur lors de l'ajout de l'achat : " + e.getMessage(), e);
        }
    }

    private void handleSearchRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        String searchTerm = request.getParameter("searchTerm");
        List<Achat> achats = new ArrayList<>();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            try {
                int idAchat = Integer.parseInt(searchTerm);
                achats = achatDAO.searchAchatsById(idAchat);
                if (achats.isEmpty()) {
                    request.setAttribute("warning", "Aucun achat trouvé avec l'ID : " + searchTerm);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("warning", "Le terme de recherche doit être un ID d'achat valide.");
            }
        } else {
            request.setAttribute("warning", "Veuillez entrer un terme de recherche.");
            handleListRequest(request, response);
            return;
        }

        request.setAttribute("achats", achats);
        request.setAttribute("searchTerm", searchTerm);
        request.getRequestDispatcher("/achats.jsp").forward(request, response);
    }


    private void handleError(HttpServletRequest request, HttpServletResponse response,
                             String errorMessage, Exception e)
            throws ServletException, IOException {
        e.printStackTrace();
        request.setAttribute("error", errorMessage);

        request.setAttribute("produits", produitDAO.getAllProduits());
		request.setAttribute("fournisseurs", fournisseurDAO.getAllFournisseurs());

        if ("add".equals(request.getParameter("action")) || request.getMethod().equalsIgnoreCase("POST")) {
            Achat achatEnCours = new Achat();
            try {
                achatEnCours.setDateAchat(Date.valueOf(request.getParameter("dateAchat")));
                achatEnCours.setIdFournisseur(Integer.parseInt(request.getParameter("idFournisseur")));
                
                List<LigneAchat> lignesSaisies = new ArrayList<>();
                String[] produitIds = request.getParameterValues("produitId");
                String[] quantities = request.getParameterValues("quantite");
                String[] prixAchats = request.getParameterValues("prixAchat");

                if (produitIds != null) {
                    for (int i = 0; i < produitIds.length; i++) {
                        LigneAchat ligne = new LigneAchat();
                        try {
                            ligne.setIdProduit(Integer.parseInt(produitIds[i]));
                            ligne.setQuantite(Integer.parseInt(quantities[i]));
                            ligne.setPrixAchat(Double.parseDouble(prixAchats[i]));
                        } catch (NumberFormatException ignored) {
                        }
                        lignesSaisies.add(ligne);
                    }
                }
                achatEnCours.setLignesAchat(lignesSaisies);

            } catch (Exception exForm) {
                achatEnCours = new Achat();
                achatEnCours.setDateAchat(new Date(System.currentTimeMillis()));
            }
            request.setAttribute("achat", achatEnCours);
            request.setAttribute("mode", "add");
            request.getRequestDispatcher("/addAchat.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/achats.jsp").forward(request, response);
        }
    }
}