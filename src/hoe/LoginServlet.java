package hoe;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Bejelentkezés kiszolgáló.
 *
 * @author imruf84
 */
public class LoginServlet extends HttpServletWithUserValidator {

    /**
     * Html oldalban az oldal címének a változóneve.
     */
    public static final String HTML_REGISTER_LINK_VARIABLE_NAME = "REGISTER_LINK";

    @Override
    public void validateUser(HttpServletRequest request, HttpServletResponse response, User user, int requestType) throws IOException {

        // Ha a felhasználó hitelesítve van akkor átirányítjuk a játékhoz.
        if (null != user) {
            response.sendRedirect(HttpServer.PLAY_PATH);
            return;
        }

        // Egyébként beléptetjük.
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        appendDefaultPage(request, response, "");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Felhasználói adatok lekérdezése a kérelemből.
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Ha nincs megadva felhasználónév, akkor hibával kilépünk.
        if (username.isEmpty()) {
            appendDefaultPage(request, response, Language.getText(LanguageMessageKey.USERNAME_MUST_BE_SET));
            return;
        }

        // Névhez tartozó felhasználó lekérdezése.
        User user = UserManager.getUserByName(username);

        // Ha már van ilyen néven felhasználó bejelentkezve, akkor kiléptetjük.
        if (null != user) {

            // Ha rendelkezik aktív kapcsolattal, akkor átirányítást küldünk...
            if (user.hasActiveContext()) {
                user.sendMessage("{\"a\":\"rd\",\"d\":{\"url\":\"" + HttpServer.LOGOUT_PATH + "\"}}");

                // Addig várunk, amíg az adott nevű felhasználó el nem tűnik a felhasználó kezelőből.
                while (null != UserManager.getUserByName(username)){};
            }

            // ...majd érvénytelenítjük az összes hozzá tartozó sessiont.
            try {
                UserManager.logoutUser(user.getSessionId());
                //return;
            } catch (Exception ex) {
                Log.error(Language.getText(LanguageMessageKey.LOGOUT_FAILED), ex);
            }
        }

        try {

            User u = new User(request.getSession().getId(), username, password);

            // Ha nem létezik még ilyen felhasználó, akkor hibával kilépünk.
            if (!UserManager.isUserStored(u)) {
                appendDefaultPage(request, response, Language.getText(LanguageMessageKey.INVALID_USER_NAME));
                return;
            }

            // Egyébként beléptetjük a felhasználót és átirányítjuk a játékhoz.
            u = UserManager.loginUser(request, u);

            // Jelszó nem stimmel.
            if (null == u) {
                appendDefaultPage(request, response, Language.getText(LanguageMessageKey.INVALID_PASSWORD));
                return;
            }

            response.sendRedirect(HttpServer.PLAY_PATH);

        } catch (Exception ex) {
            Log.error(ex.getLocalizedMessage(), ex);
        }

    }

    @Override
    protected String insertCustomVariableValue(HttpServletRequest request, HttpServletResponse response, String v) {

        if (v.equals(HTML_REGISTER_LINK_VARIABLE_NAME)) {
            return HttpServer.REGISTER_PATH;
        }

        return super.insertCustomVariableValue(request, response, v);
    }

    @Override
    protected String getDefaultPagePath() {
        return "/hoe/html/login.html";
    }

}
