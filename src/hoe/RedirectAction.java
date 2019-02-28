package hoe;

import java.io.Serializable;
import java.time.Instant;

public class RedirectAction implements Serializable {

    private final String path;
    private final long timestamp;
    private final String user;
    private String data = null;

    public static <T extends Serializable> String createAndEncrypt(String path, String userName, T data) {
        RedirectAction ra = new RedirectAction(path, Instant.now().getEpochSecond(), userName);
        ra.setData(Cryptography.encryptObject(data));
        return Cryptography.encryptObject(ra);
    }
    
    public RedirectAction(String path, long timestamp, String user) {
        this.path = path;
        this.timestamp = timestamp;
        this.user = user;
    }

    public String getPath() {
        return path;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUser() {
        return user;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RedirectAction{" + "path=" + path + ", timestamp=" + timestamp + ", user=" + user + '}';
    }

}
