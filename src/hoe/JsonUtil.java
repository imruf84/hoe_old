package hoe;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonUtil {

    public static String chatMessage(String userName, String msg) {

        JsonObject json = new JsonObject();
        
        json.add("a", new JsonPrimitive("cm"));
        
        JsonObject data = new JsonObject();
        json.add("d", data);
        data.add("user", new JsonPrimitive(userName));
        data.add("msg", new JsonPrimitive(msg));
        
        return json.toString();
    }
}
