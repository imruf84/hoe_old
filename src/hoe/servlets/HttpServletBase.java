package hoe.servlets;

import hoe.servers.AbstractServer;
import javax.servlet.http.HttpServlet;

public class HttpServletBase extends HttpServlet {

    private final AbstractServer server;

    public HttpServletBase(AbstractServer server) {
        this.server = server;
    }

    public AbstractServer getServer() {
        return server;
    }

}
