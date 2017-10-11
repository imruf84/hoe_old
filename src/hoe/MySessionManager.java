package hoe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.ConcurrentMap;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.HashedSession;

/**
 * Saját session tároló/visszaállító.
 *
 * @author imruf84
 */
public class MySessionManager extends HashSessionManager {

    @Override
    public void saveSessions(boolean reactivate) throws Exception {

        for (HashedSession session : _sessions.values()) {

            // Ha a session nem tartalmazza a felhasználó nevét akkor nem mentjük.
            if (!session.getNames().contains("user")) {
                continue;
            }

            // Session tárolása fájlba.
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(getStoreDirectory().getPath() + "/" + session.getId()))) {
                for (String s : session.getNames()) {
                    bw.write(session.getClusterId());
                    bw.newLine();
                    bw.write(Long.toString(session.getCreationTime()));
                    bw.newLine();
                    bw.write(Long.toString(session.getAccessed()));
                    bw.newLine();
                    bw.write(Integer.toString(session.getRequests()));
                    bw.newLine();
                    bw.write(s);
                    bw.newLine();
                    bw.write("" + session.getAttributeMap().get(s));
                    bw.newLine();
                }

                Log.debug("Session stored to " + getStoreDirectory().getPath() + "/" + session.getId());
            }
        }

    }

    @Override
    public void restoreSessions() throws Exception {

        for (final File file : getStoreDirectory().listFiles()) {
            if (file.isFile()) {
                try (BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
                    String ID = br.readLine();
                    long created = Long.parseLong(br.readLine());
                    long accessed = Long.parseLong(br.readLine());
                    int requests = Integer.parseInt(br.readLine());
                    HashedSession hs = (HashedSession) newSession(created, accessed, ID);
                    hs.setRequests(requests);
                    // 'user' kulcs és a felhasználó nevének párosa
                    hs.setAttribute(br.readLine(), br.readLine());
                    addSession(hs, true);
                }
            }
        }

        Log.debug(_sessions.values().size() + " session(s) restored.");
    }

    /**
     * Session tároló lekérdezése.
     *
     * @return sessionok tárolója
     */
    public ConcurrentMap<String, HashedSession> getSessionsMap() {
        return _sessions;
    }

    /**
     * Session érvénytelenítése felhasználó alapján.
     *
     * @param userName felhasználó neve
     * @return sikeres érvénytelenítés esetén igaz, egyébként hamis
     */
    public boolean invalidate(String userName) {

        boolean b = false;

        for (String key : getSessionsMap().keySet()) {
            HashedSession hs = getSessionsMap().get(key);
            Object attr = hs.getAttribute(User.USER_NAME_SESSION_KEY);
            if (null != attr) {
                String name = attr.toString();
                if (name.toUpperCase().equals(userName.toUpperCase())) {
                    Log.debug("Invalidate user session: " + name);
                    hs.invalidate();
                    b = true;
                }
            }
        }

        return b;
    }

}
