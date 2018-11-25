package prototype;

import Jama.Matrix;
import java.awt.BorderLayout;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Screen;
import javax.swing.JFrame;
import javax.swing.UIManager;
import physics.Vector3D;

// http://hz2.org/blog/hobby_curve.html
// http://i.stanford.edu/pub/cstr/reports/cs/tr/85/1047/CS-TR-85-1047.pdf
// https://github.com/qzyse2017/k-Curve/blob/master/kCurve.py
// https://www.stkent.com/2015/07/03/building-smooth-paths-using-bezier-curves.html
public class Prototype {

    private static final PannableCanvas CANVAS = new PannableCanvas();
    private static final NodeGestures NODE_GESTURES = new NodeGestures(CANVAS);
    private static final Group NODES_GROUP = new Group();
    private static final Group EDGES_GROUP = new Group();

    public static void main_(String[] args) {

    }

    public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException, IOException {

        setLookAndFeel();
        JFrame frame = new JFrame("Prototype");
        frame.setLayout(new BorderLayout());
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel, BorderLayout.CENTER);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Platform.runLater(() -> {
            Group group = new Group();
            group.getChildren().add(CANVAS);
            CANVAS.getChildren().add(EDGES_GROUP);
            CANVAS.getChildren().add(NODES_GROUP);

            Scene scene = new Scene(group, Screen.getPrimary().getVisualBounds().getWidth() - 100, Screen.getPrimary().getVisualBounds().getHeight() - 100);
            frame.setSize((int) scene.getWidth(), (int) scene.getHeight());
            frame.setLocationRelativeTo(null);

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

    private static void updateScene() {

        Group pointsGroup = new Group();
        NODES_GROUP.getChildren().add(pointsGroup);
        Random rnd = new Random();
        int n = 4;

        if (true) {
            CentripetalCatmullRomCurve curve = new CentripetalCatmullRomCurve();
            for (int i = 0; i < n; i++) {
                double a = 2d * 3.1415d / (double) n;
                double r = 100d;
                //curve.appendPoint(new Vector3D((float) (rnd.nextDouble() * r * Math.cos((double) i * a)), (float) (rnd.nextDouble() * r * Math.sin((double) i * a)), 0));
                curve.appendPoint(new Vector3D((float) (r * Math.cos((double) i * a)), (float) (r * Math.sin((double) i * a)), 0));
            }

            Group curvePathGroup = new Group();
            NODES_GROUP.getChildren().add(curvePathGroup);
            for (Vector3D p : curve.getPoints()) {
                Circle c = new Circle(3);
                c.setUserData(p);
                c.setFill(new Color(0, 0, 1, .5));
                c.setTranslateX(p.x);
                c.setTranslateY(p.y);
                pointsGroup.getChildren().add(c);
                c.toFront();

                c.addEventFilter(MouseEvent.MOUSE_PRESSED, NODE_GESTURES.getOnMousePressedEventHandler());
                c.addEventFilter(MouseEvent.MOUSE_DRAGGED, NODE_GESTURES.getOnMouseDraggedEventHandler());
                c.addEventFilter(MouseEvent.MOUSE_DRAGGED, (MouseEvent event) -> {

                    Node node = (Node) event.getSource();
                    Vector3D lp = (Vector3D) node.getUserData();
                    lp.set((float) node.getTranslateX(), (float) node.getTranslateY(), 0);
                    curvePathGroup.getChildren().clear();

                    int steps = 10;
                    boolean b = true;
                    double length = 4;
                    Vector3D pp = null;
                    //curve.setClosed(true);
                    //for (Vector3D pn:curve.generatePathPointsBySteps(steps)) {
                    for (Vector3D pn : curve.generatePathPointsByLength(length)) {

                        if (b) {
                            curvePathGroup.getChildren().add(new Circle(pn.x, pn.y, 1d, Color.LIGHTGRAY));
                        } else {
                            curvePathGroup.getChildren().add(new Circle(pn.x, pn.y, 1d, Color.GRAY));
                        }
                        b = !b;

                        if (pp != null) {
                            Line l = new Line(pp.x, pp.y, pn.x, pn.y);
                            l.setStroke(Color.BLACK);
                            l.setStrokeWidth(.2f);
                            curvePathGroup.getChildren().add(l);
                            l.toBack();
                        }

                        pp = pn;
                    }
                    curvePathGroup.toBack();

                    event.consume();
                });

            }
        }
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
