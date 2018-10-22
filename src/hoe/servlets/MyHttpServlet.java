package hoe.servlets;

import hoe.servers.GameServer;
import hoe.Language;
import hoe.LanguageMessageKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class MyHttpServlet extends HttpServlet {

    public static final String HTML_TAG_FROM = "#!";
    public static final String HTML_TAG_TO = "!#";
    public static final String HTML_ERROR_VARIABLE_NAME = "ERROR";
    public static final String HTML_PAGE_TITLE_VARIABLE_NAME = "PAGE_TITLE";

    protected abstract String getDefaultPagePath();

    private String concateVariableNameWithTags(String vName) {
        return MyHttpServlet.HTML_TAG_FROM + vName + MyHttpServlet.HTML_TAG_TO;
    }

    protected void appendPage(String page, HttpServletRequest request, HttpServletResponse response, String error) throws IOException {

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        InputStream in = getClass().getResourceAsStream(page);
        try (BufferedReader input = new BufferedReader(new InputStreamReader(in))) {
            String s;
            while (null != (s = input.readLine())) {

                StringBuilder result = new StringBuilder();

                // Finding JavaScript tags.
                Matcher matcherScripts = Pattern.compile("<script\\ssrc=\"(.*\\.js)\"><\\/script>", Pattern.CASE_INSENSITIVE).matcher(s);
                if (matcherScripts.find()) {

                    // Getting the file name.
                    String jsFile = matcherScripts.group(1);

                    // Reading the file.
                    InputStream jsin = getClass().getResourceAsStream("/hoe/html/" + jsFile);
                    try (BufferedReader jsinput = new BufferedReader(new InputStreamReader(jsin))) {
                        String jss;
                        result.append("<script>\n");
                        while (null != (jss = jsinput.readLine())) {
                            result.append(jss);
                            result.append('\n');
                        }
                        result.append("</script>\n");
                    }

                }

                // Finding css links.
                Matcher matcherStyles = Pattern.compile("<link\\srel=\"stylesheet\"\\shref=\"(.*\\.css)\">", Pattern.CASE_INSENSITIVE).matcher(s);
                if (matcherStyles.find()) {

                    // Getting the file name..
                    String jsFile = matcherStyles.group(1);

                    // Reading the file content.
                    InputStream cssin = getClass().getResourceAsStream("/hoe/html/" + jsFile);
                    try (BufferedReader jsinput = new BufferedReader(new InputStreamReader(cssin))) {
                        String csss;
                        result.append("<style>\n");
                        while (null != (csss = jsinput.readLine())) {
                            result.append(csss);
                            result.append('\n');
                        }
                        result.append("</style>\n");
                    }

                }

                // If there are file content we are working with them.
                if (!result.toString().isEmpty()) {
                    s = result.toString();
                }

                // Finding variable names.
                while (s.contains(MyHttpServlet.HTML_TAG_FROM) && s.contains(MyHttpServlet.HTML_TAG_TO)) {
                    String extracted = "";
                    try {
                        extracted = s.substring(MyHttpServlet.HTML_TAG_FROM.length() + s.indexOf(MyHttpServlet.HTML_TAG_FROM), s.indexOf(MyHttpServlet.HTML_TAG_TO));
                        s = s.replaceAll(concateVariableNameWithTags(extracted), insertVariableValue(request, response, extracted));
                    } catch (IllegalArgumentException e) {

                        // Show error message.
                        if (!error.isEmpty() && extracted.equals(HTML_ERROR_VARIABLE_NAME)) {
                            s = s.replaceAll(concateVariableNameWithTags(HTML_ERROR_VARIABLE_NAME), error);
                            continue;
                        }

                        // Setting other variables value.
                        s = s.replaceAll(concateVariableNameWithTags(extracted), insertCustomVariableValue(request, response, extracted));

                    } catch (StringIndexOutOfBoundsException e) {
                    }
                }

                response.getWriter().append(s + '\n');
            }
            in.close();

        }
    }

    protected void appendDefaultPage(HttpServletRequest request, HttpServletResponse response, String error) throws IOException {

        appendPage(getDefaultPagePath(), request, response, error);
    }

    protected String insertVariableValue(HttpServletRequest request, HttpServletResponse response, String v) {
        return Language.getText(LanguageMessageKey.valueOf(v));
    }

    protected String insertCustomVariableValue(HttpServletRequest request, HttpServletResponse response, String v) {

        if (v.equals(HTML_PAGE_TITLE_VARIABLE_NAME)) {
            return GameServer.APP_TITLE;
        }

        return "";
    }
}
