package hoe.editor;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import hoe.renderer.Camera;
import hoe.renderer.shaders.ConstantColorShader;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import org.joml.Vector3d;

public class ShaderEditor implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    private final GLU glu = new GLU();
    private final GLUT glut = new GLUT();

    double panUnits[] = {1, 1};

    private final Camera camera = new Camera();

    private final double dRotate[] = new double[]{0, 0};
    private final double dTranslate[] = new double[]{0, 0};
    private double dZoom = 0;

    private final LinkedList<String> logMessages = new LinkedList<>();

    private JFrame frame = null;

    ConstantColorShader shader = null;
    private JTextArea logsTextArea;

    public ShaderEditor() {
        getCamera().setZoomLimits(1, 10);
        getCamera().setZoom(3);
    }

    @Override
    public void display(GLAutoDrawable drawable) {

        final GL2 gl = drawable.getGL().getGL2();

        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glClearColor(0f, 0f, 0f, 0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        // Transform camera.
        gl.glMatrixMode(GL2.GL_PROJECTION);

        getCamera().setZoom(getCamera().getZoom() + dZoom);
        getCamera().dTranslate(dTranslate[0], dTranslate[1]);
        getCamera().setRotateX(Math.min(Math.max(-90 - 45, getCamera().getRotateX() + dRotate[0]), 90 - 45));
        getCamera().dRotateZ(dRotate[1]);
        getCamera().recalculate();
        gl.glLoadMatrixd(getCamera().getProjectionArray(), 0);

        // Render scene.
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        // Execute OGL specific events.
        GLQueue.getInstance().execute(gl);

        // Rendering scene.
        shader.apply(1, 0, 0, 1);
        glut.glutSolidSphere(10, 64, 64);

        // Rendering axises.
        gl.glUseProgram(0);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glBegin(GL2.GL_LINES);
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(10, 0, 0);
        gl.glColor3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(0, 10, 0);
        gl.glColor3f(0.0f, 0.0f, 1.0f);
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(0, 0, 10);
        gl.glEnd();
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_LIGHTING);

        // Rendering log messages.
        renderLogMessages(gl);

        // End rendering.
        gl.glFlush();

    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
    }

    private void appendLogMessageToScreen(String s) {
        SwingUtilities.invokeLater(() -> {
            logMessages.add(s);
            if (logMessages.size() > 10) {
                logMessages.pop();
            }
        });
    }

    private void appendLogMessage(String s) {
        logsTextArea.append(s + "\n");
    }

    public final Camera getCamera() {
        return camera;
    }

    private void renderLogMessages(GL2 gl) {
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        glu.gluOrtho2D(0, getCamera().getViewportWidth(), 0, getCamera().getViewportHeight());
        gl.glScalef(0, -1, 0);
        gl.glTranslated(0, -getCamera().getViewportHeight(), 0);

        gl.glUseProgram(0);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glColor3f(1.0f, 1.0f, 0.0f);

        float y = 12f;

        for (String s : logMessages) {
            gl.glRasterPos2f(10f, y);
            glut.glutBitmapString(GLUT.BITMAP_8_BY_13, s);
            y += 14f;
        }

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPopMatrix();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

        final GL2 gl = drawable.getGL().getGL2();
        height = Math.max(height, 1);

        gl.glViewport(0, 0, width, height);
        getCamera().setViewportSize(width, height);

        shader = new ConstantColorShader(gl);
        appendLogMessageToScreen("Canvas resized to " + width + "x" + height);
    }

    public void show() {

        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        capabilities.setSampleBuffers(true);
        capabilities.setNumSamples(8);

        final GLCanvas glcanvas = new GLCanvas(capabilities);
        ShaderEditor render = new ShaderEditor();
        glcanvas.addGLEventListener(render);
        glcanvas.addMouseListener(render);
        glcanvas.addMouseMotionListener(render);
        glcanvas.addMouseWheelListener(render);
        glcanvas.addKeyListener(render);
        glcanvas.setMinimumSize(new Dimension(100, 100));
        glcanvas.setPreferredSize(new Dimension(800, 800));
        //glcanvas.setSize(800, 800);

        frame = new JFrame("Shader Editor");
        JSplitPane mainSplitPlane = new JSplitPane();
        frame.getContentPane().add(mainSplitPlane);
        JSplitPane leftSplitPlane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPlane.setLeftComponent(leftSplitPlane);
        leftSplitPlane.setLeftComponent(glcanvas);
        logsTextArea = new JTextArea();
        logsTextArea.setEditable(false);
        logsTextArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        JScrollPane logsScrollPane = new JScrollPane(logsTextArea);
        logsScrollPane.setBorder(new TitledBorder("Logs"));
        leftSplitPlane.setRightComponent(logsScrollPane);
        leftSplitPlane.setDividerLocation(.5d);

        JSplitPane rightSplitPlane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPlane.setRightComponent(rightSplitPlane);

        mainSplitPlane.setDividerLocation(.5d);

        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);
        final FPSAnimator animator = new FPSAnimator(glcanvas, 30, true);

        animator.start();

        glcanvas.requestFocus();
        appendLogMessage("Test log message");
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {

        // For camera pannign.
        prev = new int[]{e.getX(), e.getY()};
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // For camera pannign.
        prev = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    int prev[] = {0, 0};

    @Override
    public void mouseDragged(MouseEvent e) {

        if (prev == null) {
            return;
        }

        int x = e.getX();
        int y = e.getY();

        int px = prev[0];
        int py = prev[1];

        prev[0] = x;
        prev[1] = y;

        int dx = x - px;
        int dy = y - py;

        // Pan camera.
        if ((SwingUtilities.isMiddleMouseButton(e) && !e.isShiftDown()) || (e.isShiftDown() && SwingUtilities.isRightMouseButton(e))) {

            Vector3d p0 = getCamera().getProjectionMatrix().unproject(0, 0, 0, getCamera().getViewport(), new Vector3d());
            Vector3d p1 = getCamera().getProjectionMatrix().unproject(1, 0, 0, getCamera().getViewport(), new Vector3d());
            Vector3d p2 = getCamera().getProjectionMatrix().unproject(0, 1 / Math.sin(Math.PI / 4d), 0, getCamera().getViewport(), new Vector3d());

            getCamera().dTranslateX(dx * new Vector3d(p0).sub(p1).length());
            getCamera().dTranslateY(-dy * new Vector3d(p0).sub(p2).length());

            return;
        }

        // Rotate camera.
        if (SwingUtilities.isRightMouseButton(e) || (e.isShiftDown() && SwingUtilities.isMiddleMouseButton(e))) {

            getCamera().setRotateZ(getCamera().getRotateZ() + dx * .3d);
            getCamera().setRotateX(getCamera().getRotateX() + dy * .3d);

        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        getCamera().setZoom(getCamera().getZoom() + e.getPreciseWheelRotation());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
            case ' ':
                break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        double z = .1;
        double r = 2;
        double t = 2;
        boolean isRotate = e.isShiftDown();
        boolean isZoom = e.isControlDown();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (isZoom) {
                    dZoom = -z;
                } else {
                    if (isRotate) {
                        dRotate[0] = r;
                    } else {
                        dTranslate[1] = -t;
                    }
                }
                break;
            case KeyEvent.VK_DOWN:
                if (isZoom) {
                    dZoom = z;
                } else {
                    if (isRotate) {
                        dRotate[0] = -r;
                    } else {
                        dTranslate[1] = t;
                    }
                }
                break;
            case KeyEvent.VK_LEFT:
                if (isRotate) {
                    dRotate[1] = -r;
                } else {
                    dTranslate[0] = t;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (isRotate) {
                    dRotate[1] = r;
                } else {
                    dTranslate[0] = -t;
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        boolean isRotate = e.isShiftDown();
        boolean isZoom = e.isControlDown();

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
                if (isZoom) {
                    dZoom = 0;
                } else {
                    if (isRotate) {
                        dRotate[0] = 0;
                    } else {
                        dTranslate[1] = 0;
                    }
                }
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                if (e.isShiftDown()) {
                    dRotate[1] = 0;
                } else {
                    dTranslate[0] = 0;
                }
                break;
        }
    }
}
