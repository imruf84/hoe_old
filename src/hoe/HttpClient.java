package hoe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.eclipse.jetty.http.HttpStatus;

public class HttpClient {

    private final String USER_AGENT = "Mozilla/5.0";
    private String response = null;
    private int responseCode = 0;

    public int sendGetWithResponse(String url) throws MalformedURLException, IOException {
        return sendGet(url, true);
    }

    public int sendGet(String url) throws MalformedURLException, IOException {
        return sendGet(url, false);
    }

    public int sendGet(String url, boolean storeResponse) throws MalformedURLException, IOException {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        response = null;
        StringBuilder r = new StringBuilder();
        int rc = con.getResponseCode();

        if (storeResponse) {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    r.append(inputLine);
                }
                response = r.toString();
            }
        }

        responseCode = rc;

        return rc;
    }

    public boolean isOk() {
        return getResponseCode() == HttpStatus.OK_200;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponse() {
        return response;
    }

    public static void sampleGetWithBasicAuth() {
        try {
            String data = "test data";

            URL url = new URL("http://127.0.0.1:8090/calc");
            String encoding = Base64.getEncoder().encodeToString(("admin:admin").getBytes(StandardCharsets.UTF_8));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Basic " + encoding);
            connection.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
            InputStream content = (InputStream) connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            Log.error(e);
        }

    }

}
