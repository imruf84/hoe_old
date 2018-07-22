package hoe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Csempék letöltését vezérlő szervlet osztálya.
 * 
 * @author igalambo
 */
public class TileServlet extends HttpServletWithUserValidator {

    @Override
    public void validateUser(HttpServletRequest request, HttpServletResponse response, User user, int requestType) throws IOException {
     
        System.out.println(request.getPathInfo());
        
        // Ha a felhasználó nincs hitelesítve akkor nem küldünk semmit.
        if (null == user) {
            return;
        }
        
        // Fájl megnyitása.
        File image = new File("assets/tiles/test.jpg");

        // Ha nem létezik a fájl akkor kilépünk.
        if (!image.exists()) {
            return;
        }

        // Kép küldése.
        response.reset();
        response.setContentType(getServletContext().getMimeType(image.getName()));
        response.setHeader("Content-Length", String.valueOf(image.length()));
        Files.copy(image.toPath(), response.getOutputStream());
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doUserValidation(request, response, HttpServer.GET_REQUEST);
    }

    @Override
    protected String getDefaultPagePath() {
        return "";
    }
    
}
