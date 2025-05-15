<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Connexion</title>
</head>
<body>
    <h2>Connexion à la pharmacie</h2>
	<form action="login" method="post">
    <input type="text" name="login" required />
    <input type="password" name="motdepasse" required />
    <button type="submit">Se connecter</button>
</form>

    <p style="color:red;">
        ${erreur}
    </p>
</body>
</html>
