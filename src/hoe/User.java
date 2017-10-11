package hoe;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Felhasználó.
 *
 * @author imruf84
 */
public class User {

    /**
     * Felhasználói név session azonosítójának neve.
     */
    public static String USER_NAME_SESSION_KEY = "user";
    /**
     * Válaszcsatorna long pollinghoz.
     */
    private final AtomicReference<LinkedList<AsyncContext>> context = new AtomicReference<>(new LinkedList<>());
    /**
     * Session azonosító.
     */
    private final String sessionId;
    /**
     * Felhasználó neve.
     */
    private final String userName;
    /**
     * Felhasználó jelszava.
     */
    private final String password;
    /**
     * Küldendő üzenetek.
     */
    private final LinkedList<String> messages = new LinkedList<>();

    
    /**
     * Konstruktor.
     *
     * @param sessionId session azonosító
     * @param userName felhasználó neve
     * @param password jelszó
     */
    public User(final String sessionId, final String userName, final String password) {
        this.sessionId = sessionId;
        this.userName = userName.replaceAll("\"", "\\\\\"").replaceAll("'", "\\\\'");
        this.password = password;
    }

    /**
     * Session azonosító lekérdezése.
     *
     * @return session azonosító
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Felhasználó hitelességének a vizsgálata.
     *
     * @param request kérelem
     * @return igaz esetén a felhasználó hiteles, egyébként nem
     */
    public boolean isValid(final HttpServletRequest request) {

        HttpSession session = request.getSession();
        String lUserName = (String) session.getAttribute(USER_NAME_SESSION_KEY);

        if (null == lUserName) {
            return false;
        }

        return lUserName.equals(getUserName());
    }
    
    /**
     * Játékos aktív-e?
     * 
     * @param request kérés
     * @return igaz esetén aktív egyébként hamis
     */
    public boolean isActive(final HttpServletRequest request) {
        return isValid(request) && hasActiveContext();
    }
    
    /**
     * Rendelkezik-e aktív kapcsolattal?
     * 
     * @return ha rendelkezik akkor igaz, egyébként hamis
     */
    public boolean hasActiveContext() {
        return (context.get().size() > 0);
    }

    /**
     * Felhasználó nevének a lekérdezése.
     *
     * @return felhasználó neve
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Jelszó lekérdezése.
     * 
     * @return jelszó
     */
    public String getPassword() {
        return password;
    }

    /**
     * Üzenet küldése a felhasználónak.
     *
     * @param msg üzenet
     * @throws IOException kivétel
     */
    public void sendMessage(final String msg) throws IOException {

        // Üzenet tárolása.
        synchronized (messages) {
            messages.add(msg);
        }

        // Üzenet(ek) küldése.
        flushMessages();
    }

    /**
     * Aszinkron szál tárolása (long polling válasz csatorna).
     *
     * @param r kérés
     * @throws IOException kivétel
     */
    public void storeAsyncContext(final HttpServletRequest r) throws IOException {

        // Ha nincs tárolandó dolog, akkor csak az üzeneteket továbbítjuk.
        if (null == r) {
            flushMessages();
            return;
        }

        // Események regisztrálása.
        AsyncContext c = r.startAsync();
        c.setTimeout(20000);
        c.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent ae) throws IOException {
                Log.debug("onComplete[" + getUserName() + "]:" + ae.toString());
                removeContext(c);
            }

            @Override
            public void onTimeout(AsyncEvent ae) throws IOException {
                Log.debug("onTimeout[" + getUserName() + "]:" + ae.toString());
                c.complete();
                removeContext(c);
            }

            @Override
            public void onError(AsyncEvent ae) throws IOException {
                Log.debug("onError[" + getUserName() + "]:" + ae.toString());
                removeContext(c);
            }

            @Override
            public void onStartAsync(AsyncEvent ae) throws IOException {
                Log.debug("onStartAsync[" + getUserName() + "]:" + ae.toString());
            }
        });

        synchronized (context) {

            // Előző csatornák törlése.
            if (0 < context.get().size()) {
                flushContexts();
            }

            // Új csatorna tárolása.
            context.get().add(c);
            Log.debug("storeContext[" + getUserName() + ":" + context.get().size() + "]:" + c.toString());

            // Aszinkron szál nyitása.
            c.start(() -> {
            });
        }

        // Majd továbbítjuk a felhalmozott üzeneteket.
        flushMessages();
    }

    /**
     * Csatorna törlése.
     *
     * @param c kapcsolat
     */
    private void removeContext(AsyncContext c) {
        synchronized (context) {
            context.get().remove(c);
            Log.debug("removeContext[" + getUserName() + ":" + context.get().size() + "]:" + c.toString());
        }
    }

    /**
     * Csatornák befejezése.
     */
    public void flushContexts() {

        // Válaszcsatornák zárása.
        synchronized (context) {
            for (AsyncContext ac : context.get()) {
                ac.complete();
            }
        }
    }

    /**
     * Üzenetek kiküldése.
     *
     * @throws IOException kivétel
     */
    private void flushMessages() throws IOException {

        synchronized (messages) {

            // Ha nincs küldendő üzenet, akkor kilépünk.
            if (messages.isEmpty()) {
                return;
            }

            synchronized (context) {

                // Ha nincs aktív nyitott csatorna, akkor kilépünk.
                if (0 == context.get().size()) {
                    return;
                }

                // Üzenetek összefűzése.
                // TODO: egyidőben csak adott számú üzenet összefésülése küldésre, hogy elkerüljük a nagyon nagy mennyiségű adatküldést
                String s = Arrays.toString(messages.toArray());
                messages.clear();

                // Küldés.
                for (AsyncContext ac : context.get()) {
                    ServletResponse sr = ac.getResponse();
                    sr.setContentType("text/json");
                    sr.setCharacterEncoding("UTF-8");
                    sr.getWriter().append(s);

                    // Válaszcsatorna zárása.
                    ac.complete();
                }

            }

        }

    }

    /**
     * Aktív kapcsolatok számának a lekérdezése.
     *
     * @return aktív kapcsolatok száma
     */
    public int getAsyncChannelsCount() {
        return context.get().size();
    }

    @Override
    public String toString() {
        return "User{" + "sessionId=" + sessionId + ", userName=" + userName + '}';
    }

}
