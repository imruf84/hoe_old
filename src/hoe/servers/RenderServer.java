package hoe.servers;

import hoe.Log;
import hoe.SceneManager;
import hoe.servlets.RenderServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class RenderServer extends AbstractServer {

    public static final String RENDER_PATH = "/render";

    public RenderServer(String ip, int port) throws Exception {
        super(SubscribeRequest.RENDER_SERVER_TYPE, ip, port);

        SceneManager.init();
        
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        getServer().setHandler(context);
        context.addServlet(new ServletHolder(new RenderServlet(this)), RENDER_PATH);
    }

    @Override
    public void start() throws Exception {
        super.start();
        Log.info("Render server is listening on port " + getPort() + "...");
    }

}
