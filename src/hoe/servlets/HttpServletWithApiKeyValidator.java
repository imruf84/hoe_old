package hoe.servlets;

import hoe.servers.GameServer;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class HttpServletWithApiKeyValidator extends HttpServlet {

    protected abstract void handleRequest(HttpServletRequest request, HttpServletResponse response, String apiKey, int requestType) throws IOException;
    
    protected boolean isApiKeyValid(HttpServletRequest request, HttpServletResponse response, String apiKey, int requestType) {
        return true;
    }
    
    protected void doApiKeyValidation(HttpServletRequest request, HttpServletResponse response, int requestType) throws IOException {
        
        String apiKey = "test_api_key";
        
        if (!isApiKeyValid(request, response, apiKey, requestType)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        handleRequest(request, response, apiKey, requestType);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doApiKeyValidation(request, response, GameServer.GET_REQUEST);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doApiKeyValidation(request, response, GameServer.POST_REQUEST);
    }
}
