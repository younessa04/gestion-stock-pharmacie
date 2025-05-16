<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion | Gestion Pharmacie</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        :root {
            --pharma-blue: #0056b3;
            --pharma-green: #28a745;
            --pharma-light: #f8f9fa;
        }
        
        body {
            background-color: #f0f8ff;
            background-image: url('https://img.freepik.com/free-vector/medical-background-with-abstract-wave-lines_53876-117857.jpg');
            background-size: cover;
            background-attachment: fixed;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        
        .login-container {
            max-width: 450px;
            margin: 5% auto;
            padding: 2.5rem;
            background: rgba(255, 255, 255, 0.95);
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0, 86, 179, 0.2);
            border-top: 5px solid var(--pharma-blue);
            animation: fadeIn 0.5s ease-in-out;
        }
        
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        .pharma-logo {
            text-align: center;
            margin-bottom: 2rem;
            color: var(--pharma-blue);
        }
        
        .pharma-logo i {
            font-size: 3.5rem;
            margin-bottom: 1rem;
        }
        
        .pharma-logo h2 {
            font-weight: 700;
            margin: 0;
        }
        
        .form-control {
            border-radius: 8px;
            padding: 12px 15px;
            margin-bottom: 1.5rem;
            border: 1px solid #ddd;
            transition: all 0.3s;
        }
        
        .form-control:focus {
            border-color: var(--pharma-blue);
            box-shadow: 0 0 0 0.25rem rgba(0, 86, 179, 0.25);
        }
        
        .btn-pharma {
            background-color: var(--pharma-blue);
            border: none;
            border-radius: 8px;
            padding: 12px;
            font-weight: 600;
            letter-spacing: 0.5px;
            width: 100%;
            transition: all 0.3s;
        }
        
        .btn-pharma:hover {
            background-color: #004494;
            transform: translateY(-2px);
        }
        
        .alert-message {
            border-radius: 8px;
            padding: 12px 15px;
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
        }
        
        .footer-text {
            text-align: center;
            margin-top: 1.5rem;
            color: #6c757d;
            font-size: 0.9rem;
        }
        
        .password-toggle {
            cursor: pointer;
            position: absolute;
            right: 10px;
            top: 50%;
            transform: translateY(-50%);
            color: #6c757d;
        }
        
        .input-group {
            position: relative;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="login-container">
            <div class="pharma-logo">
                <i class="fas fa-prescription-bottle-alt"></i>
                <h2>PHARMA<span style="color: var(--pharma-green);">CARE</span></h2>
                <p class="text-muted">Système de gestion pharmaceutique</p>
            </div>
            
            <%-- Message de déconnexion réussie --%>
            <% if ("true".equals(request.getParameter("logout"))) { %>
                <div class="alert alert-success alert-message">
                    <i class="fas fa-check-circle me-2"></i> Vous avez été déconnecté avec succès.
                </div>
            <% } %>
            
            <%-- Message d'erreur de connexion --%>
            <% if (request.getAttribute("erreur") != null) { %>
                <div class="alert alert-danger alert-message">
                    <i class="fas fa-exclamation-circle me-2"></i> ${erreur}
                </div>
            <% } %>
            
            <form action="login" method="post">
                <div class="mb-3">
                    <label for="login" class="form-label">Identifiant</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fas fa-user"></i></span>
                        <input type="text" class="form-control" id="login" name="login" placeholder="Entrez votre identifiant" required>
                    </div>
                </div>
                
                <div class="mb-3">
                    <label for="motdepasse" class="form-label">Mot de passe</label>
                    <div class="input-group" style="position: relative;">
                        <span class="input-group-text"><i class="fas fa-lock"></i></span>
                        <input type="password" class="form-control" id="motdepasse" name="motdepasse" placeholder="Entrez votre mot de passe" required>
                        <i class="fas fa-eye password-toggle" onclick="togglePassword()"></i>
                    </div>
                </div>
                
                <button type="submit" class="btn btn-pharma">
                    <i class="fas fa-sign-in-alt"></i> Se connecter
                </button>
            </form>
            
            <div class="footer-text">
                <p>© 2023 PharmaCare. Tous droits réservés.</p>
                <p><a href="#" style="color: var(--pharma-blue); text-decoration: none;"><i class="fas fa-question-circle"></i> Aide</a></p>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Fonction pour basculer la visibilité du mot de passe
        function togglePassword() {
            const passwordField = document.getElementById('motdepasse');
            const toggleIcon = document.querySelector('.password-toggle');
            
            if (passwordField.type === 'password') {
                passwordField.type = 'text';
                toggleIcon.classList.replace('fa-eye', 'fa-eye-slash');
            } else {
                passwordField.type = 'password';
                toggleIcon.classList.replace('fa-eye-slash', 'fa-eye');
            }
        }
        
        // Animation pour les messages d'alerte
        document.addEventListener('DOMContentLoaded', function() {
            const alerts = document.querySelectorAll('.alert-message');
            alerts.forEach(alert => {
                alert.style.opacity = '0';
                alert.style.transform = 'translateY(-10px)';
                setTimeout(() => {
                    alert.style.transition = 'all 0.3s ease-out';
                    alert.style.opacity = '1';
                    alert.style.transform = 'translateY(0)';
                }, 100);
            });
        });
    </script>
</body>
</html>