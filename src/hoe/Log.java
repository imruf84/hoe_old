package hoe;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Log {

    public static boolean showDebugMessages = false;

    private static String getCurrentDateAndTime() {
        return new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss").format(new Date());
    }

    public static String formatInterval(final long l) {
        final long hr = TimeUnit.MILLISECONDS.toHours(l);
        final long min = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
        return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
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

    public static String[] printArray(double[] d) {
        DecimalFormat df = new DecimalFormat("#.########");
        String s[] = new String[d.length];
        for (int i = 0; i < d.length; i++) {
            s[i] = df.format(d[i]);
        }

        System.out.println(Arrays.toString(s));
        
        return s;
    }
}
