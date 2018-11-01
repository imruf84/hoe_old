package hoe.servers;

import hoe.servlets.SubscribeServlet;
import hoe.servlets.RedirectServlet;
import hoe.Log;
import java.util.LinkedList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class RedirectServer extends AbstractServer {

    public static final String REDIRECT_SERVLET_PATH = "/redirect/";
    public static final String SUBSCRIBE_SERVLET_PATH = "/subscribe/";
    
    private final LinkedList<String> clients = new LinkedList<>();

    public RedirectServer(String ip, int port) {
        super(null, ip, port);

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        getServer().setHandler(context);
        context.addServlet(new ServletHolder(new RedirectServlet(this)), REDIRECT_SERVLET_PATH + "*");
        context.addServlet(new ServletHolder(new SubscribeServlet(this)), SUBSCRIBE_SERVLET_PATH + "*");
    }

    @Override
    public void start() throws Exception {
        super.start();
        Log.info("Redirect server is listening at port " + getPort() + "...");
    }

    public LinkedList<String> getClients() {
        return clients;
    }

}
