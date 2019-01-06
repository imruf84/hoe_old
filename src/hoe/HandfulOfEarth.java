package hoe;

import au.edu.federation.caliko.FabrikBone2D;
import au.edu.federation.caliko.FabrikChain2D;
import au.edu.federation.caliko.FabrikStructure2D;
import au.edu.federation.utils.Vec2f;
import hoe.servers.GameServer;
import hoe.editor.Editor;
import hoe.servers.ContentServer;
import hoe.servers.RedirectServer;
import hoe.skeleton.Joint;
import hoe.skeleton.JointChain;
import hoe.skeleton.Skeleton;
import java.awt.BasicStroke;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.joml.Vector3d;

/**
 * BUGFIX: Safari (on iOS) has some issues to show redirected images, so it
 * would be good to turn off "Prevent cross-site tracking" feature
 * https://support.securly.com/hc/en-us/articles/360000881087-How-to-resolve-the-too-many-redirects-error-on-Safari-
 */
public class HandfulOfEarth {

    public static FabrikStructure2D cloneStructure(FabrikStructure2D src) {

        FabrikStructure2D result = new FabrikStructure2D();

        for (int i = 0; i < src.getNumChains(); i++) {
            FabrikChain2D chain = src.getChain(i);

            result.addChain(new FabrikChain2D(chain));
        }

        return result;
    }

    public static void copyBonePositions(FabrikStructure2D from, FabrikStructure2D to) {

        for (int i = 0; i < from.getNumChains(); i++) {
            FabrikChain2D chainFrom = from.getChain(i);
            FabrikChain2D chainTo = to.getChain(i);

            for (int j = 0; j < chainFrom.getNumBones(); j++) {
                FabrikBone2D boneFrom = chainFrom.getBone(j);
                FabrikBone2D boneTo = chainTo.getBone(j);
                boneTo.setStartLocation(boneFrom.getStartLocation());
                boneTo.setEndLocation(boneFrom.getEndLocation());
            }
        }

    }
    
    public static void main___(String[] args) {
        Vector3d A = new Vector3d(-1,2,4);
        Vector3d B = new Vector3d(4,-5,-1);
        Vector3d C = new Vector3d(5,4,6);
        Vector3d P = new Vector3d();
        
        Vector3d AB=B.sub(A,new Vector3d());
        Vector3d AC=C.sub(A,new Vector3d());
        Vector3d n = AB.cross(AC).normalize();
        double D = -n.dot(A);
        double d = Math.abs(n.dot(P)+D);
        
        System.out.println(d);
    }

