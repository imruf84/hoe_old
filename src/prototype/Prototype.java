package prototype;

import hoe.Log;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static final ArrayList<Player> players = new ArrayList<Player>();
    private static Thread thread = null;

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
                    /*for (int i = 0; i < players.size()-1; i++) {
                        for (int j = i+1; j < players.size(); j++) {
                            System.out.println(i+","+j);
                        }
                    }*/

                    ArrayList<ArrayList<Player>> clusters = new ArrayList<>();

                    for (Player p : players) {
                        ArrayList<Player> cluster = new ArrayList<>();
                        cluster.add(p);
                        clusters.add(cluster);
                    }

                    for (int i = 0; i < clusters.size() - 1; i++) {
                        ArrayList<Player> ci = clusters.get(i);
                        boolean b = false;
                        for (int j = i + 1; j < clusters.size(); j++) {
                            ArrayList<Player> cj = clusters.get(j);
                            for (Player pi : ci) {
                                for (Player pj : cj) {
                                    if (pi.getPosition().distance(pj.getPosition()) <= pi.getRadius() + pj.getRadius() + pi.getMaxStep() + pj.getMaxStep()) {
                                        ci.addAll(cj);
                                        clusters.remove(j);
                                        b = true;
                                        break;
                                    }
                                }
                                if (b) {
                                    break;
                                }
                            }
                            if (b) {
                                i--;
                                break;
                            }
                        }
                    }

                    for (ArrayList<Player> cluster2 : clusters) {
                        Color c = VPlayer.getRandomColor();
                        for (Player player2 : cluster2) {
                            ((VPlayer) player2).setStrokeColor(c);
                        }
                        
                        ObjectsPacker.packPlayers(cluster2, true);
                    }
                    
//                    System.out.println("----");

                    //ObjectsPacker.packPlayers(players, true);
                    return;
                }

                if (e.getCharacter().equals("p")) {

                    if (thread != null) {
                        stopThread();
                        return;
                    }

                    final long timeInterval = 100;
                    Runnable runnable = () -> {
                        while (thread != null) {
                            ObjectsPacker.packPlayers(players, true);
                            try {
                                Thread.sleep(timeInterval);
                            } catch (InterruptedException ex) {
                                thread = null;
                            }
                        }
                    };
                    thread = new Thread(runnable);
                    thread.start();

                    return;
                }

                if (e.getCharacter().equals("r")) {
                    stopThread();

                    PLAYER_NODES_GROUP.getChildren().clear();
                    players.clear();
                    updateScene();
                }
            }
            );

            updateScene();

            SceneGestures sceneGestures = new SceneGestures(CANVAS);

            scene.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
            scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
            scene.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

            CANVAS.setTranslateX(
                    (fxPanel.getWidth() - CANVAS.getBoundsInLocal().getWidth()) / 2);
            CANVAS.setTranslateY(
                    (fxPanel.getHeight() - CANVAS.getBoundsInLocal().getHeight()) / 2);

            // TEST
            CANVAS.setScale(
                    4);
            CANVAS.setTranslateX(CANVAS.getTranslateX() + 300);
            CANVAS.setTranslateY(CANVAS.getTranslateY() + 300);

            fxPanel.setScene(scene);

            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        }
        );

    }

    public static void stopThread() {
        if (thread != null) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException ex) {
                Log.error(ex);
            }
        }
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

    private static int rnd(int a, int b) {
        if (a == b) {
            return a;
        }
        return ThreadLocalRandom.current().nextInt(Math.min(a, b), Math.max(a, b));
    }

    private static void updateScene() {

        Line xAxis = new Line(0, 0, 100, 0);
        xAxis.setStroke(Color.RED);
        Line yAxis = new Line(0, 0, 0, 100);
        yAxis.setStroke(Color.BLUE);
        //EDGES_GROUP.getChildren().addAll(xAxis, yAxis);

        int rangePlayer[] = {-150, 150, 90, 150};
        int rangeNavPoint[][] = {
            //{-100, 100, 0, 40},
            //{-130, 130, -60, -50},
            {-290, 290, -145, -140},};

        int np = 30;
        int nn[] = {1, rangeNavPoint.length + 1};
        int maxStep[] = {2, 4};
        for (int i = 0; i < np; i++) {
            VPlayer player = new VPlayer("P" + i, new Vector3D(rnd(rangePlayer[0], rangePlayer[1]), rnd(rangePlayer[2], rangePlayer[3]), 0), rnd((int) VPlayer.SHAPE_SIZE / 2, (int) VPlayer.SHAPE_SIZE), rnd(maxStep[0], maxStep[1]), NODE_GESTURES);
            int n = rnd(nn[0], nn[1]);
            for (int j = 0; j < n; j++) {
                int k = j + 1 == n ? rangeNavPoint.length - 1 : j;
                player.addNavigationPoint(new Vector3D(ThreadLocalRandom.current().nextInt(rangeNavPoint[k][0], rangeNavPoint[k][1]), ThreadLocalRandom.current().nextInt(rangeNavPoint[k][2], rangeNavPoint[k][3]), 0));
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
