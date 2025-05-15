<%@ page session="true" %>
<%
    String nom = (String) session.getAttribute("utilisateur");
    if (nom == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Accueil</title>
</head>
<body>
    <h2>Bienvenue, <%= nom %> !</h2>
    <p>Vous êtes connecté à l'application de gestion de stock.</p>
</body>
</html>
