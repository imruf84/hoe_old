package hoe;

import hoe.servers.GameServer;
import hoe.editor.Editor;
import hoe.servers.ContentServer;
import hoe.servers.RedirectServer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import nlopt.ObjectsPacker;
import physics.Vector3D;
import prototype.Player;
import prototype.TimeElapseMeter;

/**
 * BUGFIX: Safari (on iOS) has some issues to show redirected images, so it
 * would be good to turn off "Prevent cross-site tracking" feature
 * https://support.securly.com/hc/en-us/articles/360000881087-How-to-resolve-the-too-many-redirects-error-on-Safari-
 */
public class HandfulOfEarth {

    public static String[] printArray(double[] d) {
        DecimalFormat df = new DecimalFormat("#.########");
        String s[] = new String[d.length];
        for (int i = 0; i < d.length; i++) {
            s[i] = df.format(d[i]);
        }

        return s;
    }

    // http://www.ai7.uni-bayreuth.de/test_problem_coll.pdf
    private static int rnd(int a, int b) {
        if (a == b) {
            return a;
        }
        return ThreadLocalRandom.current().nextInt(Math.min(a, b), Math.max(a, b));
    }

    public static int counter(int i, int n) {
        if (i==0) return 0;
        
        int r = 0;
        
        for (int j = 0;j<i;j++){
            r+=n-j-1;
        }
        
        return r;
    }
    
    public static void main___(String[] args) {
        int n = 5;
        int c = 0;
        for (int i = 0; i < n - 1; i++) {
            //System.out.println(i + " " + n + " " + c);
            System.out.println(i + " " + c+" "+counter(i, n));
            for (int j = i + 1; j < n; j++) {
                //System.out.println(i + " " + j + " " + c);
                c++;
            }
        }
    }

    public static void main____(String[] args) {

        Log.showDebugMessages = true;

        ArrayList<Player> players = new ArrayList<>();

        int rangePlayer[] = {-300, 300, 90, 150};
        int rangeNavPoint[][] = {
            //{-200, 200, 0, 40},
            //{-130, 130, -80, -30},
            {-290, 290, -200, -150},};

        int SHAPE_SIZE = 10;
        int np = 50;
        int nn[] = {1, rangeNavPoint.length + 1};
        int maxStep[] = {2, 4};
        double playerScale = 1.d;
        for (int i = 0; i < np; i++) {
            Player player = new Player("P" + i, new Vector3D(rnd(rangePlayer[0], rangePlayer[1]), rnd(rangePlayer[2], rangePlayer[3]), 0), rnd((int) SHAPE_SIZE / 2, (int) SHAPE_SIZE) * playerScale, rnd(maxStep[0], maxStep[1]));
            int n = rnd(nn[0], nn[1]);
            for (int j = 0; j < n; j++) {
                int k = j + 1 == n ? rangeNavPoint.length - 1 : j;
                player.addNavigationPoint(new Vector3D(ThreadLocalRandom.current().nextInt(rangeNavPoint[k][0], rangeNavPoint[k][1]), ThreadLocalRandom.current().nextInt(rangeNavPoint[k][2], rangeNavPoint[k][3]), 0));
            }
            players.add(player);
        }

        /*ObjectsPacker.packPlayerClusters(ObjectsPacker.clusterize(players), true, () -> {
            Log.info("finished");
        });*/
        
        ArrayList<Player> players2 = new ArrayList<>();
        players2.addAll(players);
        
        TimeElapseMeter tem = new TimeElapseMeter(true);
        ObjectsPacker.packPlayers(players, true);
        System.out.println(tem.stopAndGet());
        
        tem = new TimeElapseMeter(true);
        ObjectsPacker.packPlayers2(players2, true);
        System.out.println(tem.stopAndGet());
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
            BufferedReader in = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            Log.error(e);
        }
    }

    public static void main(String[] args) throws Exception {

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

        propKey = "runeditor";
        if (prop.containsKey(propKey) && prop.getProperty(propKey).toLowerCase().equals("true")) {
            Editor editor = new Editor();
            editor.show();
        }

    }

}
