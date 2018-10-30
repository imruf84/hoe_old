package hoe.servlets;

import hoe.servers.AbstractServer;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetIpServlet extends HttpServletBase {

    public GetIpServlet(AbstractServer server) {
        super(server);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        String ip = request.getRemoteAddr();
        response.getWriter().append(ip + "\n");
    }

}
