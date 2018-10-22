package hoe.servlets;

import hoe.servers.GameServer;
import hoe.Language;
import hoe.LanguageMessageKey;
import hoe.Log;
import hoe.User;
import hoe.UserManager;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServletWithUserValidator {

    public static final String HTML_REGISTER_LINK_VARIABLE_NAME = "REGISTER_LINK";

    @Override
    public void validateUser(HttpServletRequest request, HttpServletResponse response, User user, int requestType) throws IOException {

        // Redirection valid user to the game.
        if (null != user) {
            response.sendRedirect(GameServer.PLAY_PATH);
            return;
        }

        // Redirecting to the login page else.
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        appendDefaultPage(request, response, "");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Getting user's data from request.
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Exit on missing username.
        if (username.isEmpty()) {
            appendDefaultPage(request, response, Language.getText(LanguageMessageKey.USERNAME_MUST_BE_SET));
            return;
        }

        // Getting user's data from name.
        User user = UserManager.getUserByName(username);

        // Logging out all the other players with the same name.
        if (null != user) {

            // Redirecting on active connection...
            if (user.hasActiveContext()) {
                user.sendMessage("{\"a\":\"rd\",\"d\":{\"url\":\"" + GameServer.LOGOUT_PATH + "\"}}");

                // Waiting while user is exists.
                while (null != UserManager.getUserByName(username)) {
                };
            }

            // ...and invalidating all the owned sessions.
            try {
                UserManager.logoutUser(user.getSessionId());
                //return;
            } catch (Exception ex) {
                Log.error(Language.getText(LanguageMessageKey.LOGOUT_FAILED), ex);
            }
        }

        try {

            User u = new User(request.getSession().getId(), username, password);

            // Exit if user does not exist.
            if (!UserManager.isUserStored(u)) {
                appendDefaultPage(request, response, Language.getText(LanguageMessageKey.INVALID_USER_NAME));
                return;
            }

            // Logging in the user and redirecting to the game.
            u = UserManager.loginUser(request, u);

            // Wrong password.
            if (null == u) {
                appendDefaultPage(request, response, Language.getText(LanguageMessageKey.INVALID_PASSWORD));
                return;
            }

            response.sendRedirect(GameServer.PLAY_PATH);

        } catch (Exception ex) {
            Log.error(ex.getLocalizedMessage(), ex);
        }

    }

    @Override
    protected String insertCustomVariableValue(HttpServletRequest request, HttpServletResponse response, String v) {

        if (v.equals(HTML_REGISTER_LINK_VARIABLE_NAME)) {
            return GameServer.REGISTER_PATH;
        }

        return super.insertCustomVariableValue(request, response, v);
    }

    @Override
    protected String getDefaultPagePath() {
        return "/hoe/html/login.html";
    }

}
