package hoe;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Felhasználó regisztrációját végző szervlet.
 *
 * @author imruf84
 */
public class RegisterServlet extends HttpServletWithUserValidator {

    /**
     * Html oldalban az oldal címének a változóneve.
     */
    public static final String HTML_LOGIN_LINK_VARIABLE_NAME = "LOGIN_LINK";

    @Override
    public void validateUser(HttpServletRequest request, HttpServletResponse response, User user, int requestType) throws IOException {
        // Ha a felhasználó hitelesítve van akkor átirányítjuk a játékhoz.
        if (null != user) {
            response.sendRedirect(HttpServer.PLAY_PATH);
            return;
        }

        // Egyébként regisztráljuk.
        appendDefaultPage(request, response, "");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Felhasználói adatok lekérdezése a kérelemből.
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String passwordConfirm = request.getParameter("passwordconfirm");

        // Ha nincs megadva felhasználónév, akkor hibával kilépünk.
        if (username.isEmpty()) {
            appendDefaultPage(request, response, Language.getText(LanguageMessageKey.USERNAME_MUST_BE_SET));
            return;
        }

        // Ha nem egyeznek meg a jelszavak akkor hibával kilépünk.
        if (!password.equals(passwordConfirm)) {
            appendDefaultPage(request, response, Language.getText(LanguageMessageKey.PASSWORDS_NOT_EQUALS));
            return;
        }

        User user = new User(request.getSession().getId(), username, password);

        try {
            // Ha létezik már felhasználó ezzel a névvel akkor hibával kilépünk.
            if (UserManager.isUserStored(user)) {
                appendDefaultPage(request, response, Language.getText(LanguageMessageKey.USER_ALREADY_REGISTERED));
                return;
            }

            // Ha sikertelen a felhasználó tárolása akkor hibával kilépünk.
            if (!UserManager.storeUser(user)) {
                appendDefaultPage(request, response, Language.getText(LanguageMessageKey.STORING_USER_FAILED));
                return;
            }

            // Felhasználó átirányítása a bejelentkező oldalra.
            response.sendRedirect(HttpServer.PLAY_PATH);

        } catch (SQLException ex) {
            Log.error(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    protected String insertCustomVariableValue(HttpServletRequest request, HttpServletResponse response, String v) {

        if (v.equals(HTML_PAGE_TITLE_VARIABLE_NAME)) {
            return HttpServer.APP_TITLE + " - " + Language.getText(LanguageMessageKey.REGISTER);
        }
        if (v.equals(HTML_LOGIN_LINK_VARIABLE_NAME)) {
            return HttpServer.LOGIN_PATH;
        }

        return "";
    }
    
    @Override
    protected String getDefaultPagePath() {
        return "/hoe/html/register.html";
    }

}
