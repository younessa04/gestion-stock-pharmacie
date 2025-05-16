<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Médicaments</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css">
</head>
<body>
    <div class="container mt-4">
        <h2>Liste des Médicaments</h2>
        <table class="table">
            <thead>
                <tr>
                    <th>Nom</th>
                    <th>Prix</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${produits}" var="p">
                    <tr>
                        <td>${p.nom}</td>
                        <td>${p.prixVente} €</td>
                        <td>
                            <a href="produits?action=edit&id=${p.id}" class="btn btn-warning">Modifier</a>
                            <a href="produits?action=delete&id=${p.id}" 
                               onclick="return confirm('Supprimer ce médicament?')" 
                               class="btn btn-danger">Supprimer</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <a href="produits?action=add" class="btn btn-primary">Ajouter</a>
    </div>
</body>
</html>