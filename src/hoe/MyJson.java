package hoe;

/**
 * Objektumok átalakítása Json-ná.
 * 
 * @author imruf84
 */
public class MyJson {
    
    /**
     * Chat üzenet átalakítása Json objektummá.
     * 
     * @param userName felhasználó neve
     * @param msg üzenet szövege
     * @return chat üzenet Json-ként
     */
    public static String chatMessage(String userName, String msg) {
        return "{\"a\":\"cm\",\"d\":{\"user\":\"" + userName + "\",\"msg\":\"" + msg + "\"}}";
    }
}
