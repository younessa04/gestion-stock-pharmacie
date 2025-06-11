<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ajouter une Vente</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .form-container { max-width: 800px; margin: 30px auto; }
        .product-row { margin-bottom: 15px; padding: 15px; border: 1px solid #dee2e6; border-radius: 5px; }
        .add-product-btn { margin-bottom: 20px; }
        .total-section { font-size: 1.2em; font-weight: bold; margin: 20px 0; }
    </style>
</head>
<body>
    <div class="container form-container">
        <h2 class="mb-4">Nouvelle Vente</h2>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form action="ventes" method="post">
            <input type="hidden" name="action" value="add">
            
            <div class="mb-3">
                <label for="dateVente" class="form-label">Date de Vente</label>
                <input type="date" class="form-control" id="dateVente" name="dateVente" 
                       value="${vente.dateVente}" required>
            </div>

            <h4 class="mt-4">Produits</h4>
            <div id="products-container">
                <c:forEach items="${vente.lignesVente}" var="ligne" varStatus="loop">
                    <div class="product-row row">
	                       <div class="col-md-5">
						    <label class="form-label">Produit</label>
						    <select class="form-select product-select" name="produitId" required>
						        <option value="">Sélectionner un produit</option>
						        <c:forEach items="${produits}" var="produit">
						            <option value="${produit.id}">
						                ${produit.nom} (Stock: ${produit.stockActuel})
						            </option>
						        </c:forEach>
						    </select>
						    
						    <%-- Debug output - fixed version --%>
							<div class="text-muted small mt-1">
							    ${empty produits ? 'Aucun produit disponible' : produits.size()} produits chargés
							</div>
						</div>
                        <div class="col-md-2">
                            <label class="form-label">Quantité</label>
                            <input type="number" class="form-control quantity" name="quantite" 
                                   min="1" value="${ligne.quantite}" required>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Prix Unitaire (DH)</label>
                            <input type="number" step="0.1" class="form-control price" name="prixUnitaire" 
                                   min="0.01" value="${ligne.prixUnitaire}" required>
                        </div>
                        <div class="col-md-2 d-flex align-items-end">
                            <button type="button" class="btn btn-danger btn-sm remove-product">Supprimer</button>
                        </div>
                    </div>
                </c:forEach>
                <c:if test="${empty vente.lignesVente}">
                    <div class="product-row row">
                        <div class="col-md-5">
                            <label class="form-label">Produit</label>
                            <select class="form-select product-select" name="produitId" required>
							    <option value="">Sélectionner un produit</option>
							    <c:forEach items="${produits}" var="produit">
							        <option value="${produit.id}" data-prix="${produit.prixVente}">
									    ${produit.nom} (Stock: ${produit.stockActuel})
									</option>
							    </c:forEach>
							</select>
                        <div class="col-md-2">
                            <label class="form-label">Quantité</label>
                            <input type="number" class="form-control quantity" name="quantite" min="1" value="1" required>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Prix Unitaire (DH)</label>
                            <input type="number" step="0.01" class="form-control price" name="prixUnitaire" min="0.01"  required>
                        </div>
                        <div class="col-md-2 d-flex align-items-end">
                            <button type="button" class="btn btn-danger btn-sm remove-product">Supprimer</button>
                        </div>
                    </div>
                </c:if>
            </div>

            <button type="button" id="add-product" class="btn btn-secondary add-product-btn">Ajouter un Produit</button>

            <div class="total-section">
                Total: <span id="total-amount">0.00</span> DH
            </div>

            <div class="d-flex justify-content-between mt-4">
                <a href="ventes" class="btn btn-outline-secondary">Annuler</a>
                <button type="submit" class="btn btn-primary">Enregistrer la Vente</button>
            </div>
        </form>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Add product row
            document.getElementById('add-product').addEventListener('click', function() {
                const newRow = document.querySelector('.product-row').cloneNode(true);
                const inputs = newRow.querySelectorAll('input');
                inputs.forEach(input => input.value = input.type === 'number' ? '1' : '');
                newRow.querySelector('.product-select').value = '';
                document.getElementById('products-container').appendChild(newRow);
                updateTotal();
            });

            // Remove product row
            document.addEventListener('click', function(e) {
                if (e.target.classList.contains('remove-product')) {
                    if (document.querySelectorAll('.product-row').length > 1) {
                        e.target.closest('.product-row').remove();
                        updateTotal();
                    } else {
                        alert('Une vente doit avoir au moins un produit.');
                    }
                }
            });

            // Update total when quantities or prices change
            document.addEventListener('input', function(e) {
                if (e.target.classList.contains('quantity') || e.target.classList.contains('price')) {
                    updateTotal();
                }
            });

            // Initialize total
            updateTotal();

            // Set today's date as default if empty
            const dateInput = document.getElementById('dateVente');
            if (!dateInput.value) {
                const today = new Date().toISOString().split('T')[0];
                dateInput.value = today;
            }
        });

        function updateTotal() {
            let total = 0;
            document.querySelectorAll('.product-row').forEach(row => {
                const quantity = parseFloat(row.querySelector('.quantity').value) || 0;
                const price = parseFloat(row.querySelector('.price').value) || 0;
                total += quantity * price;
            });
            document.getElementById('total-amount').textContent = total.toFixed(2);
        }
     // Mettre à jour le prix unitaire lors de la sélection d'un produit
        document.addEventListener('change', function(e) {
            if (e.target.classList.contains('product-select')) {
                const selectedOption = e.target.options[e.target.selectedIndex];
                const prix = selectedOption.getAttribute('data-prix');
                const productRow = e.target.closest('.product-row');
                const priceInput = productRow.querySelector('.price');
                if (prix && priceInput) {
                    priceInput.value = prix;
                    updateTotal();
                }
            }
        });

    </script>
</body>
</html>