package hoe.servlets;

import hoe.Log;
import hoe.servers.AbstractServer;
import hoe.servers.RedirectServer;
import hoe.servers.SubscribeRequest;
import java.io.IOException;
import java.util.LinkedList;
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

        String clientType = sr.getType();
        LinkedList<String> clients = server.getClients().get(clientType);
        if (sr.isUnsubscribe()) {
            if (clients.contains(url)) {
                server.getClients().remove(url);
                Log.info("Client (" + clientType + ") is unsubscribed [" + url + "]");
            }
        } else {
            if (!clients.contains(url)) {
                clients.add(url);
                Log.info("Client (" + clientType + ") is subscribed [" + url + "]");
            }
        }
        response.getWriter().append(url);

    }

}