    public static void main(String[] args) {

        // http://joml-ci.github.io/JOML/
        /*JointChain jc = new JointChain();
        jc.setOffset(new Vector3d(1, -1, 0));

        jc.appendJoint(new Joint(5, 90, 180, 90));
        jc.appendJoint(new Joint(4, 0, 0, 90));
        jc.appendJoint(new Joint(2, 0, 0, 60));

        jc.updatePositions();*/
        
        Skeleton skeleton = new Skeleton();

        float restLength = 4;

        FabrikStructure2D struct = new FabrikStructure2D();
        FabrikChain2D chain1 = new FabrikChain2D();
        chain1.setEmbeddedTargetMode(true);
        chain1.setFixedBaseMode(false);
        chain1.addBone(new FabrikBone2D(new Vec2f(-restLength / 2f, 0), new Vec2f(-1, 0).rotateRads((float) Math.toRadians(50)), 5));
        chain1.addConsecutiveBone(new Vec2f(-1, 0).rotateRads((float) Math.toRadians(70)), 5);
        struct.addChain(chain1);
        chain1.updateEmbeddedTarget(chain1.getEffectorLocation());

        FabrikChain2D chain2 = new FabrikChain2D();
        chain2.setEmbeddedTargetMode(true);
        chain2.setFixedBaseMode(false);
        chain2.addBone(new FabrikBone2D(new Vec2f(restLength / 2f, 0), new Vec2f(1, 0), 5));
        chain2.addConsecutiveBone(new Vec2f(1, 0), 5);
        struct.addChain(chain2);
        chain2.updateEmbeddedTarget(chain2.getEffectorLocation());

        FabrikStructure2D structClone = cloneStructure(struct);

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

            skeleton.render(g2d, scale);

            /*t = chain2.getLastTargetLocation();
            s -= 3;
            g2d.setColor(Color.orange);
            g2d.drawLine((int) (t.x * scale - s), (int) (t.y * scale), (int) (t.x * scale + s), (int) (t.y * scale));
            g2d.drawLine((int) (t.x * scale), (int) (t.y * scale - s), (int) (t.x * scale), (int) (t.y * scale + s));*/

            // base
            /*Vector3d b = jc.getOffset();
            s*=4;
            g2d.setColor(Color.pink);
            g2d.drawLine((int) (b.x * scale - s), (int) (b.y * scale), (int) (b.x * scale + s), (int) (b.y * scale));*/
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
                /*Point2D p = new Point2D.Float(e.getX(), e.getY());

                try {

                    Point2D q = transform.inverseTransform(p, null);
                    q.setLocation(q.getX() / scale, q.getY() / scale);

                    if (SwingUtilities.isLeftMouseButton(e)) {
                        chain1.updateEmbeddedTarget(new Vec2f((float) q.getX(), (float) q.getY()));
                        jc.setTarget(new Vector3d(q.getX(), q.getY(), 0));
                    }
                    if (SwingUtilities.isRightMouseButton(e)) {
                        chain2.updateEmbeddedTarget(new Vec2f((float) q.getX(), (float) q.getY()));
                    }
                    
                    jc.solveTarget();

                    
                    copyBonePositions(structClone, struct);

                    chain1.setFixedBaseMode(!true);
                    chain2.setFixedBaseMode(!true);
                    struct.solveForTarget(new Vec2f());

                    Vec2f v1 = new Vec2f(chain1.getBaseLocation());
                    Vec2f v2 = new Vec2f(chain2.getBaseLocation());

                    Vec2f delta = new Vec2f(v1).minus(v2);
                    double deltalength = delta.length();

                    double diff = (deltalength - restLength) / deltalength;
                    delta = delta.times(.5f * (float) diff);

                    chain1.setBaseLocation(v1.minus(delta));
                    chain2.setBaseLocation(v2.plus(delta));
                    chain1.setFixedBaseMode(true);
                    chain2.setFixedBaseMode(true);

                    struct.solveForTarget(new Vec2f());

                    //System.out.println(v1+" "+v2+" "+v1.minus(v2).length());
                    //}
                    render.run();

                } catch (NoninvertibleTransformException ex) {
                }*/
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

                    skeleton.solveTarget();
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
    
    private void updateChainAndRender(MouseEvent e) {
        
    }

    public static void main__(String[] args) throws Exception {
        try {
            String data = "test data";

            //URL url = new URL("http://192.168.0.25:8090/calc");
            URL url = new URL("http://127.0.0.1:8090/calc");
            String encoding = Base64.getEncoder().encodeToString(("admin:admin").getBytes(StandardCharsets.UTF_8));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Basic " + encoding);
            connection.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
            InputStream content = (InputStream) connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            Log.error(e);
        }
    }

    public static void main_(String[] args) throws Exception {

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
        if (prop.containsKey(propKey) && prop.getProperty(propKey).toLowerCase().equals("true")) {
            String webPort = prop.getProperty("dbserverwebport");
            String tcpPort = prop.getProperty("dbservertcpport");
            hoe.servers.DatabaseServer.startServers(webPort, tcpPort);
        }

        propKey = "startredirectserver";
        if (prop.containsKey(propKey) && prop.getProperty(propKey).toLowerCase().equals("true")) {
            int port = Integer.parseInt(prop.getProperty("redirectserverport"));
            RedirectServer server = new RedirectServer(ip, port);
            server.start();
        }

        propKey = "startcontentserver";
        if (prop.containsKey(propKey) && prop.getProperty(propKey).toLowerCase().equals("true")) {
            int port = Integer.parseInt(prop.getProperty("contentserverport"));
            ContentServer server = new ContentServer(ip, port);
            server.setRedirectServerUrl(prop.getProperty("redirectserverurl"));
            server.start();
        }

        propKey = "startgameserver";
        if (prop.containsKey(propKey) && prop.getProperty(propKey).toLowerCase().equals("true")) {
            try {
                String userDbIp = prop.getProperty("userdbip");
                UserManager.setDataBaseIp(userDbIp);

                String sceneDbIp = prop.getProperty("scenedbip");
                SceneManager.setDataBaseIp(sceneDbIp);

                int port = Integer.parseInt(prop.getProperty("gameserverport"));
                GameServer server = new GameServer(ip, port);
                server.setRedirectServerUrl(prop.getProperty("redirectserverurl"));
                server.start();

            } catch (Exception ex) {
                Log.error(Language.getText(LanguageMessageKey.CREATING_SERVER_FAILED), ex);
            }
        }

        propKey = "runeditor";
        if (prop.containsKey(propKey) && prop.getProperty(propKey).toLowerCase().equals("true")) {
            Editor editor = new Editor();
            editor.show();
        }

    }

}
