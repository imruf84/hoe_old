package hoe.servers;

import hoe.Log;
import hoe.servlets.RenderServlet;
import java.util.Collections;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;

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
