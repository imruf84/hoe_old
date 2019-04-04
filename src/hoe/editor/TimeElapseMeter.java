package hoe.editor;

import hoe.Log;
import java.util.Calendar;

public class TimeElapseMeter {

    private long time;

    public TimeElapseMeter() {
        this(true);
    }
    
    public TimeElapseMeter(boolean autoStart) {
        if (autoStart) {
            start();
        }
    }

    public final void start() {
        time = Calendar.getInstance().getTimeInMillis();
    }
    
    public final void stop() {
        time = getTime();
    }

    public String stopAndGetFormat() {
        return Log.formatInterval(stopAndGet());
    }

    public long getTime() {
        return Calendar.getInstance().getTimeInMillis() - time;
    }
    
    public long stopAndGet() {
        stop();
        return time;
    }
}
