package hoe.servers;

import hoe.Log;
import hoe.servlets.HttpServletWithEncryption;
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
        String url = "http://" + request.getRemoteAddr() + ":" + sr.getPort();
        Log.info("Client subscribed [" + url + "]");
        RedirectServer server = (RedirectServer) getServer();
        if (!server.getClients().contains(url)) {
            server.getClients().add(url);
        }
        response.getWriter().append(url);

    }

}
