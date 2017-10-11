package hoe;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Játék kiszolgáló.
 *
 * @author imruf84
 */
public class PlayServlet extends HttpServletWithUserValidator {

    /**
     * Html oldalban a debug üzenetek megjelenítésének a változóneve.
     */
    public static final String HTML_ENABLE_DEBUG_VARIABLE_NAME = "DEBUG_ENABLED";
    /**
     * Html oldalban a játékos nevének a változóneve.
     */
    public static final String HTML_PLAYER_NAME_VARIABLE_NAME = "PLAYER_NAME";
    /**
     * Html oldalban a chat üzenetek letöltésének a változóneve.
     */
    public static final String HTML_CHAT_MESSAGES_VARIABLE_NAME = "DOWNLOADING_CHAT_MESSAGES";

    @Override
    public void validateUser(HttpServletRequest request, HttpServletResponse response, User user, int requestType) throws IOException {

        Log.debug("validateUser: sID=" + request.getSession().getId());

        // Ha a felhasználó hitelesítése nem megy akkor átirányítjuk a bejelentkező ablakhoz.
        if (null == user) {
            Log.debug("User is not valid: sID=" + request.getSession().getId());
            response.sendRedirect(HttpServer.LOGIN_PATH);
            return;
        }

        // Egyébként mehet a munka.
        // Oldalt kizárólag GET kérések esetében szolgálunk ki.
        if (requestType == HttpServer.GET_REQUEST) {

            // Ha a felhasználónak már van nyitott csatornája, akkor hibával kilépünk.
            if (0 < user.getAsyncChannelsCount()) {
                Log.debug("User is valid but already connected: name=" + user.getUserName() + " sID=" + user.getSessionId());
                response.setContentType("text/html");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().append(
                        "<!DOCTYPE html><html>"
                        + "<head>"
                        + "  <meta charset='UTF-8'>"
                        + "  <title>" + HttpServer.APP_TITLE + "</title>"
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

            // Játékfelület kiszolgálása.
            appendDefaultPage(request, response, "");

            return;
        }

        // POST kérés esetén szinkronizációs adatokkal van dolgunk.
        String jsonString = request.getReader().readLine();
        Log.debug("POST[" + user.getUserName() + "]: " + jsonString);

        // Üres kérés esetén csak csatornát tárolunk.
        if (null == jsonString) {
            user.storeAsyncContext(request);
            return;
        }

        // Feldolgozás.
        Gson gson = new Gson();
        Map<String, Object> root = gson.fromJson(jsonString, Map.class);

        // Az "a" kulcs tárolja a végrehajtandó parancsot.
        switch (root.get("a").toString()) {
            // Játéktér szinkronizálása (GetSceneData)
            case "gsd":
                user.sendMessage(new Meteor(1, 4, 20, "imruf84", 1, 0, 0, 0, 0).toJson());
                user.sendMessage(new Meteor(2, 4, 30, "imruf84", 1, 50, 0, 0, 0).toJson());
                user.sendMessage(new Meteor(3, 4, 10, "imruf84", 1, 0, 50, 0, 0).toJson());
                break;
            // Chat üzenetek lekérése (GetChatMessages).
            case "gcm":
                try {
                    LinkedTreeMap data = (LinkedTreeMap) root.get("d");
                    int msgCount = (int) Float.parseFloat(data.get("mc").toString());
                    user.sendMessage(Universe.getUniverse().getMessagesAsJson(msgCount));
                } catch (SQLException ex) {
                    // Hiba esetén visszaküldjük a felhasználónak a hiba tényét.
                    user.sendMessage(Language.getText(LanguageMessageKey.STORE_MESSAGE_FAILED));
                }

                break;
            // Játékállapot lekérdezése (GetGameState).
            case "ggs":
                user.sendMessage(Game.getStateChangedMessage());
                break;
            // Chat üzenet küldése (ChatMessage).
            case "cm":
                LinkedTreeMap data = (LinkedTreeMap) root.get("d");
                String msg = data.get("msg").toString();

                try {
                    // Üzenet tárolása.
                    Universe.getUniverse().storeMessage(user, msg);
                    // Új üzenet szétküldéss mindenkinek.
                    UserManager.sendMessageToAll(MyJson.chatMessage(user.getUserName(), msg));
                } catch (SQLException ex) {
                    // Hiba esetén visszaküldjük a felhasználónak a hiba tényét.
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
