<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Rapport de Stock</title>
    <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js"></script>
    <style type="text/css">
        body {
            font-size: 16px;
            font-family: Arial, sans-serif;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
        }
        th, td {
            padding: 8px 12px;
            border: 1px solid #ccc;
            text-align: left;
        }
        th {
            background-color: #f8f8f8;
        }
    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/_sidebar.jsp" />
    <h2>Rapport de Stock Généré</h2>

    <button onclick="downloadPDF()" style="margin: 1rem 0;">📥 Télécharger en PDF</button>

    <div id="rapport-container" style="padding: 1rem; border: 1px solid #ccc;"></div>

    <script>
        const rawMarkdown = `<%= request.getAttribute("rapport") %>`;
        const container = document.getElementById("rapport-container");
        container.innerHTML = marked.parse(rawMarkdown);

        function downloadPDF() {
            const element = document.getElementById("rapport-container");

            const opt = {
                margin:       0.5,
                filename:     'rapport-stock.pdf',
                image:        { type: 'jpeg', quality: 0.98 },
                html2canvas:  { scale: 2 },
                jsPDF:        { unit: 'in', format: 'a4', orientation: 'portrait' }
            };

            html2pdf().set(opt).from(element).save();
        }
    </script>
</body>
</html>
