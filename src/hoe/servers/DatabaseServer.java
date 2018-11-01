package hoe.servers;

import hoe.Log;
import java.sql.SQLException;
import org.h2.tools.Server;

public class DatabaseServer {

    private static Server webServer;
    private static Server tcpServer;

    public static void startServers(String webPort, String tcpPort) {
        try {
            Log.info("Starting databse Web Console server...");
            webServer = Server.createWebServer("-webPort", webPort, "-webAllowOthers");
            webServer.start();
            Log.info(webServer.getStatus());

            Log.info("Starting databse TCP server...");
            tcpServer = Server.createTcpServer("-tcpPort", tcpPort, "-tcpAllowOthers");
            tcpServer.start();
            Log.info(tcpServer.getStatus());
        } catch (SQLException ex) {
            Log.error(ex);
        }
    }
}
