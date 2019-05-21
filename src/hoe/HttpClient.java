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
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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

        response = null;

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpResponse reps = httpclient.execute(httpGet)) {
            HttpEntity entity = reps.getEntity();

            if (storeResponse && entity != null) {
                response = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
            }
            
            responseCode = reps.getStatusLine().getStatusCode();
            
            httpGet.releaseConnection();
            
            return responseCode;
        }
        
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
