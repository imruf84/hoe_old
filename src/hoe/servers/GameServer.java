package hoe.servers;

import hoe.FileSessionManager;
import hoe.GameServlet;
import hoe.Log;
import hoe.SceneManager;
import hoe.UserManager;
import hoe.servlets.GetIpServlet;
import hoe.servlets.TileServlet;
import hoe.servlets.VideoServlet;
import hoe.servlets.LogoutServlet;
import hoe.servlets.LoginServlet;
import hoe.servlets.PlayServlet;
import hoe.servlets.RegisterServlet;
import java.io.File;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class GameServer extends AbstractServer {

    public static final String APP_TITLE = "Handful of Earth";
    private static final String SESSION_COOKIE = "hoesession";
    public static final String LOGIN_PATH = "/";
    public static final String LOGOUT_PATH = "/logout";
    public static final String PLAY_PATH = "/play";
    public static final String GAME_PATH = "/game/";
    public static final String REGISTER_PATH = "/register";
    public static final String GET_TILE_PATH = "/tile/";
    public static final String GET_VIDEO_PATH = "/video/";
    public static final String GET_IP_PATH = "/getip";
    public static final String DO_RENDER_PATH = "/render/";
    public static int POST_REQUEST = 0;
    public static int GET_REQUEST = 1;
    private final FileSessionManager sm;

    public GameServer(String ip, int port) throws Exception {
        super(SubscribeRequest.GAME_SERVER_TYPE, ip, port);

        SceneManager.init();
        GameServer gs = this;
        GameServlet.init(gs);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        getServer().setHandler(context);
        ServletHolder psh = new ServletHolder(new PlayServlet(this));
        psh.setAsyncSupported(true);
        context.addServlet(psh, PLAY_PATH);
        context.addServlet(new ServletHolder(new GameServlet(this)), GAME_PATH+"*");
        context.addServlet(new ServletHolder(new LoginServlet(this)), LOGIN_PATH);
        context.addServlet(new ServletHolder(new LogoutServlet()), LOGOUT_PATH);
        context.addServlet(new ServletHolder(new RegisterServlet(this)), REGISTER_PATH);
        context.addServlet(new ServletHolder(new TileServlet(this)), GET_TILE_PATH + "*");
        context.addServlet(new ServletHolder(new VideoServlet(this)), GET_VIDEO_PATH + "*");
        context.addServlet(new ServletHolder(new GetIpServlet(this)), GET_IP_PATH);

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

    @Override
    public void start() throws Exception {
        super.start();
        Log.info("HTTP server is listening at port " + getPort() + "...");

        // Restoring sessions from file.
        UserManager.init(sm);
        
        // Start the game.
        GameServlet.start();
    }

}
