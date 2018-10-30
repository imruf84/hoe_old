package hoe.servers;

import hoe.Cryptography;
import hoe.HttpClient;
import hoe.Log;
import hoe.NothingLogger;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.server.Server;

public abstract class AbstractServer {

    private final int port;
    private final Server server;
    private String redirectServerUrl = null;

    public AbstractServer(int port) {
        turnOffLogging();

        this.port = port;

        server = new Server(getPort());
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

    protected void connectingToRedirectServer() {

        if (getRedirectServerUrl() == null) {
            return;
        }

        String data = Cryptography.encryptObject(new SubscribeRequest(getPort()));

        // Connecting to redirect server...
        HttpClient client = new HttpClient();
        String url = getRedirectServerUrl() + RedirectServer.SUBSCRIBE_SERVLET_PATH + data;
        Log.info("Trying to connect to the redirect server ["+url+"]...");
        while (!client.isOk()) {
            try {
                client.sendGet(url);
                TimeUnit.SECONDS.sleep(1);
            } catch (IOException | InterruptedException ex) {
                Log.debug(ex.getLocalizedMessage());
            }
        }
        Log.info("Conneted to redirect server ["+url+"]");
    }
}
