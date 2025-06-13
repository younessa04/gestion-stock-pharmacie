<%@ page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Récupérer le nom de l'utilisateur depuis la session, car le fragment peut être inclus --%>
<%
    String nom = (String) session.getAttribute("utilisateur");
    // Pas de redirection ici; la page appelante doit gérer la redirection si l'utilisateur n'est pas connecté.
%>
<style>
    /* Styles spécifiques à la sidebar */
    :root {
        --pharma-blue: #0056b3;
        --pharma-green: #28a745;
        --pharma-light: #f8f9fa;
        --pharma-dark: #212529;
    }
    
    .sidebar {
        background: linear-gradient(135deg, var(--pharma-blue), #003366);
        color: white;
        min-height: 100vh;
        box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
        position: fixed; /* Rend la sidebar fixe */
        top: 0;
        bottom: 0;
        left: 0;
        z-index: 1000; /* Assure qu'elle est au-dessus du contenu */
        padding-top: 0; /* Ajustement pour éviter le double padding avec sidebar-header */
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
    
    .user-profile {
        display: flex;
        align-items: center;
        padding: 10px;
        background-color: rgba(255, 255, 255, 0.1);
        border-radius: 50px;
        margin: 20px 10px; /* Ajustement pour être centré dans la sidebar */
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

    /* Styles pour le contenu principal ajusté à la sidebar fixe */
    body {
        padding-left: 17%; /* Largeur approximative de la sidebar col-md-3/col-lg-2 */
    }
    .main-content {
        padding: 30px;
    }

    /* Media queries pour la responsivité */
    @media (max-width: 768px) {
        .sidebar {
            position: relative;
            min-height: auto;
            width: 100%;
            height: auto;
            box-shadow: none;
        }
        body {
            padding-left: 0;
        }
        .main-content {
            padding: 15px;
        }
        .user-profile {
            margin: 10px auto;
            width: 90%;
        }
    }
</style>

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
        <a href="${pageContext.request.contextPath}/logout" class="logout-btn" title="Déconnexion">
            <i class="fas fa-sign-out-alt"></i>
        </a>
    </div>
    
    <div class="sidebar-menu">
        <a href="${pageContext.request.contextPath}/accueil.jsp" class="nav-link">
        	<i class="fas fa-tachometer-alt"></i> Tableau de bord
        </a>
        <a href="${pageContext.request.contextPath}/produits" class="nav-link">
          <i class="fas fa-pills"></i> Médicaments
        </a>
        <a href="${pageContext.request.contextPath}/fournisseurs" class="nav-link">
            <i class="fas fa-truck"></i> Fournisseurs
        </a>
        <a href="${pageContext.request.contextPath}/achats" class="nav-link">
            <i class="fas fa-shopping-cart"></i> Achats
        </a>
        <a href="${pageContext.request.contextPath}/ventes" class="nav-link">
            <i class="fas fa-file-invoice-dollar"></i> Ventes
        </a>
        <a href="${pageContext.request.contextPath}/stock-report" class="nav-link">
        	<i class="fas fa-boxes"></i>Rapport de Stock
        </a>
		<a class="nav-link">
		    <form action="generate-stock-report" method="POST">
			    <i class="fas fa-chart-line"></i> 
			    <button class="btn btn-primary" type="submit">Générer rapport stock</button>
			</form>

		</a>
    </div>
</div>

<script>
    // Script pour activer le lien actif dans la sidebar
    document.addEventListener('DOMContentLoaded', function() {
        const currentPath = window.location.pathname;
        document.querySelectorAll('.sidebar-menu a').forEach(link => {
            // Supprimer la classe 'active' de tous les liens
            link.classList.remove('active');
            
            // Ajouter la classe 'active' si le href du lien correspond au chemin actuel
            // Gère les cas où le chemin est /servlet ou /servlet?param=value
            const linkHref = link.getAttribute('href');
            if (currentPath === linkHref || (currentPath + '/').startsWith(linkHref + '/') && linkHref !== '${pageContext.request.contextPath}/') {
                link.classList.add('active');
            }
             // Cas spécial pour la page d'accueil si elle est '/'
            if (currentPath === '${pageContext.request.contextPath}/accueil' && linkHref === '${pageContext.request.contextPath}/accueil') {
                 link.classList.add('active');
            }
        });
    });
</script>