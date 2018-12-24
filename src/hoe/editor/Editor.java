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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import nlopt.ObjectsPacker;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import physics.Vector3D;
import prototype.Player;
import prototype.TimeElapseMeter;
import prototype.VPlayer2;

public class Editor implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    private final GLU glu = new GLU();
    private final GLUT glut = new GLUT();

    int viewport[] = new int[4];
    double projection[] = new double[16];
    double panUnits[] = {1, 1};

    private final double rotate[] = new double[]{100, 0};//full top view
    //private final double rotate[] = new double[]{0, 0};
    private final double dRotate[] = new double[]{0, 0};
    private final double translate[] = new double[]{0, 0};
    private final double dTranslate[] = new double[]{0, 0};
    private double zoom = 5.5;
    private double dZoom = 0;
    private int prog;
    private float colR = 0;
    private float dColR = 0.004f;

    private final LinkedList<String> logMessages = new LinkedList<>();

    private final ArrayList<VPlayer2> players = new ArrayList<>();
    private JFrame frame = null;
    private final AtomicBoolean isUpdating = new AtomicBoolean(false);
    private Thread thread = null;
    private long delay=(long) (250*TimeUtils.getDeltaTime());

    @Override
    public void display(GLAutoDrawable drawable) {

        final GL2 gl = drawable.getGL().getGL2();

        GLQueue.getInstance().execute(gl);

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
        zoom = Math.min(Math.max(1, zoom), 100);
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

        // Rendering players.
        for (VPlayer2 p : players) {
            p.render(gl, glut, prog);
        }
        for (VPlayer2 p : players) {
            p.renderPath(gl, glut, prog);
        }

        // Rendering 3D labels.
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glUniform4f(col, 1, 1, 1, 1);
        gl.glRasterPos2f(0, 0);
        glut.glutBitmapString(GLUT.BITMAP_8_BY_13, "ORIGO");
        gl.glPopMatrix();

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

        // Adding random players.
        addRandomPlayers();
    }

    private void appendLogMessage(String s) {
        SwingUtilities.invokeLater(() -> {
            logMessages.add(s);
            if (logMessages.size() > 10) {
                logMessages.pop();
            }
        });
    }

    private void renderLogMessages(GL2 gl) {
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        glu.gluOrtho2D(0, viewport[2], 0, viewport[3]);
        gl.glScalef(0, -1, 0);
        gl.glTranslatef(0, -viewport[3], 0);

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

    private void addRandomPlayers() {
        players.clear();

        //int rangePlayer[] = {-50, 50, -5, 5};
        int rangePlayer[] = {-50, 50, -50, 50};
        double rangeNavPoint[][] = {
            {-60, 60, 25, 27},
            {-50, 50, 50, 55},};

        int np = 10;
        //int nn[] = {1, rangeNavPoint.length + 1};
        int nn[] = {1, 4};
        double maxStep[] = {1, 3};
        double playerSize = 5;
        double playerScale = 1;
        for (int i = 0; i < np; i++) {
            VPlayer2 player = new VPlayer2("P" + i, new Vector3D(rnd(rangePlayer[0], rangePlayer[1]), rnd(rangePlayer[2], rangePlayer[3]), 0), rnd(playerSize / 2, playerSize) * playerScale, rnd(maxStep[0], maxStep[1]));
            int n = (int) rnd(nn[0], nn[1]);
            for (int j = 0; j < n; j++) {
                int k = j + 1 == n ? rangeNavPoint.length - 1 : j;
                //player.addNavigationPoint(new Vector3D(rnd(rangeNavPoint[k][0], rangeNavPoint[k][1]), rnd(rangeNavPoint[k][2], rangeNavPoint[k][3]), 0));
                player.addNavigationPoint(new Vector3D(rnd(rangePlayer[0], rangePlayer[1]), rnd(rangePlayer[2], rangePlayer[3]), 0));
            }
            player.initOrientation();
            players.add(player);
        }
        /*
        VPlayer2 player = new VPlayer2("P", new Vector3D(20,0, 0), 4, 3);
        player.addNavigationPoint(new Vector3D(-10, 1, 0));
        player.addNavigationPoint(new Vector3D(100, 4, 0));
        player.initOrientation();
        players.add(player);
        
        player = new VPlayer2("P", new Vector3D(0,0, 0), 5, 3);
        player.addNavigationPoint(new Vector3D(-10, 0, 0));
        player.addNavigationPoint(new Vector3D(60, 1, 0));
        player.addNavigationPoint(new Vector3D(10, 4, 0));
        player.initOrientation();
        players.add(player);
        
        player = new VPlayer2("P", new Vector3D(0,0, 0), 5, 3);
        player.addNavigationPoint(new Vector3D(-10, 0, 0));
        player.addNavigationPoint(new Vector3D(60, 1, 0));
        player.addNavigationPoint(new Vector3D(10, 4, 0));
        player.initOrientation();
        players.add(player);*/
    }

    private static double rnd(double a, double b) {
        if (a == b) {
            return a;
        }
        return ThreadLocalRandom.current().nextDouble(Math.min(a, b), Math.max(a, b));
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
            gl.glOrtho(-10 / lZoom * aspect, 10 / lZoom * aspect, -10 / lZoom, 10 / lZoom, -100, 100);
        } else {
            aspect = 1 / aspect;
            gl.glOrtho(-10 / lZoom, 10 / lZoom, -10 / lZoom * aspect, 10 / lZoom * aspect, -100, 100);
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

        frame = new JFrame("Editor");
        frame.getContentPane().add(glcanvas);
        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        final FPSAnimator animator = new FPSAnimator(glcanvas, 30, true);

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

        GLQueue.getInstance().add((GL2 gl) -> {

        });
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

        // Pan camera.
        if ((SwingUtilities.isMiddleMouseButton(e) && !e.isShiftDown()) || (e.isShiftDown() && SwingUtilities.isRightMouseButton(e))) {

            Matrix4d m = new Matrix4d(projection[0], projection[1], projection[2], projection[3], projection[4], projection[5], projection[6], projection[7], projection[8], projection[9], projection[10], projection[11], projection[12], projection[13], projection[14], projection[15]);
            Vector3d p0 = m.unproject(0, 0, 0, viewport, new Vector3d());
            Vector3d p1 = m.unproject(1, 0, 0, viewport, new Vector3d());
            Vector3d p2 = m.unproject(0, 1 / Math.sin(Math.PI / 4d), 0, viewport, new Vector3d());

            translate[0] += dx * new Vector3d(p0).sub(p1).length();
            translate[1] -= dy * new Vector3d(p0).sub(p2).length();
            return;
        }

        // Rotate camera.
        if (SwingUtilities.isRightMouseButton(e) || (e.isShiftDown() && SwingUtilities.isMiddleMouseButton(e))) {
            rotate[1] += dx * .3d;
            rotate[0] += dy * .3d;

        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        zoom += e.getPreciseWheelRotation();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
            case ' ':
                stopTimer();
                updatePlayerPositions();
                break;
            case 'i':
            case 'I':
                stopTimer();
                for (Player p:players){
                    p.initPosition();
                }
                break;
            case 'r':
            case 'R':
                stopTimer();
                addRandomPlayers();
                TimeUtils.timeUnitLeft=0;
                break;
            case 'p':
            case 'P':
                if (thread != null) {
                    stopTimer();
                    return;
                }

                thread = new Thread(() -> {
                    Runnable updater = () -> {
                        updatePlayerPositions();
                    };

                    while (thread != null) {
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException ex) {
                        }

                        if (!isUpdating.get()) //Platform.runLater(updater);
                        {
                            updater.run();
                        }
                    }
                });

                thread.setDaemon(true);
                thread.start();
                break;
        }
    }

    private void stopTimer() {

        if (thread == null) {
            return;
        }
        thread.interrupt();
        thread = null;
    }

    protected void interpolatePlayerPositions() {
        
        double dt = TimeUtils.getDeltaTime();
        
        //appendLogMessage("Interpolating...");
        for (Player p : players) {
            p.doOneStep(TimeUtils.timeUnitLeft, true);
        }
        //appendLogMessage("Done");
        
        TimeUtils.timeUnitLeft += dt;
        if (TimeUtils.timeUnitLeft>=TimeUtils.timeUnit){
            TimeUtils.timeUnitLeft = 0;
        }
        
        isUpdating.set(false);
    }

    public void updatePlayerPositions() {

        if (isUpdating.get()) {
            return;
        }

        TimeElapseMeter time = new TimeElapseMeter(true);

        isUpdating.set(true);
/*        
        for (Player p:players){
            System.out.println(p.getName()+"\t"+p.getOrientation()+"\t"+p.getOrientationTo()+"\t"+p.getOrientationLeft());
        }
        System.out.println("----");
*/
        if (TimeUtils.timeUnitLeft == 0) {

            appendLogMessage("Calculating...");
            ArrayList<Player> pal = new ArrayList<>();
            for (Player p : players) {
                pal.add(p);
                //p.doOneStep(1, false);
            }
            ObjectsPacker.packPlayerClusters(ObjectsPacker.clusterize(pal), true, () -> {
                for (ArrayList<Player> cluster2 : ObjectsPacker.clusterize(pal)) {
                    float c[] = cluster2.size() == 1 ? new float[]{1, 1, 1, 1} : VPlayer2.getRandomLightColor();
                    for (Player player2 : cluster2) {
                        ((VPlayer2) player2).setFillColor(c);
                    }
                }

                /*for (Player p : players) {
                    p.doOneStep(1, true);
                }*/
                
                appendLogMessage("Finished in " + time.stopAndGet());
                interpolatePlayerPositions();
                isUpdating.set(false);
            });
        } else {
            interpolatePlayerPositions();
            isUpdating.set(false);
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
