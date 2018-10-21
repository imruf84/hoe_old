package hoe.servlets;

import hoe.HttpServer;
import hoe.Log;
import hoe.UserManager;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            // Redirecting to the login page after logging out.
            UserManager.logoutUser(request);
        } catch (Exception ex) {
            Log.error(ex.getLocalizedMessage(), ex);
        }
        
        response.sendRedirect(HttpServer.LOGIN_PATH);
    }
}
