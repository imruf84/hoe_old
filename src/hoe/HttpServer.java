package hoe;

import hoe.servlets.TileServlet;
import hoe.servlets.VideoServlet;
import hoe.servlets.LogoutServlet;
import hoe.servlets.LoginServlet;
import hoe.servlets.PlayServlet;
import hoe.servlets.RegisterServlet;
import java.io.File;
import javax.servlet.http.HttpServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class HttpServer extends HttpServlet {

    public static final String APP_TITLE = "Handful of Earth";
    private static final String SESSION_COOKIE = "hoesession";
    public static final String LOGIN_PATH = "/";
    public static final String LOGOUT_PATH = "/logout";
    public static final String PLAY_PATH = "/play";
    public static final String REGISTER_PATH = "/register";
    public static final String TILE_PATH = "/tile/*";
    public static final String VIDEO_PATH = "/video/*";
    public static final String GET_IP_PATH = "/getip";
    public static int POST_REQUEST = 0;
    public static int GET_REQUEST = 1;
    private final Server server;
    private final FileSessionManager sm;
    private final int port;

    public HttpServer(int port) throws Exception {
        this.port = port;

        org.eclipse.jetty.util.log.Log.setLog(new NothingLogger());

        SceneManager.init();
        Game.init();

        server = new Server(getPort());
        //Server server = new Server(new InetSocketAddress("192.168.0.20", 80));

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
        context.addServlet(new ServletHolder(new VideoServlet()), VIDEO_PATH);
        context.addServlet(new ServletHolder(new GetIpServlet()), GET_IP_PATH);

        /*FilterHolder filter = new FilterHolder(CrossOriginFilter.class);
        filter.setInitParameter("allowedOrigins", "*");
        filter.setInitParameter("allowedMethods", "*");
	filter.setInitParameter("allowedHeaders", "*");
        context.addFilter(filter, "/*", EnumSet.of(DispatcherType.REQUEST));*/
 /*FilterHolder holder = new FilterHolder(CrossOriginFilter.class);
        holder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        holder.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        holder.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        holder.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "*");
        holder.setName("cross-origin");
        FilterMapping fm = new FilterMapping();
        fm.setFilterName("cross-origin");
        fm.setPathSpec("*");
        context.addFilter(holder, "*", null);*/
        sm = new FileSessionManager();
        sm.setSessionCookie(SESSION_COOKIE + port);
        sm.setStoreDirectory(new File("./sessions"));
        SessionHandler sh = new SessionHandler(sm);
        context.setSessionHandler(sh);
    }

    public final int getPort() {
        return port;
    }

    public void start() throws Exception {
        server.start();
        Log.info("HTTP server is listening at port " + getPort() + "...");

        // Restoring sessions from file.
        UserManager.init(sm);
    }

}
