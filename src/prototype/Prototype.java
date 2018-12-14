package prototype;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Screen;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import physics.Vector3D;

// https://github.com/prasser/newtonraphson
// https://choco-tuto.readthedocs.io/en/latest/src/201.firstexample.html
// https://github.com/marcio-da-silva-arantes/MINLP
// R MINLPC
// https://dirkschumacher.github.io/ompr/
public class Prototype {

    private static final PannableCanvas CANVAS = new PannableCanvas();
    private static final NodeGestures NODE_GESTURES = new NodeGestures(CANVAS);
    private static final Group PLAYER_NODES_GROUP = new Group();
    private static final Group EDGES_GROUP = new Group();
    private static JPanel buttonsPanel;
    private static ArrayList<Player> players = new ArrayList<Player>();

    public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException, IOException {

        setLookAndFeel();
        JFrame frame = new JFrame("Prototype");
        frame.setLayout(new BorderLayout());
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel, BorderLayout.CENTER);

        buttonsPanel = new JPanel(new FlowLayout());
        frame.add(buttonsPanel, BorderLayout.EAST);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Platform.runLater(() -> {
            Group group = new Group();
            group.getChildren().add(CANVAS);
            CANVAS.getChildren().add(EDGES_GROUP);
            CANVAS.getChildren().add(PLAYER_NODES_GROUP);

            Scene scene = new Scene(group, Screen.getPrimary().getVisualBounds().getWidth() - 100, Screen.getPrimary().getVisualBounds().getHeight() - 100);
            frame.setSize((int) scene.getWidth(), (int) scene.getHeight());
            frame.setLocationRelativeTo(null);

            scene.setOnKeyTyped((KeyEvent e) -> {
                if (e.getCharacter().equals(" ")) {
                    for (Player p : players) {
                        p.oneStep();
                    }
                    return;
                }
            });

            updateScene();

            SceneGestures sceneGestures = new SceneGestures(CANVAS);
            scene.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
            scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
            scene.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

            CANVAS.setTranslateX((fxPanel.getWidth() - CANVAS.getBoundsInLocal().getWidth()) / 2);
            CANVAS.setTranslateY((fxPanel.getHeight() - CANVAS.getBoundsInLocal().getHeight()) / 2);

            // TEST
            CANVAS.setScale(4);
            CANVAS.setTranslateX(CANVAS.getTranslateX() + 300);
            CANVAS.setTranslateY(CANVAS.getTranslateY() + 300);

            fxPanel.setScene(scene);

            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        });

    }

    public static long combinations(int n, int k) {
        BigInteger factorialN = factorial(n);
        BigInteger factorialK = factorial(k);
        BigInteger factorialNMinusK = factorial(n - k);
        return factorialN.divide(factorialK.multiply(factorialNMinusK)).longValue();
    }

    private static BigInteger factorial(int n) {
        BigInteger ret = BigInteger.ONE;
        for (int i = 1; i <= n; ++i) {
            ret = ret.multiply(BigInteger.valueOf(i));
        }
        return ret;
    }

    private static void updateScene() {

        Line xAxis = new Line(0, 0, 100, 0);
        xAxis.setStroke(Color.RED);
        Line yAxis = new Line(0, 0, 0, 100);
        yAxis.setStroke(Color.BLUE);
        EDGES_GROUP.getChildren().addAll(xAxis, yAxis);

        VPlayer player = new VPlayer("player_1", new Vector3D(-50, 0, 0), NODE_GESTURES);
        player.addNavigationPoint(new Vector3D(10, -30, 0));
        player.addNavigationPoint(new Vector3D(100, 0, 0));
        player.addNavigationPoint(new Vector3D(100, 100, 0));
        PLAYER_NODES_GROUP.getChildren().add(player.getContainer());
        players.add(player);

        
 /*class MyCircle {

            public double r;
            public double px, py;
            public double nx, ny;
            public double cx, cy;
            public boolean immovable = false;

            public MyCircle(double r, double px, double py, double nx, double ny) {
                this.r = r;
                this.px = px;
                this.py = py;
                this.nx = nx;
                this.ny = ny;
            }
            
            public MyCircle(double r, double px, double py, double nx, double ny, boolean immovable) {
                this(r,px,py,nx,ny);
                this.immovable = immovable;
            }
        }

        ArrayList<MyCircle> circles = new ArrayList<>();
        circles.add(new MyCircle(20, -40, 0, 0, -30, true));
        circles.add(new MyCircle(10, 60, -50, 10, -20, !true));
        circles.add(new MyCircle(15, 20, 0, 0, -10, true));
        
        final int dimension = 2;

        Calcfc calcfc = new Calcfc() {
            @Override
            public double Compute(int n, int m, double[] x, double[] c) {
                
                int cCounter = 0;
                
                double dSum = 0;
                for (int i = 0; i < circles.size(); i++) {
                    MyCircle circle = circles.get(i);
                    dSum += Math.sqrt(Math.pow(circle.nx-x[i*dimension],2d)+Math.pow(circle.ny-x[i*dimension+1],2d));
                    
                    c[cCounter++] = Math.sqrt(Math.pow(circle.px-circle.nx,2d)+Math.pow(circle.py-circle.ny,2d))-Math.sqrt(Math.pow(circle.px-x[i*dimension],2d)+Math.pow(circle.py-x[i*dimension+1],2d));
                    
                    if (circle.immovable) {
                        x[i*dimension] = circle.nx;
                        x[i*dimension+1] = circle.ny;
                    }
                }
                
                for (int i = 0; i < circles.size()-1; i++) {
                    MyCircle ci = circles.get(i);
                    for (int j = i+1; j < circles.size(); j++) {
                        MyCircle cj = circles.get(j);
                        
                        if (ci.immovable && cj.immovable) {
                            cCounter++;
                            continue;
                        }
                        
                        c[cCounter++]=Math.sqrt(Math.pow(x[i*dimension]-x[j*dimension],2d)+Math.pow(x[i*dimension+1]-x[j*dimension+1],2d))-ci.r-cj.r;
                    }
                }
                
                return dSum;
            }
        };

        LinkedList<Double> xx = new LinkedList<>();
        for (MyCircle c : circles) {
            xx.add(c.nx);
            xx.add(c.ny);
        }

        double[] x = new double[xx.size()];
        for (int i = 0; i < xx.size(); i++) {
            x[i] = xx.get(i);
        }
        double rhobeg = 0.5;
        double rhoend = 1.0e-6;
        int iprint = 0;
        int maxfun = 3500;
        CobylaExitStatus result = Cobyla.FindMinimum(calcfc, x.length, (int) combinations(circles.size(), 2)+circles.size(), x, rhobeg, rhoend, iprint, maxfun);
        System.out.println(result.equals(CobylaExitStatus.Normal));
        
        for (int i = 0; i < circles.size(); i++) {
            MyCircle c = circles.get(i);
            c.cx = x[i*dimension];
            c.cy = x[i*dimension+1];
        }

        for (MyCircle ci : circles) {
            Circle c = new Circle(ci.nx, ci.ny, ci.r, null);
            c.getStrokeDashArray().addAll(1d, 2d);
            c.setStroke(Color.BLACK);
            c.setStrokeWidth(.2d);
            c.setStrokeLineCap(StrokeLineCap.ROUND);
            PLAYER_NODES_GROUP.getChildren().add(c);

            c = new Circle(ci.px, ci.py, ci.r, null);
            c.getStrokeDashArray().addAll(1d, 4d);
            c.setStroke(Color.BLACK);
            c.setStrokeWidth(.2d);
            c.setStrokeLineCap(StrokeLineCap.ROUND);
            PLAYER_NODES_GROUP.getChildren().add(c);

            c = new Circle(ci.cx, ci.cy, ci.r, null);
            c.setStroke(Color.BLACK);
            c.setStrokeWidth(.5d);
            c.setStrokeLineCap(StrokeLineCap.ROUND);
            PLAYER_NODES_GROUP.getChildren().add(c);

            Line l = new Line(ci.px, ci.py, ci.nx, ci.ny);
            l.setStrokeLineCap(StrokeLineCap.ROUND);
            l.setStrokeWidth(.2d);
            l.getStrokeDashArray().addAll(2d);
            PLAYER_NODES_GROUP.getChildren().add(l);
            l = new Line(ci.px, ci.py, ci.cx, ci.cy);
            l.setStrokeLineCap(StrokeLineCap.ROUND);
            l.setStrokeWidth(.2d);
            PLAYER_NODES_GROUP.getChildren().add(l);
        }*/
    }

    public static void setLookAndFeel() {
        // Téma beállítása.
        javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(new MyMetalTheme());
        // Az ablakkeret az operációs rendszeré szeretnénk, hogy legyen.
        JFrame.setDefaultLookAndFeelDecorated(false);
        // Egyes témák esetében az alapértelmezett Enter leütés nem csinál semmit, ezért engedélyezzük külön.
        UIManager.getLookAndFeelDefaults().put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
        // Görgetősávok témájának megváltoztatása sajátra, mert a lila szerintem túl gagyi.
        UIManager.getLookAndFeelDefaults().put("ScrollBarUI", "lottery.SimpleScrollBarUI");
        // Folyamatjelző felirata legyen fekete.
        UIManager.put("ProgressBar.selectionForeground", Color.BLACK);
        UIManager.put("ProgressBar.selectionBackground", Color.BLACK);
    }

}
