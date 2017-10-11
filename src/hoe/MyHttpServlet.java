package hoe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Saját kiterjesztéseket tartalmazó osztály.
 *
 * @author imruf84
 */
public abstract class MyHttpServlet extends HttpServlet {

    /**
     * Html oldalak változók előtagja.
     */
    public static final String HTML_TAG_FROM = "#!";
    /**
     * Html oldalak változók utótagja.
     */
    public static final String HTML_TAG_TO = "!#";
    /**
     * Html oldalban a hibaüzenet változóneve.
     */
    public static final String HTML_ERROR_VARIABLE_NAME = "ERROR";
    /**
     * Html oldalban az oldal címének a változóneve.
     */
    public static final String HTML_PAGE_TITLE_VARIABLE_NAME = "PAGE_TITLE";

    /**
     * Alapértelmezett html oldal elérési útjának és nevének a lekérdezése.
     *
     * @return alapértelmezett html oldal elérési útja és neve
     */
    protected abstract String getDefaultPagePath();

    /**
     * Változó nevének összefűzése az elő-, és utótaggal.
     *
     * @param vName változó neve
     * @return összefűzött név
     */
    private String concateVariableNameWithTags(String vName) {
        return MyHttpServlet.HTML_TAG_FROM + vName + MyHttpServlet.HTML_TAG_TO;
    }

    /**
     * Oldal kiszolgálása.
     *
     * @param page oldal
     * @param request kérés
     * @param response válaszobjektum
     * @param error hibaüzenet
     * @throws IOException kivétel
     */
    protected void appendPage(String page, HttpServletRequest request, HttpServletResponse response, String error) throws IOException {

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        InputStream in = getClass().getResourceAsStream(page);
        try (BufferedReader input = new BufferedReader(new InputStreamReader(in))) {
            String s;
            while (null != (s = input.readLine())) {

                // JavaScript linkek keresése.
                Matcher matcherScripts = Pattern.compile("<script\\ssrc=\"(.*\\.js)\"><\\/script>", Pattern.CASE_INSENSITIVE).matcher(s);
                if (matcherScripts.find()) {
                    
                    // Fájlnév kinyerése.
                    String jsFile = matcherScripts.group(1);

                    // Fájl tartalmának a beolvasása.
                    InputStream jsin = getClass().getResourceAsStream("/hoe/html/" + jsFile);
                    try (BufferedReader jsinput = new BufferedReader(new InputStreamReader(jsin))) {
                        String jss;
                        response.getWriter().append("<script>\n");
                        while (null != (jss = jsinput.readLine())) {
                            response.getWriter().append(jss + '\n');
                        }
                        response.getWriter().append("</script>\n");
                    }

                    continue;
                }

                // Változónevek keresése.
                while (s.contains(MyHttpServlet.HTML_TAG_FROM) && s.contains(MyHttpServlet.HTML_TAG_TO)) {
                    String extracted = "";
                    try {
                        extracted = s.substring(MyHttpServlet.HTML_TAG_FROM.length() + s.indexOf(MyHttpServlet.HTML_TAG_FROM), s.indexOf(MyHttpServlet.HTML_TAG_TO));
                        s = s.replaceAll(concateVariableNameWithTags(extracted), insertVariableValue(request, response, extracted));
                    } catch (IllegalArgumentException e) {

                        // Hibaüzenet megjelenítése.
                        if (!error.isEmpty() && extracted.equals(HTML_ERROR_VARIABLE_NAME)) {
                            s = s.replaceAll(concateVariableNameWithTags(HTML_ERROR_VARIABLE_NAME), error);
                            continue;
                        }
                        // Egyéb változók értékének a megadása.
                        s = s.replaceAll(concateVariableNameWithTags(extracted), insertCustomVariableValue(request, response, extracted));

                    } catch (StringIndexOutOfBoundsException e) {
                    }
                }

                response.getWriter().append(s + '\n');
            }
            in.close();

        }
    }

    /**
     * Alapértelmezett oldal kiszolgálása.
     *
     * @param request kérés
     * @param response válaszobjektum
     * @param error hibaüzenet
     * @throws IOException kivétel
     */
    protected void appendDefaultPage(HttpServletRequest request, HttpServletResponse response, String error) throws IOException {

        appendPage(getDefaultPagePath(), request, response, error);
    }

    /**
     * Language osztályban található változó értékének a lekérdezése.
     *
     * @param request kérés
     * @param response válaszobjektum
     * @param v változó neve
     * @return változó értéke
     */
    protected String insertVariableValue(HttpServletRequest request, HttpServletResponse response, String v) {
        return Language.getText(LanguageMessageKey.valueOf(v));
    }

    /**
     * Language osztályban nem található változó értékének a lekérdezése.
     *
     * @param request kérés
     * @param response válaszobjektum
     * @param v változó neve
     * @return változó értéke
     */
    protected String insertCustomVariableValue(HttpServletRequest request, HttpServletResponse response, String v) {

        if (v.equals(HTML_PAGE_TITLE_VARIABLE_NAME)) {
            return HttpServer.APP_TITLE;
        }

        return "";
    }
}
