package hoe.servlets;

import hoe.servers.GameServer;
import hoe.Language;
import hoe.LanguageMessageKey;
import hoe.Log;
import hoe.User;
import hoe.UserManager;
import hoe.servers.AbstractServer;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegisterServlet extends HttpServletWithUserValidator {

    public static final String HTML_LOGIN_LINK_VARIABLE_NAME = "LOGIN_LINK";

    public RegisterServlet(AbstractServer server) {
        super(server);
    }

    @Override
    public void validateUser(HttpServletRequest request, HttpServletResponse response, User user, int requestType) throws IOException {
        // Redirecting valid players to the game
        if (null != user) {
            response.sendRedirect(GameServer.PLAY_PATH);
            return;
        }

        // Registering else.
        appendDefaultPage(request, response, "");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Getting user's data from the request.
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String passwordConfirm = request.getParameter("passwordconfirm");

        // Exit on missing user name.
        if (username.isEmpty()) {
            appendDefaultPage(request, response, Language.getText(LanguageMessageKey.USERNAME_MUST_BE_SET));
            return;
        }

        // Exit on wrong password.
        if (!password.equals(passwordConfirm)) {
            appendDefaultPage(request, response, Language.getText(LanguageMessageKey.PASSWORDS_NOT_EQUALS));
            return;
        }

        User user = new User(request.getSession().getId(), username, password);

        try {
            // Exit if player exists.
            if (UserManager.isUserStored(user)) {
                appendDefaultPage(request, response, Language.getText(LanguageMessageKey.USER_ALREADY_REGISTERED));
                return;
            }

            // Exit if storing player failed.
            if (!UserManager.storeUser(user)) {
                appendDefaultPage(request, response, Language.getText(LanguageMessageKey.STORING_USER_FAILED));
                return;
            }

            // Redirecting user to the game.
            response.sendRedirect(GameServer.PLAY_PATH);

        } catch (SQLException ex) {
            Log.error(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    protected String insertCustomVariableValue(HttpServletRequest request, HttpServletResponse response, String v) {

        if (v.equals(HTML_PAGE_TITLE_VARIABLE_NAME)) {
            return GameServer.APP_TITLE + " - " + Language.getText(LanguageMessageKey.REGISTER);
        }
        if (v.equals(HTML_LOGIN_LINK_VARIABLE_NAME)) {
            return GameServer.LOGIN_PATH;
        }

        return "";
    }

    @Override
    protected String getDefaultPagePath() {
        return "/hoe/html/register.html";
    }

}
