<%@ page session="true" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String nom = (String) session.getAttribute("utilisateur");
    if (nom == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Gestion des M�dicaments</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        /* D�finition des variables CSS si elles ne sont pas d�j� d�finies dans le fragment ou un fichier CSS commun */
        :root {
            --pharma-blue: #0056b3; /* Utilisez la couleur de accueil.jsp pour la coh�rence */
            --pharma-light-blue: #e6f2ff;
            --pharma-green: #28a745;
            --pharma-white: #f8f9fa;
        }
        
        body {
            background-color: var(--pharma-white);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            /* padding-left sera g�r� par le style du fragment _sidebar.jsp */
        }
        
        .container-fluid { /* Utilisation de container-fluid pour la page enti�re avec la sidebar */
            padding-right: 0;
            padding-left: 0;
        }

        .main-content { /* Le contenu principal qui sera � c�t� de la sidebar */
            padding: 30px;
            margin-left: auto; /* Permet au contenu de s'ajuster apr�s la sidebar */
            margin-right: auto;
        }
        
        h2 {
            color: var(--phma-blue);
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
        
        .navbar-buttons {
            display: flex;
            gap: 10px;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar Include -->
            <jsp:include page="/WEB-INF/fragments/_sidebar.jsp" />

            <!-- Main Content -->
            <div class="container"> <%-- Pas besoin d'offset ici car body padding-left est global --%>
                
                <!-- Navbar (qui �tait dans medicament.jsp, maintenant � l'int�rieur du main-content) -->
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
                                       placeholder="Rechercher un m�dicament..."
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
                            <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger">
                                <i class="fas fa-sign-out-alt"></i> D�connexion
                            </a>
                        </div>
                    </div>
                </nav>

                <div class="container mt-5">
                    <div class="header-section">
                        <h2><i class="fas fa-pills med-icon"></i>Gestion des M�dicaments</h2>
                        <a href="produits?action=add" class="btn btn-primary">
                            <i class="fas fa-plus-circle"></i> Ajouter un m�dicament
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
                                        <td class="price-cell">${p.prixVente} DH</td> <%-- Ajout de la devise --%>
                                        <td class="action-buttons">
                                            <a href="${pageContext.request.contextPath}/produits?action=edit&id=${p.id}" 
                                               class="btn btn-warning btn-sm"
                                               title="Modifier">
                                                <i class="fas fa-edit"></i> Modifier
                                            </a>
                                            <a href="${pageContext.request.contextPath}/produits?action=delete&id=${p.id}" 
                                               onclick="return confirm('�tes-vous s�r de vouloir supprimer ce m�dicament?')" 
                                               class="btn btn-danger btn-sm"
                                               title="Supprimer">
                                                <i class="fas fa-trash-alt"></i> Supprimer
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty produits}">
                                    <tr>
                                        <td colspan="3">Aucun m�dicament trouv�.</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Inclure le script de la sidebar si des fonctionnalit�s JS y sont li�es et si refreshAlerts n'est pas g�r� globalement -->
    <%-- Si refreshAlerts �tait dans le script de la sidebar, il sera ex�cut� avec l'inclusion --%>
</body>
</html>