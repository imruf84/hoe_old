package hoe;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.eclipse.jetty.server.session.HashedSession;

/**
 * Felhasználó kezelő.
 *
 * @author imruf84
 */
public class UserManager {

    /**
     * Felhasználók tárolója session azonosító alapján.
     */
    private static final HashMap<String, User> USERS_BY_SESSION_ID = new HashMap<>();
    /**
     * Session kezelő.
     */
    private static MySessionManager sessionManager;
    /**
     * Felhasználói adatbázis.
     */
    private static UsersDataBase usersDataBase;

    /**
     * Inicializálás.
     *
     * @param sm session kezelő
     * @throws ClassNotFoundException kivétel
     * @throws SQLException kivétel
     */
    public static void init(MySessionManager sm) throws ClassNotFoundException, SQLException {
        setSessionManager(sm);
        connectToDataBase();
    }

    /**
     * Session kezelő tárolása.
     *
     * @param sm session kezelő
     */
    private static void setSessionManager(MySessionManager sm) {
        sessionManager = sm;

        // Felhasználók tárolása sessionokből.
        for (HashedSession hs : sm.getSessionsMap().values()) {
            User user = new User(hs.getId(), hs.getAttribute(User.USER_NAME_SESSION_KEY).toString(), null);
            USERS_BY_SESSION_ID.put(user.getSessionId(), user);
        }
    }

    /**
     * Adatbáziskapcsolat létrehozása.
     *
     * @throws ClassNotFoundException kivétel
     * @throws SQLException kivétel
     */
    private static void connectToDataBase() throws ClassNotFoundException, SQLException {
        usersDataBase = new UsersDataBase();
    }

    /**
     * Felhasználó hitelességének a vizsgálata.
     *
     * @param request kérelem
     * @return hiteles felhasználó esetén igaz, egyébként hamis
     */
    public static boolean userIsValid(final HttpServletRequest request) {
        User user = getUserBySessionId(request.getSession().getId());
        if (null == user) {
            return false;
        }

        return user.isValid(request);
    }

    /**
     * Felhasználó hitelességének és aktivitásának a vizsgálata (azaz hiteles és
     * rendelkezik nyitott kommunikációs csatornával).
     *
     * @param request kérés
     * @return igaz esetén aktív egyébként hamis
     */
    public static boolean userIsActive(final HttpServletRequest request) {
        User user = getUserBySessionId(request.getSession().getId());
        if (null == user) {
            return false;
        }

        return user.isActive(request);
    }

    /**
     * Felhasználó lekérdezése session azonosító alapján.
     *
     * @param sessionId session azonosító
     * @return felhasználó
     */
    public static User getUserBySessionId(final String sessionId) {
        return USERS_BY_SESSION_ID.get(sessionId);
    }

    /**
     * Felhasználó lekérdezése sessionből.
     *
     * @param request kérelem
     * @return felhasználó
     */
    public static User getUserBySession(final HttpServletRequest request) {
        return getUserBySessionId(request.getSession().getId());
    }

    /**
     * Felhasználó lekérdezése név alapján.
     *
     * @param name név
     * @return névhez tartozó felhasználó vagy null, ha nem léétezk ilyen
     */
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

    /**
     * Felhasználó létezésének a legkérdezése.
     *
     * @param user felhasználó
     * @return létező felhasználó esetén igaz egyébként hamis
     * @throws SQLException kivétel
     */
    public static boolean isUserStored(User user) throws SQLException {
        return usersDataBase.isUserStored(user);
    }

    /**
     * Felhasználó tárolása.
     *
     * @param user felhasználó
     * @return sikeres tárolás esetén igaz egyébként hamis
     * @throws SQLException kivétel
     */
    public static synchronized boolean storeUser(User user) throws SQLException {
        return usersDataBase.storeUser(user);
    }

    /**
     * Új felhasználó beléptetése.
     *
     * @param request kérelem
     * @param user felhasználó
     * @return beléptetett felhasználó
     * @throws java.lang.Exception kivétel
     */
    public static User loginUser(final HttpServletRequest request, final User user) throws Exception {

        // Ellenőrizzük a felhasznáól jelszavát.
        if (!usersDataBase.validateUser(user)) {
            // Sikertelen hitelesítés esetén kilépünk.
            Log.debug("Invalid password: " + user.getUserName());
            return null;
        }

        // Felhaszbnáló ellenörzése sessionben.
        HttpSession session = request.getSession();
        session.setAttribute(User.USER_NAME_SESSION_KEY, user.getUserName());

        // Felhasználó tárolása.
        synchronized (USERS_BY_SESSION_ID) {
            USERS_BY_SESSION_ID.put(session.getId(), user);
        }

        // Sessionok tárolása fájlokba.
        sessionManager.saveSessions(true);

        Log.debug("User logged in: " + user.getUserName() + " sID: " + session.getId());

        return user;
    }

    /**
     * Felhasználó kiléptetése session azonosító alapján.
     * 
     * @param sessionID azonosító
     * @throws java.lang.Exception kivétel
     */
    public static void logoutUser(String sessionID) throws Exception {
        
        boolean invalidated = false;
        
        synchronized (USERS_BY_SESSION_ID) {
            if (USERS_BY_SESSION_ID.containsKey(sessionID)) {
                User user = getUserBySessionId(sessionID);
                // Long polling miatt minden függőben lévő szálat le kell futtatni.
                USERS_BY_SESSION_ID.get(sessionID).flushContexts();
                // Felhasználó eltávolítása.
                USERS_BY_SESSION_ID.remove(sessionID);
                
                // Session érvénytelenítése.
                invalidated = sessionManager.invalidate(user.getUserName());
                
                Log.debug("User logged out: " + user.getUserName());
            }
        }
        
        // Sessionok tárolása fájlokba.
        if (invalidated) {
            sessionManager.saveSessions(true);
        }
        
    }
    
    /**
     * Felhasználó kiléptetése kérés alapján.
     *
     * @param request kérelem
     * @throws java.io.IOException kivétel
     */
    public static void logoutUser(final HttpServletRequest request) throws IOException, Exception {
        
        HttpSession session = request.getSession();
        String sid = session.getId();

        logoutUser(sid);
    }

    /**
     * Üzenet küldése minden felhasználónak.
     *
     * @param msg üzenet
     * @throws java.io.IOException kivétel
     */
    public static void sendMessageToAll(final String msg) throws IOException {
        synchronized (USERS_BY_SESSION_ID) {
            for (User u : USERS_BY_SESSION_ID.values()) {
                u.sendMessage(msg);
            }
        }
    }

}
