package hoe.editor;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import hoe.Player;
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
import hoe.math.Lined;
import hoe.math.Rayd;
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
import hoe.nonlinear.ObjectsPacker;
import org.joml.Vector3d;
import hoe.physics.Vector3D;
import hoe.renderer.Camera;
import hoe.renderer.shaders.PhongShader;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class Editor implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    private final GLU glu = new GLU();
    private final GLUT glut = new GLUT();

    double panUnits[] = {1, 1};

    private final Camera camera = new Camera();

    private final ArrayList<Vector3d> points = new ArrayList<>();

    private final double dRotate[] = new double[]{0, 0};
    private final double dTranslate[] = new double[]{0, 0};
    private double dZoom = 0;

    private int prog;
    private float colR = 0;
    private float dColR = 0.004f;

    private final LinkedList<String> logMessages = new LinkedList<>();

    private final ArrayList<VPlayer> players = new ArrayList<>();
    private JFrame frame = null;
    private final AtomicBoolean isUpdating = new AtomicBoolean(false);
    private Thread thread = null;
    private final long delay = (long) (250 * TimeUtils.getDeltaTime());
    
    DiscreteDynamicsWorld dynamicsWorld = null;
    private PhongShader phongShader;

    public Editor() {
        getCamera().setZoomLimits(1, 10);
        getCamera().setZoom(3);
        
        initPhysics();
    }
    
    private void initPhysics() {
        BroadphaseInterface broadphase = new DbvtBroadphase();
        DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        dynamicsWorld.setGravity(new Vector3f(0, -10, 0));

        CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 1);
        CollisionShape fallShape = new SphereShape(1);

        DefaultMotionState groundMotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, -1, 0), 1.0f)));

        RigidBodyConstructionInfo groundRigidBodyCI = new RigidBodyConstructionInfo(0, groundMotionState, groundShape, new Vector3f(0, 0, 0));
        RigidBody groundRigidBody = new RigidBody(groundRigidBodyCI);

        dynamicsWorld.addRigidBody(groundRigidBody); // add our ground to the dynamic world.. 

        DefaultMotionState fallMotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, 50, 0), 1.0f)));

        int mass = 1;

        Vector3f fallInertia = new Vector3f(0, 0, 0);
        fallShape.calculateLocalInertia(mass, fallInertia);

        RigidBodyConstructionInfo fallRigidBodyCI = new RigidBodyConstructionInfo(mass, fallMotionState, fallShape, fallInertia);
        RigidBody fallRigidBody = new RigidBody(fallRigidBodyCI);

        dynamicsWorld.addRigidBody(fallRigidBody);
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

        gl.glUseProgram(prog);
        int col = gl.glGetUniformLocation(prog, "col");
        colR += dColR;
        if (colR > 1f || colR < 0f) {
            dColR = -dColR;
        }
        gl.glUniform4f(col, colR, 0, 1, 1);

        // Rendering players.
        
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT_AND_DIFFUSE, new float[]{1,1,1},0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, new float[]{1,1,1},0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[]{0,0,50},0);
        
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT_AND_DIFFUSE, new float[]{1,0,0},0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, new float[]{1,0,0},0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, new float[]{0,0,-50},0);
                
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, new float[]{0,0,0},0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, new float[]{0,0,1},0);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, new float[]{.4f,.4f,.4f},0);
        gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 10);
        
        Vector3d dir = new Vector3d(getCamera().getEye());
        phongShader.apply(dir);
        for (VPlayer p : players) {
            p.render(gl, glut, prog);
        }
        for (VPlayer p : players) {
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

        // Rendering points.
        renderPoints(gl);

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
        
        phongShader = new PhongShader(gl);

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

    private void renderPoints(GL2 gl) {
        gl.glPushMatrix();

        gl.glUseProgram(0);
        gl.glDisable(GL2.GL_LIGHTING);

        gl.glPointSize(10);
        int i = 0;
        for (Vector3d p : points) {
            gl.glColor3f(1.0f, 1.0f, 1.0f);
            gl.glBegin(GL2.GL_POINTS);
            gl.glVertex3f((float) p.x, (float) p.y, (float) p.z);
            gl.glEnd();

            gl.glPushMatrix();
            gl.glLoadIdentity();
            float s = (float) (getCamera().getZoom()) * .1f;
            gl.glColor3f(1f, .3882f, .0157f);
            gl.glRasterPos3f((float) p.x + s, (float) p.y + s, (float) p.z + s);
            glut.glutBitmapString(GLUT.BITMAP_8_BY_13, "" + i++);
            gl.glPopMatrix();
        }

    }

    private void addRandomPlayers() {

        players.clear();

        //int rangePlayer[] = {-50, 50, -5, 5};
        int rangePlayer[] = {-50, 50, -50, 50};
        double rangeNavPoint[][] = {
            {-60, 60, 25, 27},
            {-50, 50, 50, 55},};

        int np = 20;
        //int nn[] = {1, rangeNavPoint.length + 1};
        int nn[] = {1, 4};
        double maxStep[] = {1, 3};
        double playerSize = 5;
        double playerScale = 1;
        for (int i = 0; i < np; i++) {
            VPlayer player = new VPlayer("P" + i, new Vector3D(rnd(rangePlayer[0], rangePlayer[1]), rnd(rangePlayer[2], rangePlayer[3]), 0), rnd(playerSize / 2, playerSize) * playerScale, rnd(maxStep[0], maxStep[1]));
            int n = (int) rnd(nn[0], nn[1]);
            for (int j = 0; j < n; j++) {
                int k = j + 1 == n ? rangeNavPoint.length - 1 : j;
                //player.addNavigationPoint(new Vector3D(rnd(rangeNavPoint[k][0], rangeNavPoint[k][1]), rnd(rangeNavPoint[k][2], rangeNavPoint[k][3]), 0));
                player.addNavigationPoint(new Vector3D(rnd(rangePlayer[0], rangePlayer[1]), rnd(rangePlayer[2], rangePlayer[3]), 0));
            }
            player.setOrientation(rnd(0, 360));
            player.initOrientation();
            //players.add(player);
        }

        VPlayer player = new VPlayer("p", new Vector3D(), 5, 4);
        player.addNavigationPoint(new Vector3D());
        player.initOrientation();
        players.add(player);

        player = new VPlayer("q", new Vector3D(15, 0, 0), 5, 4);
        player.addNavigationPoint(new Vector3D());
        player.initOrientation();
        players.add(player);

        // Add points.
        points.add(new Vector3d(5, 0, 0));
        points.add(new Vector3d(-5, 0, 0));
        points.add(new Vector3d(0, 5, 0));
        points.add(new Vector3d(0, -5, 0));
        points.add(new Vector3d(0, 0, 5));
        points.add(new Vector3d(0, 0, -5));

    }

    public static double rnd(double a, double b) {
        if (a == b) {
            return a;
        }
        return ThreadLocalRandom.current().nextDouble(Math.min(a, b), Math.max(a, b));
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

        final GL2 gl = drawable.getGL().getGL2();
        height = Math.max(height, 1);

        gl.glViewport(0, 0, width, height);
        getCamera().setViewportSize(width, height);
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

        // For camera pannign.
        prev = new int[]{e.getX(), e.getY()};

        GLQueue.getInstance().add((GL2 gl) -> {
        });
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        Rayd ray = getCamera().calculateRay(e.getX(), e.getY());
        Vector3d dest = Camera.getDestinationFromRay(ray);
        Vector3d dir = ray.getDirection();
        Lined line = ray.toLine();
        double dMin = Double.POSITIVE_INFINITY;
        int pIndex = -1;
        int i = 0;
        for (Vector3d p : points) {

            i++;

            // If not close enough to the view ray...
            if (line.distance(p) > .08d * getCamera().getZoom()) {
                continue;
            }

            // Compare the distance to the closest point to viewplane.
            double d = getCamera().getProjectionMatrix().transformPosition(p, new Vector3d()).z;

            // If the point is behind the near plane...
            if (d < -1) {
                continue;
            }

            if (d < dMin || pIndex < 0) {
                pIndex = i - 1;
                dMin = d;
            }
        }

        //System.out.println(pIndex + ". " + dMin);
        if (pIndex != -1) {
            appendLogMessage(pIndex + ". is selected");
        }

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

        double direction = e.getPreciseWheelRotation() / Math.abs(e.getPreciseWheelRotation());

        Vector3d p0 = getCamera().getProjectionMatrix().unproject(0, 0, 0, getCamera().getViewport(), new Vector3d());
        Vector3d p1 = getCamera().getProjectionMatrix().unproject(1, 1, 0, getCamera().getViewport(), new Vector3d());

        double dl = p0.distanceSquared(p1) / (Math.pow(getCamera().getViewportWidth(), 2) + Math.pow(getCamera().getViewportHeight(), 2));
        double s = getCamera().getZoom() * 400;
        double ds = (s * s * dl) / (s * dl + Math.sqrt(Math.pow(getCamera().getViewportWidth(), 2) + Math.pow(getCamera().getViewportHeight(), 2)));
        //double ds = Math.min((s * s * dl) / (s * dl + getCamera().getViewportWidth()), (s * s * dl) / (s * dl + getCamera().getViewportHeight()));
        //double ds = Math.min((s * dl + dl) / (s * dl + getCamera().getViewportWidth()), (s * dl + dl) / (s * dl + getCamera().getViewportHeight()));
        //double ds = Math.min((s * dl) / (s * getCamera().getViewportWidth()), (s * dl) / (s * getCamera().getViewportHeight()));
        //double ds = s*Math.min(dl / getCamera().getViewportWidth(), dl/ getCamera().getViewportHeight());

        //getCamera().setZoom(getCamera().getZoom() + ds * direction);
        getCamera().setZoom(getCamera().getZoom() + e.getPreciseWheelRotation());
        
        //getCamera().setZoom(getCamera().getZoom()+direction*(1/Math.pow(dl,2)));
        //getCamera().setZoom(getCamera().getZoom()+direction*dl*200);
        //getCamera().setZoom(Math.min(100,Math.max(getCamera().getZoom(), 1)));
        //System.out.println(dl + " " + ds);
        //System.out.println(dl);
        //System.out.println(getCamera().getZoom());
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
                for (Player p : players) {
                    p.initPosition();
                }
                break;
            case 'r':
            case 'R':
                stopTimer();
                addRandomPlayers();
                TimeUtils.timeUnitLeft = 0;
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
                        dynamicsWorld.stepSimulation(1 / 100.f, 10);
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
        if (TimeUtils.timeUnitLeft >= TimeUtils.timeUnit) {
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
                    float c[] = cluster2.size() == 1 ? new float[]{1, 1, 1, 1} : VPlayer.getRandomLightColor();
                    for (Player player2 : cluster2) {
                        ((VPlayer) player2).setFillColor(c);
                    }
                }

                /*for (Player p : players) {
                 p.doOneStep(1, true);
                 }*/
                appendLogMessage("Finished in " + time.stopAndGetFormat());
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
