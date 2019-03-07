package hoe.servlets;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import hoe.GameServlet;
import hoe.servers.GameServer;
import hoe.Language;
import hoe.LanguageMessageKey;
import hoe.Log;
import hoe.Meteor;
import hoe.JsonUtil;
import hoe.SceneManager;
import hoe.User;
import hoe.UserManager;
import hoe.servers.AbstractServer;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PlayServlet extends HttpServletWithUserValidator {

    public static final String HTML_ENABLE_DEBUG_VARIABLE_NAME = "DEBUG_ENABLED";
    public static final String HTML_PLAYER_NAME_VARIABLE_NAME = "PLAYER_NAME";
    public static final String HTML_CHAT_MESSAGES_VARIABLE_NAME = "DOWNLOADING_CHAT_MESSAGES";

    public PlayServlet(AbstractServer server) {
        super(server);
    }

    @Override
    public void validateUser(HttpServletRequest request, HttpServletResponse response, User user, int requestType) throws IOException {
        Log.debug("validateUser: sID=" + request.getSession().getId());

        // Redirection invalid user to the login page.
        if (null == user) {
            Log.debug("User is not valid: sID=" + request.getSession().getId());
            response.sendRedirect(GameServer.LOGIN_PATH);
            return;
        }

        // We only share GET requests.
        if (requestType == GameServer.GET_REQUEST) {

            // Exit on already connected users.
            if (0 < user.getAsyncChannelsCount()) {
                Log.debug("User is valid but already connected: name=" + user.getUserName() + " sID=" + user.getSessionId());
                response.setContentType("text/html");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().append("<!DOCTYPE html><html>"
                        + "<head>"
                        + "  <meta charset='UTF-8'>"
                        + "  <title>" + GameServer.APP_TITLE + "</title>"
                        + "</head>"
                        + "<body>"
                        + "  <div id='timeDiv'></div>"
                        + "  <script>"
                        + "    function msg(){document.getElementById('timeDiv').innerHTML = '" + Language.getText(LanguageMessageKey.USER_ALREADY_HAS_AN_ACTIVE_CONNECTION) + "'.replace('%time%',time);};"
                        + "    var time = 20;"
                        + "    msg();"
                        + "    setInterval(function(){time--; msg(); if (time == 0) location.reload();}, 1000);"
                        + "  </script>"
                        + "</body>"
                        + "</html>"
                );
                return;
            }

            Log.debug("User is valid: name=" + user.getUserName() + " sID=" + user.getSessionId());

            // Sharing game.
            appendDefaultPage(request, response, "");

            return;
        }

        // Handle sync messages on POST requests.
        String jsonString = request.getReader().readLine();
        Log.debug("POST[" + user.getUserName() + "]: " + jsonString);

        // Store response channel on empty requests.
        if (null == jsonString) {
            user.storeAsyncContext(request);
            return;
        }

        // Working with data.
        Gson gson = new Gson();
        @SuppressWarnings("unchecked")
        Map<String, Object> root = gson.fromJson(jsonString, Map.class);

        // "a" key for actions.
        switch (root.get("a").toString()) {
            // Sync game scene (GetSceneData)
            case "gsd":
                try {
                    Meteor m = new Meteor(1);
                    if (SceneManager.getMeteor(m)) {
                        user.sendMessage(m.toJson());
                    }
                } catch (SQLException ex) {
                    // Sending error message if exists.
                    user.sendMessage(ex.getLocalizedMessage());
                }
                break;
            // Getting chat messages history (GetChatMessages).
            case "gcm":
                try {
                    LinkedTreeMap data = (LinkedTreeMap) root.get("d");
                    int msgCount = (int) Float.parseFloat(data.get("mc").toString());
                    user.sendMessage(SceneManager.getSceneDataBase().getMessagesAsJson(msgCount));
                } catch (SQLException ex) {
                    // Sending error message if exists.
                    user.sendMessage(Language.getText(LanguageMessageKey.STORE_MESSAGE_FAILED));
                }

                break;
            // Getting game state (GetGameState).
            case "ggs":
                user.sendMessage(GameServlet.getStateChangedMessage());
                break;
            // Sending chat message (ChatMessage).
            case "cm":
                LinkedTreeMap data = (LinkedTreeMap) root.get("d");
                String msg = data.get("msg").toString();

                try {
                    // Storing message.
                    SceneManager.getSceneDataBase().storeMessage(user, msg);
                    // Sending message to all players.
                    UserManager.sendMessageToAll(JsonUtil.chatMessage(user.getUserName(), msg));
                } catch (SQLException ex) {
                    // Sending error message if exists.
                    user.sendMessage(Language.getText(LanguageMessageKey.STORE_MESSAGE_FAILED));
                }

                break;
        }

    }

    @Override
    protected String insertCustomVariableValue(HttpServletRequest request, HttpServletResponse response, String v) {

        if (v.equals(HTML_PLAYER_NAME_VARIABLE_NAME)) {
            return UserManager.getUserBySession(request).getUserName();
        }

        if (v.equals(HTML_CHAT_MESSAGES_VARIABLE_NAME)) {
            return Language.getText(LanguageMessageKey.GETTING_MESSAGES);
        }

        if (v.equals(HTML_ENABLE_DEBUG_VARIABLE_NAME)) {
            return Log.showDebugMessages ? "true" : "false";
        }

        return super.insertCustomVariableValue(request, response, v);
    }

    @Override
    protected String getDefaultPagePath() {
        return "/hoe/html/play.html";
    }

}
