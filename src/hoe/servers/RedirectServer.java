package hoe.servers;

import hoe.servlets.SubscribeServlet;
import hoe.servlets.RedirectServlet;
import hoe.Log;
import hoe.SceneManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class RedirectServer extends AbstractServer {

    public static final String REDIRECT_SERVLET_PATH = "/redirect/";
    public static final String SUBSCRIBE_SERVLET_PATH = "/subscribe/";
    
    private final HashMap<String, LinkedList<String>> clients = new HashMap<>();

    public RedirectServer(String ip, int port) throws ClassNotFoundException, SQLException {
        super(null, ip, port);

        initClientsContainer();
        
        SceneManager.init();
        
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        getServer().setHandler(context);
        context.addServlet(new ServletHolder(new RedirectServlet(this)), REDIRECT_SERVLET_PATH + "*");
        context.addServlet(new ServletHolder(new SubscribeServlet(this)), SUBSCRIBE_SERVLET_PATH + "*");
    }

    @Override
    public void start() throws Exception {
        super.start();
        Log.info("Redirect server is listening on port " + getPort() + "...");
    }

    public HashMap<String, LinkedList<String>> getClients() {
        return clients;
    }

    private void initClientsContainer() {
        getClients().put(SubscribeRequest.CONTENT_SERVER_TYPE, new LinkedList<>());
        getClients().put(SubscribeRequest.GAME_SERVER_TYPE, new LinkedList<>());
        getClients().put(SubscribeRequest.RENDER_SERVER_TYPE, new LinkedList<>());
    }

}
