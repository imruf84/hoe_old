package hoe.servlets;

import hoe.Log;
import hoe.servers.AbstractServer;
import hoe.servers.RedirectServer;
import hoe.servers.SubscribeRequest;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;

public class SubscribeServlet extends HttpServletWithEncryption {

    public SubscribeServlet(AbstractServer server) {
        super(server);
    }

    @Override
    protected <T> void handleRequest(HttpServletRequest request, HttpServletResponse response, T action, int requestType) throws IOException {

        SubscribeRequest sr = (SubscribeRequest) action;

        response.reset();
        response.setStatus(HttpStatus.OK_200);
        String url = "http://" + sr.getIp() + ":" + sr.getPort();
        RedirectServer server = (RedirectServer) getServer();
        switch (sr.getType()) {
            case SubscribeRequest.CONTENT_SERVER_TYPE:
                if (sr.isUnsubscribe()) {
                    if (server.getClients().contains(url)) {
                        server.getClients().remove(url);
                        Log.info("Client is unsubscribed [" + url + "]");
                    }
                } else {
                    if (!server.getClients().contains(url)) {
                        server.getClients().add(url);
                        Log.info("Client is subscribed [" + url + "]");
                    }
                }
                break;
        }
        response.getWriter().append(url);

    }

}
