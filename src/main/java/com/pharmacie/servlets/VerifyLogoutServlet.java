package com.pharmacie.servlets;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.JSONObject;
import com.pharmacie.util.ConnectDb;

@WebServlet("/verifyLogout")
public class VerifyLogoutServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        JSONObject jsonResponse = new JSONObject();
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        HttpSession session = request.getSession(false);
        
        try (Connection conn = ConnectDb.getConnection()) {
            String sql = "SELECT * FROM Utilisateur WHERE Login = ? AND MotDePasse = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Vérifier que l'utilisateur correspond à la session
                String sessionUser = (String) session.getAttribute("utilisateur");
                if (username.equals(sessionUser)) {
                    jsonResponse.put("success", true);
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "L'utilisateur ne correspond pas à la session");
                }
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Identifiants incorrects");
            }
        } catch (SQLException e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Erreur serveur");
            e.printStackTrace();
        }
        
        response.getWriter().write(jsonResponse.toString());
    }
}