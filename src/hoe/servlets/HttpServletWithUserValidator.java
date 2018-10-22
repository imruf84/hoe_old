package hoe.servlets;

import hoe.User;
import hoe.UserManager;
import hoe.servers.GameServer;
import hoe.servlets.MyHttpServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class HttpServletWithUserValidator extends MyHttpServlet {

    public abstract void validateUser(HttpServletRequest request, HttpServletResponse response, User user, int requestType) throws IOException;

    protected void doUserValidation(HttpServletRequest request, HttpServletResponse response, int requestType) throws IOException {

        setAccessControlHeaders(request, response);

        // Redirectiong to login page.
        if (!UserManager.userIsValid(request)) {
            validateUser(request, response, null, requestType);
            return;
        }

        validateUser(request, response, UserManager.getUserBySession(request), requestType);
    }

    private void setAccessControlHeaders(HttpServletRequest request, HttpServletResponse response) {
        //response.setHeader("Access-Control-Allow-Origin", "http://localhost:8000,http://192.168.0.20:8000");
        //response.setHeader("Access-Control-Allow-Origin", "*");
        //response.setHeader("Access-Control-Allow-Methods", "*");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doUserValidation(request, response, GameServer.GET_REQUEST);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doUserValidation(request, response, GameServer.POST_REQUEST);
    }
}
