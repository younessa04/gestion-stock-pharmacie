<%@ page session="true" %>
<%
    String nom = (String) session.getAttribute("utilisateur");
    if (nom == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Gestion des Médicaments</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        :root {
            --pharma-blue: #0077be;
            --pharma-light-blue: #e6f2ff;
            --pharma-green: #28a745;
            --pharma-white: #f8f9fa;
        }
        
        body {
            background-color: var(--pharma-white);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .container {
            max-width: 1200px;
        }
        
        h2 {
            color: var(--pharma-blue);
            border-bottom: 2px solid var(--pharma-blue);
            padding-bottom: 10px;
            margin-bottom: 25px;
            font-weight: 600;
        }
        
        .table {
            background-color: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }
        
        .table thead {
            background-color: var(--pharma-blue);
            color: white;
        }
        
        .table th {
            font-weight: 500;
            text-transform: uppercase;
            font-size: 0.85rem;
            letter-spacing: 0.5px;
        }
        
        .table td, .table th {
            vertical-align: middle;
            padding: 15px;
        }
        
        .btn-primary {
            background-color: var(--pharma-blue);
            border-color: var(--pharma-blue);
            padding: 8px 20px;
            font-weight: 500;
            border-radius: 4px;
        }
        
        .btn-primary:hover {
            background-color: #0062a3;
            border-color: #0062a3;
        }
        
        .btn-warning {
            background-color: #ffc107;
            border-color: #ffc107;
            color: #212529;
            padding: 6px 12px;
            border-radius: 4px;
        }
        
        .btn-danger {
            padding: 6px 12px;
            border-radius: 4px;
        }
        
        .action-buttons .btn {
            margin-right: 5px;
            font-size: 0.85rem;
        }
        
        .med-icon {
            color: var(--pharma-blue);
            margin-right: 8px;
        }
        
        .header-section {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }
        
        .price-cell {
            font-weight: 600;
            color: var(--pharma-green);
        }
        
        tr:hover {
            background-color: var(--pharma-light-blue) !important;
        }
        
        /* Style pour la barre de recherche */
        .input-group {
            width: 300px;
            margin-right: 20px;
        }

        .input-group input {
            border-right: none;
        }

        .input-group .btn {
            border-left: none;
            background-color: white;
        }

        .input-group .btn:hover {
            background-color: var(--pharma-light-blue);
        }
        
        /* Style pour les boutons de la navbar */
        .navbar-buttons {
            display: flex;
            gap: 10px;
        }
    </style>
</head>
<body>

<nav class="navbar navbar-light bg-light mb-4">
    <div class="container-fluid">
        <span class="navbar-brand">
            <i class="fas fa-user-circle me-2"></i>
            <%= nom %>
        </span>
        
        <!-- Ajout de la zone de recherche -->
        <form class="d-flex" action="produits" method="get">
            <input type="hidden" name="action" value="search">
            <div class="input-group">
                <input type="text" 
                       class="form-control" 
                       name="searchTerm" 
                       placeholder="Rechercher un médicament..."
                       value="${param.searchTerm}">
                <button class="btn btn-outline-primary" type="submit">
                    <i class="fas fa-search"></i>
                </button>
            </div>
        </form>
        
        <div class="navbar-buttons">
            <a href="stock-report" class="btn btn-info">
                <i class="fas fa-chart-bar"></i> Rapports
            </a>
            <a href="${pageContext.request.contextPath}/acceuil.jsp" class="btn btn-outline-primary">
    <i class="fas fa-home"></i> Accueil
</a>
            <a href="logout" class="btn btn-outline-danger">
                <i class="fas fa-sign-out-alt"></i> Déconnexion
            </a>
        </div>
    </div>
</nav>

<div class="container mt-5">
    <div class="header-section">
        <h2><i class="fas fa-pills med-icon"></i>Gestion des Médicaments</h2>
        <a href="produits?action=add" class="btn btn-primary">
            <i class="fas fa-plus-circle"></i> Ajouter un médicament
        </a>
    </div>
    
    <div class="table-responsive">
        <table class="table table-hover">
            <thead>
                <tr>
                    <th><i class="fas fa-capsules"></i> Nom</th>
                    <th><i class="fas fa-tag"></i> Prix</th>
                    <th><i class="fas fa-tasks"></i> Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${produits}" var="p">
                    <tr>
                        <td>${p.nom}</td>
                        <td class="price-cell">${p.prixVente} €</td>
                        <td class="action-buttons">
                            <a href="produits?action=edit&id=${p.id}" 
                               class="btn btn-warning btn-sm"
                               title="Modifier">
                                <i class="fas fa-edit"></i> Modifier
                            </a>
                            <a href="produits?action=delete&id=${p.id}" 
                               onclick="return confirm('Êtes-vous sûr de vouloir supprimer ce médicament?')" 
                                    class="btn btn-danger btn-sm"
                               title="Supprimer">
                                <i class="fas fa-trash-alt"></i> Supprimer
                            </a>
                        </tr>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>