package hoe;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.eclipse.jetty.server.session.HashedSession;

public class UserManager {

    private static final HashMap<String, User> USERS_BY_SESSION_ID = new HashMap<>();
    private static FileSessionManager sessionManager;
    private static UsersDataBase usersDb;
    private static String dataBaseIP;

    public static void init(FileSessionManager sm) throws ClassNotFoundException, SQLException {
        setSessionManager(sm);
        connectToDataBase();
    }

    private static void setSessionManager(FileSessionManager sm) {
        sessionManager = sm;

        // Storing users from session.
        for (HashedSession hs : sm.getSessionsMap().values()) {
            User user = new User(hs.getId(), hs.getAttribute(User.USER_NAME_SESSION_KEY).toString(), null);
            USERS_BY_SESSION_ID.put(user.getSessionId(), user);
        }
    }

    protected static void connectToDataBase() throws ClassNotFoundException, SQLException {
        usersDb = new UsersDataBase(getDataBaseIp());
    }

    public static boolean userIsValid(final HttpServletRequest request) {
        User user = getUserBySessionId(request.getSession().getId());
        if (null == user) {
            return false;
        }

        return user.isValid(request);
    }

    public static boolean userIsActive(final HttpServletRequest request) {
        User user = getUserBySessionId(request.getSession().getId());
        if (null == user) {
            return false;
        }

        return user.isActive(request);
    }

    public static User getUserBySessionId(final String sessionId) {
        return USERS_BY_SESSION_ID.get(sessionId);
    }

    public static User getUserBySession(final HttpServletRequest request) {
        return getUserBySessionId(request.getSession().getId());
    }

    public static User getUserByName(final String name) {
        synchronized (USERS_BY_SESSION_ID) {
            for (User u : USERS_BY_SESSION_ID.values()) {
                if (u.getUserName().equals(name)) {
                    return u;
                }
            }
        }

        return null;
    }

    public static UsersDataBase getUsersDataBase() {
        return usersDb;
    }

    public static boolean isUserStored(User user) throws SQLException {
        return getUsersDataBase().isUserStored(user);
    }

    public static synchronized boolean storeUser(User user) throws SQLException {
        return getUsersDataBase().storeUser(user);
    }

    public static User loginUser(final HttpServletRequest request, final User user) throws Exception {

        // Chck user's password.
        if (!getUsersDataBase().validateUser(user)) {
            // Exit on wrong password.
            Log.debug("Invalid password: " + user.getUserName());
            return null;
        }

        // Checking user in session.
        HttpSession session = request.getSession();
        session.setAttribute(User.USER_NAME_SESSION_KEY, user.getUserName());

        // Storing user.
        synchronized (USERS_BY_SESSION_ID) {
            USERS_BY_SESSION_ID.put(session.getId(), user);
        }

        // Storing sessions to file.
        sessionManager.saveSessions(true);

        Log.debug("User logged in: " + user.getUserName() + " sID: " + session.getId());

        return user;
    }

    public static void logoutUser(String sessionID) throws Exception {

        boolean invalidated = false;

        synchronized (USERS_BY_SESSION_ID) {
            if (USERS_BY_SESSION_ID.containsKey(sessionID)) {
                User user = getUserBySessionId(sessionID);
                // Finish all the thread because of long polling.
                USERS_BY_SESSION_ID.get(sessionID).flushContexts();
                // Remove user.
                USERS_BY_SESSION_ID.remove(sessionID);

                // Invalidate session.
                invalidated = sessionManager.invalidate(user.getUserName());

                Log.debug("User logged out: " + user.getUserName());
            }
        }

        // Storing session to file.
        if (invalidated) {
            sessionManager.saveSessions(true);
        }

    }

    public static void logoutUser(final HttpServletRequest request) throws IOException, Exception {

        HttpSession session = request.getSession();
        String sid = session.getId();

        logoutUser(sid);
    }

    public static void sendMessageToAll(final String msg) throws IOException {
        synchronized (USERS_BY_SESSION_ID) {
            for (User u : USERS_BY_SESSION_ID.values()) {
                u.sendMessage(msg);
            }
        }
    }

    public static void setDataBaseIp(String ip) {
        dataBaseIP = ip;
    }

    private static String getDataBaseIp() {
        return dataBaseIP;
    }

}
