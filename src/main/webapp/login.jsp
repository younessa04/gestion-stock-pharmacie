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
        
        .error-message {
            background-color: #ffe6e6;
            border-left: 4px solid #dc3545;
            padding: 10px;
            border-radius: 4px;
            margin-top: 1rem;
            display: ${empty erreur ? 'none' : 'block'};
        }
        
        .footer-text {
            text-align: center;
            margin-top: 1.5rem;
            color: #6c757d;
            font-size: 0.9rem;
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
                    <div class="input-group">
                        <span class="input-group-text"><i class="fas fa-lock"></i></span>
                        <input type="password" class="form-control" id="motdepasse" name="motdepasse" placeholder="Entrez votre mot de passe" required>
                    </div>
                </div>
                
                <button type="submit" class="btn btn-pharma">
                    <i class="fas fa-sign-in-alt"></i> Se connecter
                </button>
                
                <div class="error-message">
                    <i class="fas fa-exclamation-circle"></i> ${erreur}
                </div>
            </form>
            
            <div class="footer-text">
                <p>© 2023 PharmaCare. Tous droits réservés.</p>
                <p><a href="#" style="color: var(--pharma-blue); text-decoration: none;"><i class="fas fa-question-circle"></i> Aide</a></p>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Masquer le message d'erreur s'il est vide
        document.addEventListener('DOMContentLoaded', function() {
            const errorMessage = document.querySelector('.error-message');
            if (errorMessage.textContent.trim() === '') {
                errorMessage.style.display = 'none';
            }
        });
    </script>
</body>
</html>