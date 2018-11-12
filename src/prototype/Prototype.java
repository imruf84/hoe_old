package prototype;

import Jama.Matrix;
import java.awt.BorderLayout;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javax.swing.JFrame;
import javax.swing.UIManager;
import physics.Vector3D;

public class Prototype {

    private static final PannableCanvas CANVAS = new PannableCanvas();
    private static final NodeGestures NODE_GESTURES = new NodeGestures(CANVAS);
    private static final Group NODES_GROUP = new Group();
    private static final Group EDGES_GROUP = new Group();

    public static void main_(String[] args) {
        //Matrix A = new Matrix(new double[][]{{1., 2.}, {4., 5.}, {7., 8.}});
        Matrix A = Matrix.random(10, 6);
        //Matrix b = new Matrix(new double[][]{{1.},{2.},{3.}});
        Matrix b = Matrix.random(10, 1);
        Matrix x = A.solve(b);
        System.out.println(Arrays.toString(x.getRowPackedCopy()));
        
        Matrix Residual = A.times(x).minus(b);
        double rnorm = Residual.normInf();
        System.out.println(rnorm);
        
        /*System.out.println(Arrays.toString(A.getRowPackedCopy()));
        System.out.println(Arrays.toString(b.getRowPackedCopy()));
        System.out.println(Arrays.toString(Residual.getRowPackedCopy()));*/
        
        int n = 3;
        for (int i = 0; i < 4*n; i++) {
            System.out.println((i<0?i+n:i+n-1)%n);
        }
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
            CANVAS.setTranslateY(CANVAS.getTranslateY() + 200);

            fxPanel.setScene(scene);

            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        });

    }

    private static void updateScene() {

        //NODES_GROUP.getChildren().add(new VPlayer(new Player("player 1"), NODE_GESTURES));
        /*CurvePath cv = new CurvePath(new Vector3D(0, 0, 0), new Vector3D(10, 40, 0), new Vector3D(80, 20, 0));
        NODES_GROUP.getChildren().add(cv.getSegmens());
        cv = new CurvePath(new Vector3D(80, 20, 0), new Vector3D(200, 80, 0), new Vector3D(180, -5, 0));
        NODES_GROUP.getChildren().add(cv.getSegmens());*/
        
        Curve c = new Curve();
        c.addPoint(new Vector3D(0, 0, 0));
        c.addPoint(new Vector3D(10, 50, 0));
        c.addPoint(new Vector3D(100, 10, 0));
        c.addPoint(new Vector3D(100, -10, 0));
        c.addPoint(new Vector3D(70, 0, 0));
        c.updateControlPoints(20);
        NODES_GROUP.getChildren().add(c.getCurve());

        /*for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                HexaCell hc = new HexaCell(x, y);
                HexaCellVisual hcv = new HexaCellVisual(hc, NODE_GESTURES);
                NODES_GROUP.getChildren().add(hcv);
            }
        }*/
 /*
        EdgeVisual ev12 = new EdgeVisual(nv1, nv2);
        EDGES_GROUP.getChildren().add(ev12);
        EDGES_GROUP.toFront();
        EDGES_VISUAL.add(ev12);
         */
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
