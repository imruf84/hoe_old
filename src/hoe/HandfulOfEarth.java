package hoe;

import hoe.servers.GameServer;
import hoe.editor.Editor;
import hoe.servers.ContentServer;
import hoe.servers.RedirectServer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;
import nlopt.CobylaExitStatus;
import nlopt.CobylaTest;

/**
 * BUGFIX: Safari (on iOS) has some issues to show redirected images, so it
 * would be good to turn off "Prevent cross-site tracking" feature
 * https://support.securly.com/hc/en-us/articles/360000881087-How-to-resolve-the-too-many-redirects-error-on-Safari-
 */
public class HandfulOfEarth {

    public static String[] printArray(double[] d) {
        DecimalFormat df = new DecimalFormat("#.########");
        String s[] = new String[d.length];
        for (int i=0; i<d.length;i++) {
            s[i]=df.format(d[i]);
        }
        
        return s;
    }
    
    // http://www.ai7.uni-bayreuth.de/test_problem_coll.pdf
    public static void main__(String[] args) throws Exception {
        CobylaTest t = new CobylaTest();
        t.test01FindMinimum();
        //double[] r = t.test09FindMinimum();
        //System.out.println(Arrays.toString(printArray(r)));
    }
    
    public static void main_(String[] args) throws Exception {
        try {
            String data = "test data";
            
            //URL url = new URL("http://192.168.0.25:8090/calc");
            URL url = new URL("http://127.0.0.1:8090/calc");
            String encoding = Base64.getEncoder().encodeToString(("admin:admin").getBytes(StandardCharsets.UTF_8));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Basic " + encoding);
            connection.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
            InputStream content = (InputStream) connection.getInputStream();
            BufferedReader in= new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            Log.error(e);
        }
    }

    public static void main___(String[] args) throws Exception {

        Properties prop = new Properties();
        prop.load(new BufferedReader(new FileReader(args[0])));

        //for (int i = 0; i < 10; i++) System.out.println(UUID.randomUUID());
        String propKey;

        propKey = "debug";
        Log.showDebugMessages = (prop.containsKey(propKey) && prop.getProperty(propKey).toLowerCase().equals("true"));

        Language.init();
        Cryptography.setKey(prop.getProperty("secretkey"));

        String ip = prop.getProperty("ip");

        propKey = "startdbserver";
        if (prop.containsKey(propKey) && prop.getProperty(propKey).toLowerCase().equals("true")) {
            String webPort = prop.getProperty("dbserverwebport");
            String tcpPort = prop.getProperty("dbservertcpport");
            hoe.servers.DatabaseServer.startServers(webPort, tcpPort);
        }

        propKey = "startredirectserver";
        if (prop.containsKey(propKey) && prop.getProperty(propKey).toLowerCase().equals("true")) {
            int port = Integer.parseInt(prop.getProperty("redirectserverport"));
            RedirectServer server = new RedirectServer(ip, port);
            server.start();
        }

        propKey = "startcontentserver";
        if (prop.containsKey(propKey) && prop.getProperty(propKey).toLowerCase().equals("true")) {
            int port = Integer.parseInt(prop.getProperty("contentserverport"));
            ContentServer server = new ContentServer(ip, port);
            server.setRedirectServerUrl(prop.getProperty("redirectserverurl"));
            server.start();
        }

        propKey = "startgameserver";
        if (prop.containsKey(propKey) && prop.getProperty(propKey).toLowerCase().equals("true")) {
            try {
                String userDbIp = prop.getProperty("userdbip");
                UserManager.setDataBaseIp(userDbIp);

                String sceneDbIp = prop.getProperty("scenedbip");
                SceneManager.setDataBaseIp(sceneDbIp);

                int port = Integer.parseInt(prop.getProperty("gameserverport"));
                GameServer server = new GameServer(ip, port);
                server.setRedirectServerUrl(prop.getProperty("redirectserverurl"));
                server.start();

            } catch (Exception ex) {
                Log.error(Language.getText(LanguageMessageKey.CREATING_SERVER_FAILED), ex);
            }
        }

        boolean runEditor = false;
        if (runEditor) {
            Editor editor = new Editor();
            editor.show();
        }

    }

}
