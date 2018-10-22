package hoe.servers;

import hoe.NothingLogger;
import org.eclipse.jetty.server.Server;

public abstract class AbstractServer {

    private final int port;
    private final Server server;

    public AbstractServer(int port) {
        turnOffLogging();

        this.port = port;

        server = new Server(getPort());
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
    }
}
