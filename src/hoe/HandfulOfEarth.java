package hoe;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;
import com.sun.management.HotSpotDiagnosticMXBean;
import fonts.FontUtils;
import hoe.designer.NetworkDesigner;
import hoe.servers.GameServer;
import hoe.editor.Editor;
import hoe.editor.MyMetalTheme;
import hoe.editor.ShaderEditor;
import hoe.editor.TimeElapseMeter;
import hoe.nonlinear.ObjectsPacker;
import hoe.renderer.OfflineRendererWindow;
import hoe.renderer.RenderCallback;
import hoe.renderer.shaders.PhongShader;
import hoe.servers.ContentServer;
import hoe.servers.RedirectServer;
import hoe.servers.RenderServer;
import hoe.skeleton.Skeleton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import javax.management.MBeanServer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.joml.Matrix4d;
import org.joml.Vector3d;

/**
 * BUGFIX: Safari (on iOS) has some issues to show redirected images, so it
 * would be good to turn off "Prevent cross-site tracking" feature
 * https://support.securly.com/hc/en-us/articles/360000881087-How-to-resolve-the-too-many-redirects-error-on-Safari-
 */
public class HandfulOfEarth {

    public static void main__(String[] args) {

        Vector3d p0 = new Vector3d();
        double twist1 = 0, d1 = 0, a1 = 10, ang1 = 45;
        Matrix4d m1 = new Matrix4d().rotateLocalX(Math.toRadians(180)).translateLocal(a1, 0, 0).rotateLocalY(Math.toRadians(ang1));
        Vector3d p1 = m1.transformPosition(new Vector3d());
        double twist2 = 0, d2 = 0, a2 = 10, ang2 = -45;
        Matrix4d m2 = m1.mul(new Matrix4d().translateLocal(a2, 0, 0).rotateLocalY(Math.toRadians(ang2)));
        Vector3d p2 = m2.transformPosition(new Vector3d());

        System.out.println(p0);
        System.out.println(p1);

        // http://joml-ci.github.io/JOML/
        Skeleton skeleton = new Skeleton();

        //struct.c
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());

