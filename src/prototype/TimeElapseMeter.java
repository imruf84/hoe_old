package prototype;

import hoe.Log;
import java.util.Calendar;

public class TimeElapseMeter {

    private long time;

    public TimeElapseMeter(boolean autoStart) {
        if (autoStart) {
            start();
        }
    }
    
    public void start() {
        time = Calendar.getInstance().getTimeInMillis();
    }
    
    public String stopAndGet() {
        return Log.formatInterval(Calendar.getInstance().getTimeInMillis() - time);
    }
}