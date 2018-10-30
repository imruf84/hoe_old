package hoe.servers;

import hoe.Log;
import java.sql.SQLException;
import org.h2.tools.Server;

public class DatabaseServer {

    private static Server webServer;
    private static Server tcpServer;

    public static void startServers() {
        try {
            Log.info("Starting databse Web Console server...");
            webServer = Server.createWebServer("-webPort", "8082", "-webAllowOthers");
            webServer.start();
            Log.info(webServer.getStatus());

            Log.info("Starting databse TCP server...");
            tcpServer = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers");
            tcpServer.start();
            Log.info(tcpServer.getStatus());
        } catch (SQLException ex) {
            Log.error(ex);
        }
    }
}
