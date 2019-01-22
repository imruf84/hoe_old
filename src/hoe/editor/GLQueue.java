package hoe.editor;

import com.jogamp.opengl.GL2;
import java.util.ArrayList;

/**
 * example: GLQueue.getInstance().add((GL2 gl) -> {});
 */
public class GLQueue {

    private static GLQueue instance = null;
    private final ArrayList queue = new ArrayList(16);

    protected GLQueue() {
    }

    public static synchronized GLQueue getInstance() {
        if (instance == null) {
            instance = new GLQueue();
        }
        return instance;
    }

    public void add(GLAction action) {
        synchronized (queue) {
            queue.add(action);
        }
    }

    public void execute(GL2 gl) {
        // make a copy of the queue to allow thread safe iteration
        ArrayList temp = null;
        synchronized (queue) {
            // Only make a copy, if the queue has entries
            if (!queue.isEmpty()) {
                temp = new ArrayList(queue);
                queue.clear();
            }
        }

        // iterate outside of the synchronization to avoid blocking the queue
        if (temp != null) {
            for (int i = 0; i < temp.size(); i++) {
                ((GLAction) temp.get(i)).execute(gl);
            }
        }
    }
}
