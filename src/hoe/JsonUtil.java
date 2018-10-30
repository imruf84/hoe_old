package hoe;

public class JsonUtil {

    public static String chatMessage(String userName, String msg) {
        return "{\"a\":\"cm\",\"d\":{\"user\":\"" + userName + "\",\"msg\":\"" + msg + "\"}}";
    }
}
