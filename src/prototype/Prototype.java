package prototype;

import java.awt.BorderLayout;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
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
    private static final Group PLAYER_NODES_GROUP = new Group();
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
            CANVAS.getChildren().add(PLAYER_NODES_GROUP);

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
        
        Line xAxis = new Line(0, 0, 100, 0);
        xAxis.setStroke(Color.RED);
        Line yAxis = new Line(0, 0, 0, 100);
        yAxis.setStroke(Color.BLUE);
        EDGES_GROUP.getChildren().addAll(xAxis,yAxis);
        
        VPlayer player = new VPlayer("player_1", new Vector3D(50, 0, 0), NODE_GESTURES);
        player.addNavigationPoint(new Vector3D(100, 0, 0));
        player.addNavigationPoint(new Vector3D(100, 100, 0));
        PLAYER_NODES_GROUP.getChildren().add(player.getContainer());
        
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
