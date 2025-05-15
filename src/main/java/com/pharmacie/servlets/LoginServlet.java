package com.pharmacie.servlets;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.pharmacie.util.ConnectDb;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.getWriter().println("✅ Servlet Login accessible !");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String login = request.getParameter("login");
        String motDePasse = request.getParameter("motdepasse");

        Connection conn = ConnectDb.getConnection();

        // ✅ Vérification de la connexion
        if (conn == null) {
            request.setAttribute("erreur", "Erreur de connexion à la base de données !");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT Nom FROM Utilisateur WHERE Login = ? AND MotDePasse = ?")) {

            ps.setString(1, login);
            ps.setString(2, motDePasse);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HttpSession session = request.getSession();
                    session.setAttribute("utilisateur", rs.getString("Nom"));
                    response.sendRedirect(request.getContextPath() + "/accueil.jsp");
                    return;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("erreur", "Erreur serveur : " + e.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        request.setAttribute("erreur", "Login ou mot de passe incorrect !");
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}
