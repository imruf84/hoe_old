package hoe.servlets;

import hoe.Cryptography;
import hoe.servers.AbstractServer;
import hoe.servers.GameServer;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;

public abstract class HttpServletWithEncryption extends HttpServletBase {

    public HttpServletWithEncryption(AbstractServer server) {
        super(server);
    }

    protected abstract <T> void handleRequest(HttpServletRequest request, HttpServletResponse response, T action, int requestType) throws IOException;

    protected <T> void doEncription(HttpServletRequest request, HttpServletResponse response, int requestType) throws IOException {

        T ra = null;

        if (request.getPathInfo() != null) {
            String pa[] = request.getPathInfo().split("/");
            if (pa.length > 1) {
                ra = Cryptography.decryptObject(pa[1]);
            }
        }

        if (ra == null) {
            response.reset();
            response.setStatus(HttpStatus.BAD_REQUEST_400);
            return;
        }

        handleRequest(request, response, ra, requestType);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doEncription(request, response, GameServer.GET_REQUEST);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doEncription(request, response, GameServer.POST_REQUEST);
    }
}
