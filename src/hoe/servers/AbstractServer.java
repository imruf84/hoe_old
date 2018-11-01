package hoe.servers;

import hoe.Cryptography;
import hoe.HttpClient;
import hoe.Log;
import hoe.NothingLogger;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.server.Server;

public abstract class AbstractServer {

    private final String ip;
    private final int port;
    private final Server server;
    private final String serverType;
    private String redirectServerUrl = null;

    public AbstractServer(String type, String ip, int port) {
        turnOffLogging();

        this.serverType = type;
        this.ip = ip;
        this.port = port;

        server = new Server(getPort());
        //server = new Server(new InetSocketAddress(ip, getPort()));
    }

    public String getRedirectServerUrl() {
        return redirectServerUrl;
    }

    public void setRedirectServerUrl(String redirectServerUrl) {
        this.redirectServerUrl = redirectServerUrl;
    }

    private void turnOffLogging() {
        org.eclipse.jetty.util.log.Log.setLog(new NothingLogger());
    }

    public String getServerType() {
        return serverType;
    }

    public String getIp() {
        return ip;
    }

    public final int getPort() {
        return port;
    }

    public final Server getServer() {
        return server;
    }

    public void start() throws Exception {
        server.start();
        connectingToRedirectServer();
    }

    private void handleSigning(boolean subscribe) {
        if (getRedirectServerUrl() == null || getServerType() == null) {
            return;
        }

        String data = Cryptography.encryptObject(new SubscribeRequest(getServerType(), getIp(), getPort(), !subscribe));

        // Connecting to redirect server...
        HttpClient client = new HttpClient();
        String url = getRedirectServerUrl() + RedirectServer.SUBSCRIBE_SERVLET_PATH + data;
        if (subscribe) {
            Log.info("Trying to connect to the redirect server [" + url + "]...");
        } else {
            Log.info("Trying to disconnect from the redirect server [" + url + "]...");
        }
        while (!client.isOk()) {
            try {
                client.sendGet(url);
            } catch (IOException ex) {
                Log.warning(ex.getLocalizedMessage());
            } finally {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                    Log.error(ex);
                }
            }
        }

        if (subscribe) {
            Log.info("Conneted to redirect server [" + url + "]");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                disconnectingFromRedirectServer();
            }));
        } else {
            Log.info("Disonneted from redirect server [" + url + "]");
        }
    }

    protected void connectingToRedirectServer() {
        handleSigning(true);
    }

    protected void disconnectingFromRedirectServer() {
        handleSigning(false);
    }
}
