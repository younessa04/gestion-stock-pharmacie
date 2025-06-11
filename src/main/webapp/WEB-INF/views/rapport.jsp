<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Rapport généré</title>
</head>
<body>
    <h1>📊 Rapport automatique</h1>
    <pre style="font-family: monospace; background-color: #f9f9f9; padding: 10px;">
        ${rapportTexte}
    </pre>
</body>
</html>
