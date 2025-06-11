<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Enregistrer un Achat</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        .container { margin-top: 20px; }
        .alert { margin-top: 10px; }
    </style>
</head>
<body>
    <div class="container">
        <h2>Enregistrer un Nouvel Achat</h2>

        <c:if test="${not empty requestScope.error}">
            <div class="alert alert-danger" role="alert">
                ${requestScope.error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/achats?action=add" method="post">
            <div class="form-group">
                <label for="dateAchat">Date de l'Achat:</label>
                <input type="date" class="form-control" id="dateAchat" name="dateAchat" value="${achat.dateAchat}" required>
            </div>

            <div class="form-group">
                <label for="idFournisseur">Fournisseur:</label>
                <select class="form-control" id="idFournisseur" name="idFournisseur" required>
                    <option value="">Sélectionnez un fournisseur</option>
                    <c:forEach var="fournisseur" items="${requestScope.fournisseurs}">
                        <option value="${fournisseur.id}" <c:if test="${fournisseur.id eq achat.idFournisseur}">selected</c:if>>${fournisseur.nom}</option>
                    </c:forEach>
                </select>
            </div>

            <h3>Produits de l'Achat</h3>
            <div id="produitsContainer">
                <!-- Les lignes de produits seront ajoutées ici par JavaScript -->
                <c:if test="${not empty achat.lignesAchat}">
                    <c:forEach var="ligneExistante" items="${achat.lignesAchat}" varStatus="loop">
                        <div class="form-row mb-2 border p-2">
                            <div class="col-md-5">
                                <label>Produit:</label>
                                <select name="produitId" class="form-control" onchange="updatePrixAchat(this)" required>
                                    <option value="">Sélectionnez un produit</option>
                                    <c:forEach var="produit" items="${requestScope.produits}">
                                        <option value="${produit.id}" data-prix-achat="${produit.prixVente}">${produit.nom} (Prix vente: ${produit.prixVente})</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <label>Quantité:</label>
                                <input type="number" name="quantite" class="form-control" value="${ligneExistante.quantite}" min="1" required>
                            </div>
                            <div class="col-md-3">
                                <label>Prix Achat Unitaire:</label>
                                <input type="number" name="prixAchat" class="form-control" step="0.01" value="${ligneExistante.prixAchat}" required>
                            </div>
                            <div class="col-md-1 d-flex align-items-end">
                                <button type="button" class="btn btn-danger btn-sm" onclick="supprimerLigneProduit(this)">X</button>
                            </div>
                        </div>
                    </c:forEach>
                </c:if>
            </div>
            <button type="button" class="btn btn-secondary mb-3" onclick="ajouterLigneProduit()">Ajouter un produit</button>

            <button type="submit" class="btn btn-success">Enregistrer l'Achat</button>
            <a href="${pageContext.request.contextPath}/achats" class="btn btn-outline-secondary">Annuler</a>
        </form>
    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.4/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

    <script>
        let ligneProduitCount = ${not empty achat.lignesAchat ? achat.lignesAchat.size() : 0};

        function ajouterLigneProduit() {
            const container = document.getElementById('produitsContainer');
            const newDiv = document.createElement('div');
            newDiv.classList.add('form-row', 'mb-2', 'border', 'p-2');
            newDiv.innerHTML = `
                <div class="col-md-5">
                    <label>Produit:</label>
                    <select name="produitId" class="form-control" onchange="updatePrixAchat(this)" required>
                        <option value="">Sélectionnez un produit</option>
                        <c:forEach var="produit" items="${requestScope.produits}">
                            <option value="${produit.id}" data-prix-achat="${produit.prixVente}">${produit.nom} (Prix vente: ${produit.prixVente})</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-3">
                    <label>Quantité:</label>
                    <input type="number" name="quantite" class="form-control" value="1" min="1" required>
                </div>
                <div class="col-md-3">
                    <label>Prix Achat Unitaire:</label>
                    <input type="number" name="prixAchat" class="form-control" step="0.01" required>
                </div>
                <div class="col-md-1 d-flex align-items-end">
                    <button type="button" class="btn btn-danger btn-sm" onclick="supprimerLigneProduit(this)">X</button>
                </div>
            `;
            container.appendChild(newDiv);
            ligneProduitCount++;
        }

        function supprimerLigneProduit(button) {
            button.closest('.form-row').remove();
        }

        function updatePrixAchat(selectElement) {
            // Optionnel: peut pré-remplir le prix d'achat avec le prix de vente du produit
            // ou laisser l'utilisateur entrer le prix d'achat réel.
            // Actuellement, le champ prixAchat est modifiable par l'utilisateur.
            // Si vous voulez le pré-remplir avec le prix de vente:
            // const selectedOption = selectElement.options[selectElement.selectedIndex];
            // const prixAchatInput = selectElement.closest('.form-row').querySelector('input[name="prixAchat"]');
            // if (selectedOption.dataset.prixAchat) {
            //     prixAchatInput.value = parseFloat(selectedOption.dataset.prixAchat).toFixed(2);
            // } else {
            //     prixAchatInput.value = '';
            // }
        }

        document.addEventListener('DOMContentLoaded', function() {
            if (ligneProduitCount === 0) {
                ajouterLigneProduit();
            } else {
                // Pour les lignes existantes, assurez-vous que les valeurs sont correctement affichées.
                // Si vous aviez un pré-remplissage du prix d'achat comme le prix de vente, ce serait ici.
            }
        });
    </script>
</body>
</html>