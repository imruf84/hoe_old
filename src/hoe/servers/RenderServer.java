package hoe.servers;

import hoe.Log;
import hoe.servlets.RenderServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class RenderServer extends AbstractServer {

    public static final String RENDER_PATH = "/render";

    public RenderServer(int port) {
        super(port);

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        getServer().setHandler(context);
        context.addServlet(new ServletHolder(new RenderServlet()), RENDER_PATH);
    }

    @Override
    public void start() throws Exception {
        super.start();
        Log.info("Render server is listening at port " + getPort() + "...");
    }

}
