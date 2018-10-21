package hoe;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    public static boolean showDebugMessages = false;

    private static String getCurrentDateAndTime() {
        return new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss").format(new Date());
    }

    public static void print(String msg) {
        System.out.println(msg);
    }

    public static void debug(String msg) {
        if (showDebugMessages) {
            System.out.println("DEBUG(" + getCurrentDateAndTime() + "): " + msg);
        }
    }

    public static void info(String msg) {
        System.out.println("INFO(" + getCurrentDateAndTime() + "): " + msg);
    }

    public static void warning(String msg) {
        System.out.println("WARNING(" + getCurrentDateAndTime() + "): " + msg);
    }

    public static void error(String msg, Exception e) {
        System.err.println("ERROR(" + getCurrentDateAndTime() + "): " + msg);
        if (null != e) {
            e.printStackTrace(System.err);
        }
    }

    public static void error(Exception e) {
        error("", e);
    }
}
