<%@ page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String nom = (String) session.getAttribute("utilisateur");
    if (nom == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<html>
<head>
    <title>Gestion des Ventes</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        /* Définition des variables CSS pour ce fichier, si non définies dans _sidebar.jsp ou un fichier commun */
        :root {
            --pharma-blue: #0056b3;
            --pharma-green: #28a745;
            --pharma-light: #f8f9fa;
            --pharma-dark: #212529;
            --pharma-light-blue: #e6f2ff; /* Couleur pour le survol des lignes de tableau */
        }

        body {
            background-color: var(--pharma-light); /* Cohérence avec accueil.jsp */
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            /* padding-left est géré par _sidebar.jsp via la classe body */
        }
        
        .container-fluid {
            padding-right: 0;
            padding-left: 0;
        }

        .main-content {
            padding: 30px;
        }

        /* Styles de la Navbar spécifique à cette page (dans le main-content) */
        .navbar {
            background-color: var(--pharma-light) !important; /* Pour une nav bar légère */
            box-shadow: 0 2px 4px rgba(0,0,0,.05);
            margin-bottom: 25px; /* Espace sous la nav bar */
            border-radius: 8px; /* Coins arrondis pour la nav bar */
        }
        .navbar-brand {
            color: var(--pharma-dark);
            font-weight: 600;
        }
        .navbar-buttons {
            display: flex;
            gap: 10px;
        }
        .input-group {
            width: 300px;
            margin-right: 20px;
        }
        .input-group input {
            border-right: none;
            border-radius: 0.25rem 0 0 0.25rem; /* Coins arrondis à gauche seulement */
        }
        .input-group .btn {
            border-left: none;
            background-color: white;
            color: var(--pharma-blue);
            border-radius: 0 0.25rem 0.25rem 0; /* Coins arrondis à droite seulement */
        }
        .input-group .btn:hover {
            background-color: var(--pharma-light-blue);
        }

        /* Styles spécifiques au contenu des Ventes (table, boutons) */
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
        
        .btn-warning { /* Gardé au cas où, mais non utilisé directement ici */
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
        
        .btn-info { /* Style pour le bouton Détails */
            background-color: #17a2b8;
            border-color: #17a2b8;
            color: white;
            padding: 6px 12px;
            border-radius: 4px;
        }
        .btn-info:hover {
            background-color: #138496;
            border-color: #138496;
        }

        .action-buttons .btn {
            margin-right: 5px;
            font-size: 0.85rem;
        }
        
        tr:hover {
            background-color: var(--pharma-light-blue) !important;
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
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar Include -->
            <jsp:include page="/WEB-INF/fragments/_sidebar.jsp" />

            <!-- Main Content -->
            <div class="col-md-10 col-lg-9 main-content offset-md-3 offset-lg-2">
                
                <!-- Navbar pour les pages internes (similaire à medicament.jsp) -->
                <nav class="navbar navbar-light bg-light mb-4">
                    <div class="container-fluid">
                        <span class="navbar-brand">
                            <i class="fas fa-user-circle me-2"></i>
                            <%= nom %>
                        </span>
                        
                        <!-- Zone de recherche pour les ventes -->
                        <form class="d-flex" action="${pageContext.request.contextPath}/ventes" method="get">
                            <input type="hidden" name="action" value="search">
                            <div class="input-group">
                                <input type="text" 
                                       class="form-control" 
                                       name="searchTerm" 
                                       placeholder="Rechercher une vente par ID..."
                                       value="${param.searchTerm}">
                                <button class="btn btn-outline-primary" type="submit">
                                    <i class="fas fa-search"></i>
                                </button>
                            </div>
                        </form>
                        
                        <div class="navbar-buttons">
                            <a href="${pageContext.request.contextPath}/stock-report" class="btn btn-info">
                                <i class="fas fa-chart-bar"></i> Rapports
                            </a>
                            <a href="${pageContext.request.contextPath}/accueil" class="btn btn-outline-primary">
                                <i class="fas fa-home"></i> Accueil
                            </a>
                            <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger">
                                <i class="fas fa-sign-out-alt"></i> Déconnexion
                            </a>
                        </div>
                    </div>
                </nav>

                <!-- Contenu principal de la page Ventes -->
                <div class="container">
                    <div class="header-section">
                        <h2><i class="fas fa-file-invoice-dollar"></i> Gestion des Ventes</h2>
                        <a href="${pageContext.request.contextPath}/ventes?action=add" class="btn btn-primary">
                            <i class="fas fa-plus-circle"></i> Nouvelle Vente
                        </a>
                    </div>
                    
                    <c:if test="${not empty requestScope.error}">
                        <div class="alert alert-danger" role="alert">
                            ${requestScope.error}
                        </div>
                    </c:if>
                    <c:if test="${param.deleted eq 'true'}">
                        <div class="alert alert-success" role="alert">
                            Vente supprimée avec succès.
                        </div>
                    </c:if>
                    <c:if test="${param.added eq 'true'}">
                        <div class="alert alert-success" role="alert">
                            Vente ajoutée avec succès.
                        </div>
                    </c:if>
                    <c:if test="${not empty requestScope.warning}">
                        <div class="alert alert-warning" role="alert">
                            ${requestScope.warning}
                        </div>
                    </c:if>

                    <div class="table-responsive">
                        <table class="table table-striped table-bordered">
                            <thead class="thead-dark">
                                <tr>
                                    <th>ID</th>
                                    <th>Date</th>
                                    <th>Montant Total</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${requestScope.ventes}" var="vente">
                                    <tr>
                                        <td>${vente.id}</td>
                                        <td><fmt:formatDate value="${vente.dateVente}" pattern="dd/MM/yyyy"/></td>
                                        <td><fmt:formatNumber value="${vente.montantTotal}" type="currency" currencySymbol="DH " minFractionDigits="2" maxFractionDigits="2"/></td>
                                        <td class="action-buttons">
                                            <a href="${pageContext.request.contextPath}/ventes?action=details&id=${vente.id}" class="btn btn-info btn-sm">Détails</a>
                                            <a href="${pageContext.request.contextPath}/ventes?action=delete&id=${vente.id}" 
                                               class="btn btn-danger btn-sm" 
                                               onclick="return confirm('Êtes-vous sûr de vouloir supprimer cette vente?')">Supprimer</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty requestScope.ventes}">
                                    <tr>
                                        <td colspan="4" class="text-center">Aucune vente trouvée</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Les scripts de la sidebar sont inclus via le fragment -->
    <!-- Pas de scripts jQuery 3.5.1/Popper 2.5.4 car Bootstrap 5.3.0 utilise son propre bundle. -->
</body>
</html>
