package hoe;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Szervlet felhasználó azonosítóval.
 *
 * @author imruf84
 */
public abstract class HttpServletWithUserValidator extends MyHttpServlet {

    /**
     * Felhasználó érvényességének a meghatározása.
     *
     * @param request kérelem
     * @param response válasz
     * @param user felhasználó vagy érvénytelen felhasználó esetén null
     * @param requestType kérés típusa
     * @throws IOException kivétel
     */
    public abstract void validateUser(HttpServletRequest request, HttpServletResponse response, User user, int requestType) throws IOException;

    /**
     * Felhasználó érvényességének a meghatározása.
     *
     * @param request kérelem
     * @param response válasz
     * @param requestType kérés típusa
     * @throws IOException kivétel
     */
    protected void doUserValidation(HttpServletRequest request, HttpServletResponse response, int requestType) throws IOException {
        
        setAccessControlHeaders(request, response);
        
        // Ha a felhasználó hitelesítése nem megy akkor átirányítjuk a bejelentkező ablakhoz.
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
        doUserValidation(request, response, HttpServer.GET_REQUEST);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doUserValidation(request, response, HttpServer.POST_REQUEST);
    }
}
