package hoe.servers;

import hoe.servlets.ContentServlet;
import hoe.Log;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ContentServer extends AbstractServer {

    public static final String CONTENT_PATH = "/content/";

    public ContentServer(String ip, int port) {
        super(SubscribeRequest.CONTENT_SERVER_TYPE, ip, port);

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        getServer().setHandler(context);
        context.addServlet(new ServletHolder(new ContentServlet(this)), CONTENT_PATH + "*");
    }

    @Override
    public void start() throws Exception {
        super.start();
        Log.info("Content server is listening at port " + getPort() + "...");
    }

}
