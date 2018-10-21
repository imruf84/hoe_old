package hoe;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class User {

    public static String USER_NAME_SESSION_KEY = "user";
    private final AtomicReference<LinkedList<AsyncContext>> context = new AtomicReference<>(new LinkedList<>());
    private final String sessionId;
    private final String userName;
    private final String password;
    private final LinkedList<String> messages = new LinkedList<>();

    public User(final String sessionId, final String userName, final String password) {
        this.sessionId = sessionId;
        this.userName = userName.replaceAll("\"", "\\\\\"").replaceAll("'", "\\\\'");
        this.password = password;
    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean isValid(final HttpServletRequest request) {

        HttpSession session = request.getSession();
        String lUserName = (String) session.getAttribute(USER_NAME_SESSION_KEY);

        if (null == lUserName) {
            return false;
        }

        return lUserName.equals(getUserName());
    }

    public boolean isActive(final HttpServletRequest request) {
        return isValid(request) && hasActiveContext();
    }

    public boolean hasActiveContext() {
        return (context.get().size() > 0);
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void sendMessage(final String msg) throws IOException {

        // Storing message.
        synchronized (messages) {
            messages.add(msg);
        }

        // Sending messages.
        flushMessages();
    }

    public void storeAsyncContext(final HttpServletRequest r) throws IOException {

        // If there are no soring thing we send the messages.
        if (null == r) {
            flushMessages();
            return;
        }

        // Registering events.
        AsyncContext c = r.startAsync();
        c.setTimeout(20000);
        c.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent ae) throws IOException {
                Log.debug("onComplete[" + getUserName() + "]:" + ae.toString());
                removeContext(c);
            }

            @Override
            public void onTimeout(AsyncEvent ae) throws IOException {
                Log.debug("onTimeout[" + getUserName() + "]:" + ae.toString());
                c.complete();
                removeContext(c);
            }

            @Override
            public void onError(AsyncEvent ae) throws IOException {
                Log.debug("onError[" + getUserName() + "]:" + ae.toString());
                removeContext(c);
            }

            @Override
            public void onStartAsync(AsyncEvent ae) throws IOException {
                Log.debug("onStartAsync[" + getUserName() + "]:" + ae.toString());
            }
        });

        synchronized (context) {

            // Removing previous channels.
            if (0 < context.get().size()) {
                flushContexts();
            }

            // Storing new channel.
            context.get().add(c);
            Log.debug("storeContext[" + getUserName() + ":" + context.get().size() + "]:" + c.toString());

            // Open async signal.
            c.start(() -> {
            });
        }

        // Sending stored messages.
        flushMessages();
    }

    private void removeContext(AsyncContext c) {
        synchronized (context) {
            context.get().remove(c);
            Log.debug("removeContext[" + getUserName() + ":" + context.get().size() + "]:" + c.toString());
        }
    }

    public void flushContexts() {

        // Closing response channels.
        synchronized (context) {
            for (AsyncContext ac : context.get()) {
                ac.complete();
            }
        }
    }

    private void flushMessages() throws IOException {

        synchronized (messages) {

            // If there are no sending message we exit.
            if (messages.isEmpty()) {
                return;
            }

            synchronized (context) {

                // If there are no opened channel we exit.
                if (0 == context.get().size()) {
                    return;
                }

                // Joining messages.
                // TODO: send only small amount of message at the same time
                String s = Arrays.toString(messages.toArray());
                messages.clear();

                // Sending.
                for (AsyncContext ac : context.get()) {
                    ServletResponse sr = ac.getResponse();
                    sr.setContentType("text/json");
                    sr.setCharacterEncoding("UTF-8");
                    sr.getWriter().append(s);

                    // Closing response channel.
                    ac.complete();
                }

            }

        }

    }

    public int getAsyncChannelsCount() {
        return context.get().size();
    }

    @Override
    public String toString() {
        return "User{" + "sessionId=" + sessionId + ", userName=" + userName + '}';
    }

}
