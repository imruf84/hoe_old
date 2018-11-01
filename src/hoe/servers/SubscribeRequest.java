package hoe.servers;

import java.io.Serializable;

public class SubscribeRequest implements Serializable {

    public static final String GAME_SERVER_TYPE = "GAME_SERVER";
    public static final String CONTENT_SERVER_TYPE = "CONTENT_SERVER";
    public static final String RENDER_SERVER_TYPE = "RENDER_SERVER";
    
    private final String ip;
    private final int port;
    private final String type;
    private final boolean unsubscribe;

    public SubscribeRequest(String type, String ip, int port, boolean unsubscribe) {
        this.type = type;
        this.ip = ip;
        this.port = port;
        this.unsubscribe = unsubscribe;
    }

    public String getType() {
        return type;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public boolean isUnsubscribe() {
        return unsubscribe;
    }
    
}
