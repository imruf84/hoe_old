package hoe;

import java.io.File;
import javax.servlet.http.HttpServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * HTTP szerver.
 *
 * @author imruf84
 */
public class HttpServer extends HttpServlet {

    /**
     * Alkalmazás neve.
     */
    public static final String APP_TITLE = "Handful of Earth";
    /**
     * Session kulcs.
     */
    private static final String SESSION_COOKIE = "hoesession";
    /**
     * Bejelentkező oldal elérési útja.
     */
    public static final String LOGIN_PATH = "/";
    /**
     * Kijelentkező oldal elérési útja.
     */
    public static final String LOGOUT_PATH = "/logout";
    /**
     * Játék oldalának elérési útja.
     */
    public static final String PLAY_PATH = "/play";
    /**
     * Új felhasználó regisztrációjának az elérési útja.
     */
    public static final String REGISTER_PATH = "/register";
    /**
     * Csempék elérési útja.
     */
    public static final String TILE_PATH = "/tile/*";
    /**
     * Post kérés.
     */
    public static int POST_REQUEST = 0;
    /**
     * Get kérés.
     */
    public static int GET_REQUEST = 1;

    /**
     * Konstruktor.
     *
     * @param port port
     * @throws Exception kivétel
     */
    public HttpServer(int port) throws Exception {

        // Üzenetek toltása.
        org.eclipse.jetty.util.log.Log.setLog(new NothingLogger());
        
        // Játéktér inicializálása.
        Universe.init();
        // Játékmenet inicializálása.
        Game.init();

        // Szerver létrehozása.
        Server server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.setContextPath("/");
        server.setHandler(context);
        ServletHolder psh = new ServletHolder(new PlayServlet());
        psh.setAsyncSupported(true);
        context.addServlet(psh, PLAY_PATH);
        context.addServlet(new ServletHolder(new LoginServlet()), LOGIN_PATH);
        context.addServlet(new ServletHolder(new LogoutServlet()), LOGOUT_PATH);
        context.addServlet(new ServletHolder(new RegisterServlet()), REGISTER_PATH);
        context.addServlet(new ServletHolder(new TileServlet()), TILE_PATH);

        MySessionManager sm = new MySessionManager();
        sm.setSessionCookie(SESSION_COOKIE + port);
        sm.setStoreDirectory(new File("./sessions"));
        SessionHandler sh = new SessionHandler(sm);
        context.setSessionHandler(sh);

        server.start();
        Log.info("HTTP server is listening at port " + port + "...");
        
        // Sessionok visszaállítása fájlból.
        UserManager.init(sm);

    }

}
