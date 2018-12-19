package hoe.editor;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.SwingUtilities;
import org.joml.Matrix4d;
import org.joml.Vector3d;

public class Editor implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    private final GLU glu = new GLU();
    private final GLUT glut = new GLUT();

    int viewport[] = new int[4];
    double projection[] = new double[16];
    double panUnits[] = {1, 1};

    private final double rotate[] = new double[]{0, 0};
    private final double dRotate[] = new double[]{0, 0};
    private final double translate[] = new double[]{0, 0};
    private final double dTranslate[] = new double[]{0, 0};
    private double zoom = 5;
    private double dZoom = 0;
    private int prog;
    private float colR = 0;
    private float dColR = 0.004f;

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
        gl.glPushMatrix();

        glu.gluLookAt(0, 1, 1, 0, 0, 0, 0, 0, 1);
        gl.glRotated(180, 0, 0, 1);
        zoom += dZoom;
        zoom = Math.min(Math.max(1, zoom), 10);
        gl.glScaled(1 / zoom, 1 / zoom, 1 / zoom);
        translate[0] += dTranslate[0];
        translate[1] += dTranslate[1];
        gl.glTranslated(translate[0], translate[1], 0);
        rotate[0] += dRotate[0];
        rotate[1] += dRotate[1];
        //rotate[0] = Math.min(Math.max(-30, rotate[0]), 30);
        rotate[0] = Math.min(Math.max(-90 - 45, rotate[0]), 90 - 45);
        gl.glRotated(rotate[0], 1, 0, 0);
        gl.glRotated(rotate[1], 0, 0, 1);

        getMatrices(gl);

        // Render scene.
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glUseProgram(prog);
        int col = gl.glGetUniformLocation(prog, "col");
        colR += dColR;
        if (colR > 1f || colR < 0f) {
            dColR = -dColR;
        }
        gl.glUniform4f(col, colR, 0, 1, 1);

        gl.glBegin(GL2.GL_TRIANGLES);

        gl.glTexCoord2f(1, 0);
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);

        gl.glTexCoord2f(0, 1);
        gl.glColor3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3f(1.0f, 0.0f, 0.0f);

        gl.glTexCoord2f(0, 0);
        gl.glColor3f(0.0f, 0.0f, 1.0f);
        gl.glVertex3f(0.0f, 1.0f, 0.0f);

        gl.glEnd();

        // origo
        gl.glUniform4f(col, 1, 1, 1, 1);
        gl.glPushMatrix();
        gl.glTranslated(0, 0, 0);
        glut.glutSolidSphere(.02, 10, 10);
        gl.glPopMatrix();

        // x axis
        gl.glUniform4f(col, 1, 0, 0, 1);
        gl.glPushMatrix();
        gl.glTranslated(1, 0, 0);
        glut.glutSolidSphere(.02, 10, 10);
        gl.glPopMatrix();

        // y axis
        gl.glUniform4f(col, 0, 1, 0, 1);
        gl.glPushMatrix();
        gl.glTranslated(0, 1, 0);
        glut.glutSolidSphere(.02, 10, 10);
        gl.glPopMatrix();

        // z axis
        gl.glUniform4f(col, 0, 0, 1, 1);
        gl.glPushMatrix();
        gl.glTranslated(0, 0, 1);
        glut.glutSolidSphere(.02, 10, 10);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glRotated(90, 1, 0, 0);
        gl.glTranslated(0, .5, -2);
        gl.glUniform4f(col, 1, 1, 1, 1);
        glut.glutWireTeapot(.5);
        gl.glPopMatrix();

        gl.glFlush();

        // End rendering.
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPopMatrix();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();

        String fc[] = new String[]{
            "uniform vec4 col;"
            + "void main()"
            + "{"
            + "    gl_FragColor = col;"
            + "}",};

        int fs2 = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fs2, 1, fc, null);
        gl.glCompileShader(fs2);
        prog = gl.glCreateProgram();
        gl.glAttachShader(prog, fs2);
        gl.glLinkProgram(prog);
        gl.glValidateProgram(prog);
    }

    private void getMatrices(GL2 gl) {
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection, 0);
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

        final GL2 gl = drawable.getGL().getGL2();
        height = Math.max(height, 1);

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        double lZoom = 1;
        float aspect = (float) width / (float) height;
        if (width > height) {
            gl.glOrtho(-1 / lZoom * aspect, 1 / lZoom * aspect, -1 / lZoom, 1 / lZoom, -1000, 1000);
        } else {
            aspect = 1 / aspect;
            gl.glOrtho(-1 / lZoom, 1 / lZoom, -1 / lZoom * aspect, 1 / lZoom * aspect, -1000, 1000);
        }

        getMatrices(gl);
    }

    public void show() {

        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        capabilities.setSampleBuffers(true);
        capabilities.setNumSamples(8);

        final GLCanvas glcanvas = new GLCanvas(capabilities);
        Editor render = new Editor();
        glcanvas.addGLEventListener(render);
        glcanvas.addMouseListener(render);
        glcanvas.addMouseMotionListener(render);
        glcanvas.addMouseWheelListener(render);
        glcanvas.addKeyListener(render);
        glcanvas.setSize(800, 800);

        final JFrame frame = new JFrame("OGL");
        frame.getContentPane().add(glcanvas);
        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        final FPSAnimator animator = new FPSAnimator(glcanvas, 60, true);

        animator.start();

        glcanvas.requestFocus();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //System.out.println("Mouse Pressed");

        prev = new int[]{e.getX(), e.getY()};

    }

    @Override
    public void mouseReleased(MouseEvent e) {
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

        if (SwingUtilities.isMiddleMouseButton(e) || (e.isShiftDown() && SwingUtilities.isRightMouseButton(e))) {

            Matrix4d m = new Matrix4d(projection[0], projection[1], projection[2], projection[3], projection[4], projection[5], projection[6], projection[7], projection[8], projection[9], projection[10], projection[11], projection[12], projection[13], projection[14], projection[15]);
            Vector3d p0 = m.unproject(0, 0, 0, viewport, new Vector3d());
            Vector3d p1 = m.unproject(1, 0, 0, viewport, new Vector3d());
            Vector3d p2 = m.unproject(0, 1 / Math.sin(Math.PI / 4d), 0, viewport, new Vector3d());

            translate[0] += dx * new Vector3d(p0).sub(p1).length();
            translate[1] -= dy * new Vector3d(p0).sub(p2).length();
            return;
        }
        
        if (SwingUtilities.isRightMouseButton(e)) {
            rotate[1] += dx * .3d;
            rotate[0] += dy * .3d;

        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        zoom -= e.getPreciseWheelRotation();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        double z = .04;
        double r = 2;
        double t = .04;
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
