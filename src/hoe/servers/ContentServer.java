package hoe.servers;

import hoe.servlets.ContentServlet;
import hoe.Log;
import hoe.SceneManager;
import java.io.File;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class ContentServer extends AbstractServer {

    public static final String CONTENT_PATH = "/content/";
    public static final String TILES_CACHE_PATH = "./content/tiles/";

    public ContentServer(String ip, int port) throws Exception {
        super(SubscribeRequest.CONTENT_SERVER_TYPE, ip, port);

        File contentDirectory = new File(TILES_CACHE_PATH);
        if (!contentDirectory.exists()) {
            contentDirectory.mkdirs();
        }

        SceneManager.init();

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        getServer().setHandler(context);
        context.addServlet(new ServletHolder(new ContentServlet(this)), CONTENT_PATH + "*");
    }

    @Override
    public void start() throws Exception {
        super.start();
        Log.info("Content server is listening on port " + getPort() + "...");
    }

}
