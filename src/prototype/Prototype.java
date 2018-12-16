package prototype;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;
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
import nlopt.ObjectsPacker;
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
                    ObjectsPacker.packPlayers(players);
                    /*for (Player p : players) {
                        //p.doOneStep();
                        p.update();
                    }*/
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
        //EDGES_GROUP.getChildren().addAll(xAxis, yAxis);
/*
        VPlayer player = new VPlayer("P1", new Vector3D(-50, 0, 0), VPlayer.SHAPE_SIZE, NODE_GESTURES);
        player.addNavigationPoint(new Vector3D(10, -30, 0));
        player.addNavigationPoint(new Vector3D(100, 0, 0));
        player.addNavigationPoint(new Vector3D(100, 100, 0));
        player.addNavigationPoint(new Vector3D(30, 10, 0));
        PLAYER_NODES_GROUP.getChildren().add(player.getContainer());
        players.add(player);
        
        player = new VPlayer("P2", new Vector3D(-20, 10, 0), VPlayer.SHAPE_SIZE, NODE_GESTURES);
        player.addNavigationPoint(new Vector3D(20, -50, 0));
        player.addNavigationPoint(new Vector3D(80, 50, 0));
        player.addNavigationPoint(new Vector3D(80, 100, 0));
        PLAYER_NODES_GROUP.getChildren().add(player.getContainer());
        players.add(player);
        
        player = new VPlayer("P3", new Vector3D(0, -50, 0), VPlayer.SHAPE_SIZE, NODE_GESTURES);
        PLAYER_NODES_GROUP.getChildren().add(player.getContainer());
        players.add(player);
        
        player = new VPlayer("P4", new Vector3D(0,70, 20), VPlayer.SHAPE_SIZE, NODE_GESTURES);
        PLAYER_NODES_GROUP.getChildren().add(player.getContainer());
        players.add(player);
        
        player = new VPlayer("P5", new Vector3D(0, 20, -50), VPlayer.SHAPE_SIZE, NODE_GESTURES);
        PLAYER_NODES_GROUP.getChildren().add(player.getContainer());
        players.add(player);
*/

        Random rnd = new Random();
        int range = 100;
        int range2 = 50;
        int np = 10;
        int nn =2;
        for (int i = 0; i < np; i++) {
            VPlayer player = new VPlayer("P"+i, new Vector3D(range/2-rnd.nextInt(range), range/2-rnd.nextInt(range), 0), VPlayer.SHAPE_SIZE/2+rnd.nextInt((int) (VPlayer.SHAPE_SIZE/2)), NODE_GESTURES);
            for (int j =0; j<rnd.nextInt(nn)+1;j++){
                player.addNavigationPoint(new Vector3D(range2/2-rnd.nextInt(range), range2/2-rnd.nextInt(range), 0));
            }
            PLAYER_NODES_GROUP.getChildren().add(player.getContainer());
            players.add(player);
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