        Dimension size = new Dimension(1200, 800);
        BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);

        double scale = 20d;

        JLabel label = new JLabel(new ImageIcon(image));

        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform transform = new AffineTransform();
        transform.setToIdentity();

        transform.scale(1, -1);
        transform.translate(size.width / 2, -size.height / 2);

        Runnable render = () -> {

            g2d.setTransform(new AffineTransform());
            g2d.clearRect(0, 0, size.width, size.height);
            g2d.setTransform(transform);

            //skeleton.render(g2d, scale);
            g2d.setColor(Color.white);
            g2d.drawLine((int) (p0.x * scale), (int) (p0.z * scale), (int) (p1.x * scale), (int) (p1.z * scale));
            g2d.setColor(Color.red);
            g2d.drawLine((int) (p1.x * scale), (int) (p1.z * scale), (int) (p2.x * scale), (int) (p2.z * scale));

            // Axises
            g2d.setColor(Color.red);
            g2d.drawLine(0, 0, 10, 0);
            g2d.setColor(Color.green);
            g2d.drawLine(0, 0, 0, 10);

            label.updateUI();
        };

        render.run();

        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        label.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        label.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

                Point2D p = new Point2D.Float(e.getX(), e.getY());

                try {

                    Point2D q = transform.inverseTransform(p, null);
                    q.setLocation(q.getX() / scale, q.getY() / scale);

                    if (SwingUtilities.isLeftMouseButton(e)) {
                        skeleton.getLeftArm().setTarget(new Vector3d(q.getX(), q.getY(), 0));
                    }

                    if (SwingUtilities.isRightMouseButton(e)) {
                        skeleton.getRightArm().setTarget(new Vector3d(q.getX(), q.getY(), 0));
                    }

                    //skeleton.solveTarget();
                    render.run();
                } catch (NoninvertibleTransformException ex) {

                }

            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        System.exit(0);
                }
            }
        });
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public static void main_(String[] args) throws Exception {
        TimeElapseMeter tem = new TimeElapseMeter();
        ArrayList<Player> players = new ArrayList<>();
        int n = (int) Editor.rnd(-10, 10);
        ObjectsPacker.packPlayers(players, false);
        System.out.println(tem.stopAndGetFormat());
    }

    /**
     * NOTE: Correct order to run servers: 1.database 2.redirect 3.minimum one
     * content/render 4.game NOTE: enable OGL debug: -Djogl.debug.DebugGL=1
     *
     * @param args arguments
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {

        if (Arrays.asList(args).contains("-heap")) {
            dumpHeap("heap.hprof", false);
        }
        
        if (Arrays.asList(args).contains("-network_designer") || Arrays.asList(args).contains("-nd")) {
            setLookAndFeel();
            NetworkDesigner designer = new NetworkDesigner();
            designer.setVisible(true);
            return;
        }

        if (Arrays.asList(args).contains("-editor") || Arrays.asList(args).contains("-e")) {
            setLookAndFeel();
            Editor editor = new Editor();
            editor.show();
            return;
        }

        if (Arrays.asList(args).contains("-shader_editor") || Arrays.asList(args).contains("-se")) {
            setLookAndFeel();
            ShaderEditor editor = new ShaderEditor();
            editor.show();
            return;
        }

        if (Arrays.asList(args).contains("-render_scene") || Arrays.asList(args).contains("-rs")) {
            setLookAndFeel();

            int tileSize = 300;
            int multisample = 1;
            double tileWorldSize = 5;
            int rows[] = {-1, 1};
            int columns[] = {-1, 1};

            OfflineRendererWindow orw = new OfflineRendererWindow(tileSize, multisample, tileWorldSize, rows, columns, new RenderCallback() {

                @Override
                public void render() {
                    GL2 gl = getGL();
                    GLUT glut = getGLUT();

                    // Rendering.
                    PhongShader phongShader = getPhongShader();
                    phongShader.apply();

                    gl.glPushMatrix();
                    gl.glTranslated(0, 0, 0);
                    gl.glRotated(15, 0, 0, 1);
                    glut.glutSolidTeapot(3, false);
                    gl.glPopMatrix();
                    /*
                     TextureShader textureShader = new TextureShader(gl);
                     textureShader.apply();

                     Texture texture = RenderServlet.createCheckerTexture(gl, 512, 4, Color.red, Color.green);
                     Texture texture2 = RenderServlet.createCircleTexture(gl, 1024, Color.lightGray);

                     textureShader.setTextures(texture, texture2);
                     */
                    gl.glPushMatrix();
                    gl.glRotated(15, 0, 0, 1);
                    gl.glBegin(GL2.GL_QUADS);
                    double s = 6;
                    gl.glNormal3d(0, 0, 1);
                    gl.glTexCoord2d(0, 0);
                    gl.glVertex3d(-s, -s, 0);
                    gl.glNormal3d(0, 0, 1);
                    gl.glTexCoord2d(1, 0);
                    gl.glVertex3d(s, -s, 0);
                    gl.glNormal3d(0, 0, 1);
                    gl.glTexCoord2d(1, 1);
                    gl.glVertex3d(s, s, 0);
                    gl.glNormal3d(0, 0, 1);
                    gl.glTexCoord2d(0, 1);
                    gl.glVertex3d(-s, s, 0);
                    gl.glEnd();
                    gl.glPopMatrix();

                }
            });

            return;
        }

        Properties prop = new Properties();

        prop.load(new BufferedReader(new FileReader(args[0])));

        //for (int i = 0; i < 10; i++) System.out.println(UUID.randomUUID());
        String propKey;

        propKey = "debug";
        Log.showDebugMessages = (prop.containsKey(propKey) && prop.getProperty(propKey).toLowerCase().equals("true"));

        Language.init();

        Cryptography.setKey(prop.getProperty("secretkey"));

        String ip = prop.getProperty("ip");

        propKey = "startdbserver";

        if (prop.containsKey(propKey)
                && prop.getProperty(propKey).toLowerCase().equals("true")) {
            String webPort = prop.getProperty("dbserverwebport");
            String tcpPort = prop.getProperty("dbservertcpport");
            hoe.servers.DatabaseServer.startServers(webPort, tcpPort);
        }

        String sceneDbIp = prop.getProperty("scenedbip");
        if (sceneDbIp
                != null) {
            SceneManager.setDataBaseIp(sceneDbIp);
        }

        propKey = "startredirectserver";

        if (prop.containsKey(propKey)
                && prop.getProperty(propKey).toLowerCase().equals("true")) {
            int port = Integer.parseInt(prop.getProperty("redirectserverport"));
            RedirectServer server = new RedirectServer(ip, port);
            server.start();
        }

        propKey = "startrenderserver";

        if (prop.containsKey(propKey)
                && prop.getProperty(propKey).toLowerCase().equals("true")) {
            int port = Integer.parseInt(prop.getProperty("renderserverport"));
            RenderServer server = new RenderServer(ip, port);
            server.setRedirectServerUrl(prop.getProperty("redirectserverurl"));
            server.start();
        }

        propKey = "startcontentserver";

        if (prop.containsKey(propKey)
                && prop.getProperty(propKey).toLowerCase().equals("true")) {
            int port = Integer.parseInt(prop.getProperty("contentserverport"));
            boolean clearCache = false;
            propKey = "clearcontentservercache";
            if (prop.containsKey(propKey) && prop.getProperty(propKey).toLowerCase().equals("true")) {
                clearCache = true;
            }
            ContentServer server = new ContentServer(ip, port, clearCache);
            server.setRedirectServerUrl(prop.getProperty("redirectserverurl"));
            server.start();
        }

        propKey = "startgameserver";

        if (prop.containsKey(propKey)
                && prop.getProperty(propKey).toLowerCase().equals("true")) {
            try {
                String userDbIp = prop.getProperty("userdbip");
                UserManager.setDataBaseIp(userDbIp);

                int port = Integer.parseInt(prop.getProperty("gameserverport"));
                GameServer server = new GameServer(ip, port);
                server.setRedirectServerUrl(prop.getProperty("redirectserverurl"));
//                SceneManager.generate();
                server.start();

            } catch (Exception ex) {
                Log.error(Language.getText(LanguageMessageKey.CREATING_SERVER_FAILED), ex);
            }
        }

    }

    public static void setLookAndFeel() {
        // Betűtípus regisztrálása.
        FontUtils.registerFont();
        // Téma beállítása.
        javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new MyMetalTheme());
        // Az ablakkeret az operációs rendszeré szeretnénk, hogy legyen.
        JFrame.setDefaultLookAndFeelDecorated(false);
        // Egyes témák esetében az alapértelmezett Enter leütés nem csinál semmit, ezért engedélyezzük külön.
        UIManager.getLookAndFeelDefaults().put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
        // Görgetősávok témájának megváltoztatása sajátra, mert a lila szerintem túl gagyi.
        UIManager.getLookAndFeelDefaults().put("ScrollBarUI", "hoe.editor.SimpleScrollBarUI");
        // Folyamatjelző felirata legyen fekete.
        UIManager.put("ProgressBar.selectionForeground", javafx.scene.paint.Color.BLACK);
        UIManager.put("ProgressBar.selectionBackground", javafx.scene.paint.Color.BLACK);
    }

    public static void dumpHeap(String filePath, boolean live) throws IOException {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        HotSpotDiagnosticMXBean mxBean = ManagementFactory.newPlatformMXBeanProxy(
                server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);
        mxBean.dumpHeap(filePath, live);
    }
}
