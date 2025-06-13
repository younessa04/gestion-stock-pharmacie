<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Rapport de Stock</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .report-header {
            background-color: #0077be;
            color: white;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .badge-rupture { background-color: #dc3545; }
        .badge-faible { background-color: #ffc107; color: #212529; }
        .badge-normal { background-color: #28a745; }
        .status-cell { width: 120px; }
    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/_sidebar.jsp" />
    <div class="container mt-4">
        <div class="report-header">
            <h2><i class="fas fa-chart-pie"></i> ${reportTitle}</h2>
            <div class="d-flex mt-3">
                <a href="stock-report?type=rupture" class="btn btn-danger me-2">
                    Rupture (${produitDAO.getProduitsEnRupture().size()})
                </a>
                <a href="stock-report?type=faible" class="btn btn-warning me-2">
                    Stock faible (${produitDAO.getProduitsStockFaible().size()})
                </a>
                <a href="stock-report?type=normal" class="btn btn-success me-2">
                    Stock normal (${produitDAO.getProduitsStockNormal().size()})
                </a>
                <a href="stock-report" class="btn btn-primary">
                    Tous les produits
                </a>
            </div>
        </div>
        
        <div class="table-responsive">
            <table class="table table-hover">
                <thead class="table-dark">
                    <tr>
                        <th>Nom</th>
                        <th>Stock Actuel</th>
                        <th>Stock Minimum</th>
                        <th>Statut</th>
                        <th>Fournisseur</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${produits}" var="p">
                        <tr>
                            <td>${p.nom}</td>
                            <td>${p.stockActuel}</td>
                            <td>${p.stockMin}</td>
                            <td class="status-cell">
                                <c:choose>
                                    <c:when test="${p.stockActuel <= 0}">
                                        <span class="badge badge-rupture">Rupture</span>
                                    </c:when>
                                    <c:when test="${p.stockActuel <= p.stockMin}">
                                        <span class="badge badge-faible">Faible</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-normal">Normal</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${p.nomFournisseur}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <div class="card-footer bg-light d-flex justify-content-between">
<!--                 <small class="text-muted"> -->
<%--                     Dernière mise à jour : <fmt:formatDate value="${now}" pattern="dd/MM/yyyy HH:mm:ss"/> --%>
<!--                 </small> -->
<c:if test="${not empty produits}">
    <div class="mb-3">
        <h5>Alerte à envoyer :</h5>
        <ul>
            <c:forEach items="${produits}" var="p">
                <c:if test="${p.stockActuel <= p.stockMin}">
                    <li>
                        <strong>${p.nom}</strong> - Stock : ${p.stockActuel} 
                        (min : ${p.stockMin}) - Fournisseur : ${p.nomFournisseur}
                        <c:choose>
                            <c:when test="${p.stockActuel == 0}">
                                <span class="text-danger">[Rupture]</span>
                            </c:when>
                            <c:otherwise>
                                <span class="text-warning">[Stock Faible]</span>
                            </c:otherwise>
                        </c:choose>
                    </li>
                </c:if>
            </c:forEach>
        </ul>
    </div>
</c:if>

                <form action="stock-report" method="post" class="d-inline">
                    <input type="hidden" name="action" value="send-alerts">
                    <button type="submit" class="btn btn-sm btn-success">
                        <i class="fas fa-paper-plane me-1"></i> Envoyer par email
                    </button>
                </form>
            </div>
        </div>
    </div>
</body>
</html>