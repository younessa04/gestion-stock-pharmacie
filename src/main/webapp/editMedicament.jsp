<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Édition Médicament</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .form-section {
            background-color: #f8f9fa;
            border-radius: 5px;
            padding: 20px;
            margin-bottom: 20px;
        }
        .form-section h5 {
            color: #0d6efd;
            margin-bottom: 15px;
        }
    </style>
</head>
<body>
    <div class="container mt-4">
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h3 class="mb-0">${produit.id == null ? 'Ajouter un nouveau' : 'Modifier le'} médicament</h3>
            </div>
            
            <div class="card-body">
                <form action="produits" method="post">
                    <input type="hidden" name="action" value="save">
                    <input type="hidden" name="id" value="${produit.id}">
                    
                    <!-- Section Information de base -->
                    <div class="form-section">
                        <h5><i class="fas fa-info-circle"></i> Information de base</h5>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Nom du médicament *</label>
                                <input type="text" name="nom" value="${produit.nom}" class="form-control" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Code barre *</label>
                                <input type="text" name="codeBarre" value="${produit.codeBarre}" class="form-control" required>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Forme *</label>
                                <select name="forme" class="form-select" required>
                                    <option value="">Sélectionnez...</option>
                                    <option value="Comprimé" ${produit.forme == 'Comprimé' ? 'selected' : ''}>Comprimé</option>
                                    <option value="Gélule" ${produit.forme == 'Gélule' ? 'selected' : ''}>Gélule</option>
                                    <option value="Sirop" ${produit.forme == 'Sirop' ? 'selected' : ''}>Sirop</option>
                                    <option value="Injectable" ${produit.forme == 'Injectable' ? 'selected' : ''}>Injectable</option>
                                    <option value="Crème" ${produit.forme == 'Crème' ? 'selected' : ''}>Crème</option>
                                </select>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Dosage *</label>
                                <input type="text" name="dosage" value="${produit.dosage}" class="form-control" required>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Prix de vente (€) *</label>
                                <input type="number" step="0.01" name="prix" value="${produit.prixVente}" class="form-control" required>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Section Stock -->
                    <div class="form-section">
                        <h5><i class="fas fa-boxes"></i> Gestion du stock</h5>
                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Stock actuel *</label>
                                <input type="number" name="stock" value="${produit.stockActuel}" class="form-control" required>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Stock minimum *</label>
                                <input type="number" name="stockMin" value="${produit.stockMin}" class="form-control" required>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Date d'expiration *</label>
                                <input type="date" name="dateExpiration" value="${produit.dateExpiration}" class="form-control" required>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Section Fournisseur -->
                    <div class="form-section">
                        <h5><i class="fas fa-truck"></i> Fournisseur</h5>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Fournisseur *</label>
                                <select name="fournisseur" class="form-select" required>
    <option value="">Sélectionnez un fournisseur</option>
    <c:forEach items="${fournisseurs}" var="f">
        <option value="${f.id}" ${produit.idFournisseur == f.id ? 'selected' : ''}>
            ${f.nom} - ${f.telephone}
        </option>
    </c:forEach>
</select>
                            </div>
                        </div>
                    </div>
                    
                    <div class="d-flex justify-content-between mt-4">
                        <a href="produits" class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i> Annuler
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> ${produit.id == null ? 'Enregistrer' : 'Mettre à jour'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Validation avant soumission
        document.querySelector('form').addEventListener('submit', function(e) {
            const dateExpiration = new Date(document.querySelector('[name="dateExpiration"]').value);
            const today = new Date();
            
            if (dateExpiration < today) {
                e.preventDefault();
                alert('La date d\'expiration ne peut pas être dans le passé !');
            }
        });
    </script>
</body>
</html>