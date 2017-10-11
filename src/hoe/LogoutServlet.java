package hoe;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * felhasználó kiléptetése.
 *
 * @author imruf84
 */
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            // Kiléptetés után visszalépünk a bejelentkező oldalra.
            UserManager.logoutUser(request);
        } catch (Exception ex) {
            Log.error(ex.getLocalizedMessage(), ex);
        }
        response.sendRedirect(HttpServer.LOGIN_PATH);
    }
}
