<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Détails de l'Achat</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        .container { margin-top: 20px; }
        .details-card { margin-top: 20px; }
    </style>
</head>
<body>
    <div class="container">
        <h2>Détails de l'Achat n° ${achat.id}</h2>

        <c:if test="${empty achat}">
            <div class="alert alert-warning" role="alert">
                Achat non trouvé.
            </div>
            <a href="${pageContext.request.contextPath}/achats" class="btn btn-secondary">Retour à la liste des achats</a>
            <hr>
            <%-- Empêche le reste du code JSP de s'exécuter si l'achat n'est pas trouvé --%>
            <c:set var="skipContent" value="true"/> 
        </c:if>

        <c:if test="${not skipContent}">
            <div class="card details-card">
                <div class="card-header">
                    Informations Générales
                </div>
                <div class="card-body">
                    <p><strong>Date de l'Achat:</strong> ${achat.dateAchat}</p>
                    <p><strong>Montant Total:</strong> <fmt:formatNumber value="${achat.montantTotal}" type="currency" currencySymbol="DH " minFractionDigits="2" maxFractionDigits="2"/></p>
                    <p><strong>Fournisseur:</strong> ${achat.nomFournisseur}</p>
                </div>
            </div>

            <div class="card details-card">
                <div class="card-header">
                    Produits Achetés
                </div>
                <div class="card-body">
                    <c:if test="${empty achat.lignesAchat}">
                        <p>Aucun produit enregistré pour cet achat.</p>
                    </c:if>
                    <c:if test="${not empty achat.lignesAchat}">
                        <table class="table table-bordered">
                            <thead class="thead-light">
                                <tr>
                                    <th>Produit</th>
                                    <th>Quantité</th>
                                    <th>Prix Achat Unitaire</th>
                                    <th>Sous-total</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="ligne" items="${achat.lignesAchat}">
                                    <tr>
                                        <td>${ligne.nomProduit}</td>
                                        <td>${ligne.quantite}</td>
                                        <td><fmt:formatNumber value="${ligne.prixAchat}" type="currency" currencySymbol="DH " minFractionDigits="2" maxFractionDigits="2"/></td>
                                        <td><fmt:formatNumber value="${ligne.quantite * ligne.prixAchat}" type="currency" currencySymbol="DH " minFractionDigits="2" maxFractionDigits="2"/></td>
                                    	                                    		
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:if>
                </div>
            </div>

            <div class="mt-4">
                <a href="${pageContext.request.contextPath}/achats" class="btn btn-secondary">Retour à la liste des achats</a>
            </div>
        </c:if>
    </div>
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.4/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>