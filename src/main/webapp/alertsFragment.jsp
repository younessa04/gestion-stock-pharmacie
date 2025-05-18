<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${not empty activeAlerts}">
    <div class="alert-container mb-4">
        <div class="alert alert-warning alert-dismissible fade show">
            <div class="d-flex justify-content-between align-items-center">
                <h4 class="mb-0">
                    <i class="fas fa-exclamation-triangle me-2"></i> 
                    Alertes de Stock
                </h4>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            
            <div class="alert-list mt-3">
                <c:forEach items="${activeAlerts}" var="alert">
                    <div class="alert-item ${alert.alertType == 'RUPTURE' ? 'alert-danger' : 'alert-warning'} p-3 mb-2 rounded">
                        <div class="d-flex justify-content-between">
                            <div>
                                <strong>${alert.produitNom}</strong> - 
                                ${alert.message}
                            </div>
                            <span class="alert-date text-muted">
                                <fmt:formatDate value="${alert.alertDate}" pattern="dd/MM/yyyy HH:mm"/>
                            </span>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</c:if>