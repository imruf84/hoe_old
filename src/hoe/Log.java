package hoe;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Üzenetek megjelenítésének osztálya.
 *
 * @author imruf84
 */
public class Log {

    /**
     * Igaz esetén a debug üzenetek megjelennek.
     */
    public static boolean showDebugMessages = false;

    /**
     * Aktuális dátum és idő lekérdezése.
     * 
     * @return aktuális dátum és idő
     */
    private static String getCurrentDateAndTime() {
        return new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss").format(new Date());
    }

    /**
     * Debug üzenet megjelenítése.
     *
     * @param msg üzenet
     */
    public static void debug(String msg) {
        if (showDebugMessages) {
            System.out.println("DEBUG(" + getCurrentDateAndTime() + "): " + msg);
        }
    }

    /**
     * Információ megjelenítése.
     *
     * @param msg üzenet
     */
    public static void info(String msg) {
        System.out.println("INFO(" + getCurrentDateAndTime() + "): " + msg);
    }

    /**
     * Figyelmeztetés megjelenítése.
     *
     * @param msg üzenet
     */
    public static void warning(String msg) {
        System.out.println("WARNING(" + getCurrentDateAndTime() + "): " + msg);
    }

    /**
     * Hiba megjelenítése.
     *
     * @param msg üzenet
     * @param e kivétel
     */
    public static void error(String msg, Exception e) {
        System.err.println("ERROR(" + getCurrentDateAndTime() + "): " + msg);
        if (null != e) {
            e.printStackTrace(System.err);
        }
    }
}
