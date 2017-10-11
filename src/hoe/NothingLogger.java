package hoe;

import org.eclipse.jetty.util.log.Logger;

/**
 * Semmit loggoló.
 *
 * @author imruf84
 */
public class NothingLogger implements Logger {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void warn(String string, Object... os) {
    }

    @Override
    public void warn(Throwable thrwbl) {
    }

    @Override
    public void warn(String string, Throwable thrwbl) {
    }

    @Override
    public void info(String string, Object... os) {
    }

    @Override
    public void info(Throwable thrwbl) {
    }

    @Override
    public void info(String string, Throwable thrwbl) {
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void setDebugEnabled(boolean bln) {
    }

    @Override
    public void debug(String string, Object... os) {
    }

    @Override
    public void debug(String string, long l) {
    }

    @Override
    public void debug(Throwable thrwbl) {
    }

    @Override
    public void debug(String string, Throwable thrwbl) {
    }

    @Override
    public Logger getLogger(String string) {
        return this;
    }

    @Override
    public void ignore(Throwable thrwbl) {
    }

}
