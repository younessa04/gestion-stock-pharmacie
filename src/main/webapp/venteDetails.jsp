<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Détails de la Vente</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .details-container { max-width: 800px; margin: 30px auto; }
        .product-table { margin-top: 20px; }
        .back-btn { margin-top: 20px; }
    </style>
</head>
<body>
    <div class="container details-container">
        <h2>Détails de la Vente #${vente.id}</h2>
        
        <div class="card mt-4">
            <div class="card-header">
                <h5>Informations Générales</h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <p><strong>Date:</strong> ${vente.dateVente}</p>
                    </div>
                    <div class="col-md-6">
                        <p><strong>Montant Total:</strong> ${vente.montantTotal} DH</p>
                    </div>
                </div>
            </div>
        </div>

        <div class="card product-table">
            <div class="card-header">
                <h5>Produits Vendus</h5>
            </div>
            <div class="card-body">
                <table class="table">
                    <thead>
                        <tr>
                            <th>Produit</th>
                            <th>Quantité</th>
                            <th>Prix Unitaire</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${vente.lignesVente}" var="ligne">
                            <tr>
                                <td>${ligne.nomProduit}</td>
                                <td>${ligne.quantite}</td>
                                <td>${ligne.prixUnitaire} DH</td>
                                <td>${ligne.quantite * ligne.prixUnitaire} DH</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <a href="ventes" class="btn btn-secondary back-btn">Retour à la liste</a>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>