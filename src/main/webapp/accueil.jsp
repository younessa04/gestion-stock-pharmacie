

<%@ page session="true" %>
<%
    String nom = (String) session.getAttribute("utilisateur");
    if (nom == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!-- En haut de votre JSP -->
<%@page import="java.util.List,com.pharmacie.entities.StockAlert" %>
<%
    List<StockAlert> testAlerts = (List<StockAlert>) request.getAttribute("activeAlerts");
    if (testAlerts != null) {
        System.out.println("Nombre d'alertes reçues : " + testAlerts.size());
        for (StockAlert a : testAlerts) {
            System.out.println("Alerte: " + a.getProduitNom() + " - " + a.getMessage());
        }
    } else {
        System.out.println("Aucune alerte reçue dans la JSP");
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tableau de Bord | Gestion Pharma</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        :root {
            --pharma-blue: #0056b3;
            --pharma-green: #28a745;
            --pharma-light: #f8f9fa;
            --pharma-dark: #212529;
        }
        
        body {
            background-color: #f8fafc;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .sidebar {
            background: linear-gradient(135deg, var(--pharma-blue), #003366);
            color: white;
            min-height: 100vh;
            box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
        }
        
        .sidebar-header {
            padding: 20px;
            background-color: rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        
        .sidebar-menu {
            padding: 20px 0;
        }
        
        .sidebar-menu a {
            color: rgba(255, 255, 255, 0.8);
            padding: 12px 20px;
            margin: 5px 0;
            border-radius: 5px;
            display: block;
            transition: all 0.3s;
            text-decoration: none;
        }
        
        .sidebar-menu a:hover, .sidebar-menu a.active {
            background-color: rgba(255, 255, 255, 0.1);
            color: white;
            transform: translateX(5px);
        }
        
        .sidebar-menu i {
            margin-right: 10px;
            width: 20px;
            text-align: center;
        }
        
        .main-content {
            padding: 30px;
        }
        
        .welcome-card {
            background: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
            border-left: 4px solid var(--pharma-green);
            margin-bottom: 30px;
        }
        
        .welcome-card h2 {
            color: var(--pharma-blue);
            font-weight: 700;
        }
        
        .welcome-card .welcome-icon {
            font-size: 2.5rem;
            color: var(--pharma-green);
            margin-bottom: 15px;
        }
        
        .stats-card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
            margin-bottom: 20px;
            border-top: 3px solid var(--pharma-blue);
            transition: all 0.3s;
        }
        
        .stats-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
        }
        
        .stats-card i {
            font-size: 1.8rem;
            color: var(--pharma-blue);
            margin-bottom: 10px;
        }
        
        .stats-card h3 {
            font-size: 1.1rem;
            color: var(--pharma-dark);
            margin-bottom: 10px;
        }
        
        .stats-card .number {
            font-size: 1.8rem;
            font-weight: 700;
            color: var(--pharma-green);
        }
        
        .user-profile {
            display: flex;
            align-items: center;
            padding: 10px;
            background-color: rgba(255, 255, 255, 0.1);
            border-radius: 50px;
            margin-top: 20px;
        }
        
        .user-profile img {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            margin-right: 10px;
            object-fit: cover;
            border: 2px solid rgba(255, 255, 255, 0.3);
        }
        
        .logout-btn {
            background-color: rgba(255, 255, 255, 0.1);
            border: 1px solid rgba(255, 255, 255, 0.3);
            color: white;
            border-radius: 5px;
            padding: 5px 15px;
            transition: all 0.3s;
        }
        
        .logout-btn:hover {
            background-color: rgba(255, 255, 255, 0.2);
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3 col-lg-2 d-md-block sidebar px-0">
                <div class="sidebar-header">
                    <h3><i class="fas fa-prescription-bottle-alt"></i> PharmaStock</h3>
                    <small>Système de gestion</small>
                </div>
                
                <div class="user-profile px-3">
                    <img src="https://ui-avatars.com/api/?name=<%= nom %>&background=random" alt="Profile">
                    <div style="flex-grow: 1;">
                        <div style="font-weight: 500;"><%= nom %></div>
                        <small>Administrateur</small>
                    </div>
                    <a href="logout" class="logout-btn" title="Déconnexion">
                        <i class="fas fa-sign-out-alt"></i>
                    </a>
                </div>
                
                <div class="sidebar-menu">
                    <a href="#" class="active"><i class="fas fa-tachometer-alt"></i> Tableau de bord</a>
					<!-- Dans votre fichier sidebar/menu (ex: sidebar.jsp) -->
					<a href="${pageContext.request.contextPath}/produits" class="nav-link">
					  <i class="fas fa-pills"></i> Médicaments
					</a>                    
                    <a href="#"><i class="fas fa-users"></i> Clients</a>
                    <a href="${pageContext.request.contextPath}/achats"><i class="fas fa-file-invoice-dollar"></i> Achats</a>
                    <a href="${pageContext.request.contextPath}/ventes"><i class="fas fa-file-invoice-dollar"></i> Ventes</a>
                    <a href="rapport"><i class="fas fa-chart-line"></i> Rapports</a>
                    <a href="#"><i class="fas fa-cog"></i> Paramètres</a>
                </div>
            </div>
            
            <!-- Main Content -->
            <div class="col-md-9 col-lg-10 main-content">
            
            
            	<!-- Section Alertes - Version améliorée -->
<c:if test="${not empty activeAlerts}">
    <div class="card mb-4 border-0 shadow-sm">
        <div class="card-header bg-warning text-dark d-flex justify-content-between align-items-center">
            <h5 class="mb-0">
                <i class="fas fa-exclamation-triangle me-2"></i> 
                Alertes de Stock <span class="badge bg-danger ms-2">${activeAlerts.size()}</span>
            </h5>
            <div>
                <a href="stock-report?type=faible" class="btn btn-sm btn-outline-dark me-2">
                    <i class="fas fa-list"></i> Voir détails
                </a>
                <button class="btn btn-sm btn-dark" data-bs-toggle="collapse" data-bs-target="#alertCollapse">
                    <i class="fas fa-chevron-down"></i>
                </button>
            </div>
        </div>
        
        <div class="collapse show" id="alertCollapse">
            <div class="card-body p-0">
                <div class="list-group list-group-flush">
                    <c:forEach items="${activeAlerts}" var="alert">
                        <div class="list-group-item list-group-item-action ${alert.alertType == 'RUPTURE' ? 'list-group-item-danger' : 'list-group-item-warning'}">
                            <div class="d-flex justify-content-between align-items-center">
                                <div>
                                    <h6 class="mb-1">
                                        <i class="fas ${alert.alertType == 'RUPTURE' ? 'fa-times-circle' : 'fa-exclamation-circle'} me-2"></i>
                                        ${alert.produitNom}
                                    </h6>
                                    <small>${alert.message}</small>
                                </div>
                                <small class="text-muted">
                                    <fmt:formatDate value="${alert.alertDate}" pattern="dd/MM HH:mm"/>
                                </small>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
            
            <div class="card-footer bg-light d-flex justify-content-between">
                <small class="text-muted">
                    Dernière mise à jour : <fmt:formatDate value="${now}" pattern="dd/MM/yyyy HH:mm:ss"/>
                </small>
                <form action="stock-report" method="post" class="d-inline">
                    <input type="hidden" name="action" value="send-alerts">
                    <button type="submit" class="btn btn-sm btn-success">
                        <i class="fas fa-paper-plane me-1"></i> Envoyer par email
                    </button>
                </form>
            </div>
        </div>
    </div>
</c:if>
                <div class="welcome-card">
                    <div class="welcome-icon">
                        <i class="fas fa-hand-holding-medical"></i>
                    </div>
                    <h2>Bienvenue, <span style="color: var(--pharma-green);"><%= nom %></span> !</h2>
                    <p class="lead">Vous êtes connecté au système de gestion pharmaceutique PharmaStock.</p>
                    <p>Voici un aperçu des principales fonctionnalités disponibles.</p>
                </div>
                
                <div class="row">
                    <div class="col-md-6 col-lg-3">
                        <div class="stats-card">
                            <i class="fas fa-pills"></i>
                            <h3>Médicaments</h3>
                            <div class="number">128</div>
                            <small>Enregistrés</small>
                        </div>
                    </div>
                    
                    <div class="col-md-6 col-lg-3">
                        <div class="stats-card">
                            <i class="fas fa-box-open"></i>
                            <h3>Stock</h3>
                            <div class="number">542</div>
                            <small>Unités disponibles</small>
                        </div>
                    </div>
                    
                    <div class="col-md-6 col-lg-3">
                        <div class="stats-card">
                            <i class="fas fa-exclamation-triangle"></i>
                            <h3>Alertes</h3>
                            <div class="number">5</div>
                            <small>Stocks faibles</small>
                        </div>
                    </div>
                    
                    <div class="col-md-6 col-lg-3">
                        <div class="stats-card">
                            <i class="fas fa-calendar-day"></i>
                            <h3>Aujourd'hui</h3>
                            <div class="number">24</div>
                            <small>Ventes</small>
                        </div>
                    </div>
                </div>
                
                <div class="row mt-4">
                    <div class="col-md-6">
                        <div class="stats-card h-100">
                            <i class="fas fa-chart-bar"></i>
                            <h3>Statistiques des ventes</h3>
                            <p>Graphique des ventes des 7 derniers jours</p>
                            <div style="height: 200px; background: #f8f9fa; border-radius: 5px; display: flex; align-items: center; justify-content: center; color: #6c757d;">
                                [Graphique à implémenter]
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-md-6">
                        <div class="stats-card h-100">
                            <i class="fas fa-bell"></i>
                            <h3>Alertes récentes</h3>
                            <ul style="padding-left: 20px;">
                                <li>Paracétamol - Stock critique</li>
                                <li>Ibuprofène - Péremption proche</li>
                                <li>Commande #4587 livrée</li>
                                <li>2 nouveaux clients ce mois</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Active le tooltip pour le bouton de déconnexion
        document.addEventListener('DOMContentLoaded', function() {
            var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
            var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
                return new bootstrap.Tooltip(tooltipTriggerEl);
            });
        });
    </script>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Actualise les alertes toutes les 5 minutes
            function refreshAlerts() {
                fetch('stock-report?action=check-alerts')
                    .then(response => {
                        if (!response.ok) throw new Error('Network response was not ok');
                        return response.text();
                    })
                    .then(html => {
                        const parser = new DOMParser();
                        const doc = parser.parseFromString(html, 'text/html');
                        const newAlerts = doc.querySelector('.alert-container');
                        if (newAlerts) {
                            const currentAlerts = document.querySelector('.alert-container');
                            if (currentAlerts) {
                                currentAlerts.replaceWith(newAlerts);
                            } else {
                                document.querySelector('.main-content').prepend(newAlerts);
                            }
                        }
                    })
                    .catch(error => console.error('Error refreshing alerts:', error));
            }

            // Démarrer l'actualisation périodique
            setInterval(refreshAlerts, 300000); // 5 minutes
            
            // Actualiser aussi au chargement de la page
            document.addEventListener('DOMContentLoaded', refreshAlerts);
        </script>
    
</body>
</html>

