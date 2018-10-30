package hoe.servers;

import java.io.Serializable;

public class SubscribeRequest implements Serializable {

    private final int port;

    public SubscribeRequest(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
    
}
