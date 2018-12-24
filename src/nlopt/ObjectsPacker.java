package nlopt;

import hoe.Log;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;
import physics.Vector3D;
import prototype.CurvePoint;
import prototype.Player;

public class ObjectsPacker {

    private static final double RHO_BEGIN = 0.5;
    private static final double RHO_END = 1.0e-3;
    private static final int PRINT_ITERATION_RESULTS = 0;
    private static final int MAX_ITERATIONS = 3500;

    private static BigInteger factorial(int n) {
        BigInteger ret = BigInteger.ONE;
        for (int i = 1; i <= n; ++i) {
            ret = ret.multiply(BigInteger.valueOf(i));
        }
        return ret;
    }

    public static long combinations(int n, int k) {
        BigInteger factorialN = factorial(n);
        BigInteger factorialK = factorial(k);
        BigInteger factorialNMinusK = factorial(n - k);
        return factorialN.divide(factorialK.multiply(factorialNMinusK)).longValue();
    }

    public static ArrayList<PackerData> packPlayerClusters(ArrayList<ArrayList<Player>> clusters, boolean updatePlayers, Runnable callback) {

        int cores = Runtime.getRuntime().availableProcessors();

        return packPlayerClusters(clusters, updatePlayers, cores, callback);
    }

    public static ArrayList<PackerData> packPlayerClusters(ArrayList<ArrayList<Player>> clusters, boolean updatePlayers, int threadsCount, Runnable callback) {

        long time = Calendar.getInstance().getTimeInMillis();

        ArrayList<PackerData> result = new ArrayList<>();

        int lThreadsCount = threadsCount;

        Log.debug("Packing object clusters [" + clusters.size() + "]...");

        AtomicInteger ai = new AtomicInteger(0);
        Thread t[] = new Thread[lThreadsCount];
        AtomicInteger counter = new AtomicInteger(lThreadsCount);
        for (int i = 0; i < lThreadsCount; i++) {
            t[i] = new Thread(() -> {
                while (true) {
                    int index = ai.getAndAdd(1);

                    int size;
                    synchronized (clusters) {
                        size = clusters.size();
                    }

                    ArrayList<Player> cluster;
                    synchronized (clusters) {
                        if (index >= size) {
                            Log.debug(Thread.currentThread().getName() + " has been finished.");
                            if (callback != null && counter.addAndGet(-1) == 0) {
                                callback.run();
                            }
                            return;
                        }
                        cluster = clusters.get(index);
                    }

                    Log.debug(Thread.currentThread().getName() + " is solving cluster " + (index + 1) + "/" + clusters.size() + " [size:" + cluster.size() + "]...");

                    ArrayList<PackerData> data = ObjectsPacker.packPlayers(cluster, true);
                    synchronized (result) {
                        result.addAll(data);
                    }
                }
            });
            t[i].start();
        }

        Log.debug("Packer has been finished in " + Log.formatInterval(Calendar.getInstance().getTimeInMillis() - time));

        return result;
    }

    public static ArrayList<PackerData> packPlayers(ArrayList<Player> players, boolean updatePlayers) {

        ArrayList<PackerData> data = new ArrayList<>();

        for (Player p : players) {
            PackerData pd = new PackerData();
            pd.previousPosition = new CurvePoint(p.getPosition());
            pd.nextPosition = new CurvePoint(p.getNextPositionOnPath());
            pd.orientation = p.getOrientation();
            pd.radius = p.getRadius();
            pd.maxStep = p.getMaxStep();
            pd.step = p.step;
            pd.immovable = false;
            data.add(pd);
        }

        final int dimension = 2;

        Calcfc calcfc = (int n, int m, double[] x, double[] c) -> {
            int cCounter = 0;

            double dSum = 0;
            for (int i = 0; i < data.size(); i++) {
                Vector3D currentPosition = new Vector3D(x[i * dimension + 0],x[i * dimension + 1],0);
                PackerData pd = data.get(i);
                //dSum += Math.sqrt(Math.pow(pd.nextPosition.x - currentPosition.x, 2d) + Math.pow(pd.nextPosition.y - currentPosition.y, 2d));
                double sum = Math.sqrt(Math.pow(pd.nextPosition.x - currentPosition.x, 2d) + Math.pow(pd.nextPosition.y - currentPosition.y, 2d));

                //double dOrientation = Math.abs(Player.calculateOrientationDifference(Player.calculateOrientation(currentPosition, pd.nextPosition),pd.orientation));
                /*
                double dOrientation = Math.abs(Player.calculateOrientationDifference(Player.calculateOrientation(pd.previousPosition, currentPosition),pd.orientation));
                //dSum+=dOrientation*Math.PI/180d;
                
//                System.out.println(dOrientation);
                
                
                
                //if (dOrientation>90) maxStep/=2d;
                maxStep *= 1d-(dOrientation/180d)*1d;
                //maxStep *= (dOrientation/180d)*9d;
//                dSum+=maxStep*1d-(dOrientation/180d)*.1d;
                //dSum+=maxStep;
                //dSum+=maxStep/pd.maxStep;
                //dSum+=pd.maxStep/maxStep;
                maxStep = Math.min(pd.maxStep, maxStep);
                //maxStep = Math.max(pd.maxStep, maxStep);
                
                //dSum+=sum*(dOrientation/180d)*maxStep/pd.maxStep;
                
                //System.out.println(pd.maxStep+" "+maxStep);
                */
                //double maxStep = Player.calculateStepByOrientation(pd.previousPosition, currentPosition, pd.maxStep, pd.orientation);
                double maxStep = pd.step;
                maxStep = Math.min(pd.maxStep, maxStep);
//System.out.println(maxStep+" "+pd.maxStep);
                c[cCounter] = Math.min(maxStep, Math.sqrt(Math.pow(pd.previousPosition.x - pd.nextPosition.x, 2d) + Math.pow(pd.previousPosition.y - pd.nextPosition.y, 2d))) - Math.sqrt(Math.pow(pd.previousPosition.x - currentPosition.x, 2d) + Math.pow(pd.previousPosition.y - currentPosition.y, 2d));
                cCounter++;

                if (pd.immovable) {
                    x[i * dimension + 0] = pd.nextPosition.x;
                    x[i * dimension + 1] = pd.nextPosition.y;
                }
            }
//            System.out.println("----");
            if (data.size() > 1) {
                for (int i = 0; i < data.size() - 1; i++) {
                    PackerData pdi = data.get(i);
                    for (int j = i + 1; j < data.size(); j++) {
                        PackerData pdj = data.get(j);

                        if (pdi.immovable && pdj.immovable) {
                            cCounter++;
                            continue;
                        }

                        c[cCounter++] = Math.sqrt(Math.pow(x[i * dimension] - x[j * dimension], 2d) + Math.pow(x[i * dimension + 1] - x[j * dimension + 1], 2d)) - pdi.radius - pdj.radius;
                    }
                }
            }

            return dSum;
        };

        int xn = data.size() * dimension;
        double[] x = new double[xn];
        for (int i = 0; i < data.size(); i++) {
            PackerData pd = data.get(i);
            x[i * dimension + 0] = pd.nextPosition.x;
            x[i * dimension + 1] = pd.nextPosition.y;
        }

        if (data.size() > 1) {
            CobylaExitStatus result = Cobyla.FindMinimum(calcfc, x.length, (int) combinations(data.size(), 2) + data.size(), x, RHO_BEGIN, RHO_END, PRINT_ITERATION_RESULTS, MAX_ITERATIONS);
        } else {
            CobylaExitStatus result = Cobyla.FindMinimum(calcfc, x.length, (int) data.size(), x, RHO_BEGIN, RHO_END, PRINT_ITERATION_RESULTS, MAX_ITERATIONS);
        }
        if (updatePlayers) {
            for (int i = 0; i < players.size(); i++) {
                PackerData pd = data.get(i);
                Player player = players.get(i);

                //player.doOneStep(new CurvePoint(new Vector3D(x[i * dimension + 0], x[i * dimension + 1], 0d), pd.nextPosition.t));
                player.setNextPosition(new CurvePoint(new Vector3D(x[i * dimension + 0], x[i * dimension + 1], 0d), pd.nextPosition.t));
                //player.doOneStep(0);
                //player.doOneStep(.5);
            }
        }

        return data;
    }

    public static ArrayList<PackerData> packPlayers2(ArrayList<Player> players, boolean updatePlayers) {

        int cores = Runtime.getRuntime().availableProcessors();

        ArrayList<PackerData> data = new ArrayList<>();

        for (Player p : players) {
            PackerData pd = new PackerData();
            pd.previousPosition = new CurvePoint(p.getPosition());
            pd.nextPosition = new CurvePoint(p.getNextPositionOnPath());
            pd.radius = p.getRadius();
            pd.maxStep = p.getMaxStep();
            pd.immovable = false;
            data.add(pd);
        }

        final int dimension = 2;

        Calcfc calcfc = (int n, int m, double[] x, double[] c) -> {
            int cCounter = 0;

            double dSum = 0;
            for (int i = 0; i < data.size(); i++) {
                PackerData pd = data.get(i);
                dSum += Math.sqrt(Math.pow(pd.nextPosition.x - x[i * dimension + 0], 2d) + Math.pow(pd.nextPosition.y - x[i * dimension + 1], 2d));

                c[cCounter] = Math.min(pd.maxStep, Math.sqrt(Math.pow(pd.previousPosition.x - pd.nextPosition.x, 2d) + Math.pow(pd.previousPosition.y - pd.nextPosition.y, 2d))) - Math.sqrt(Math.pow(pd.previousPosition.x - x[i * dimension + 0], 2d) + Math.pow(pd.previousPosition.y - x[i * dimension + 1], 2d));
                cCounter++;

                if (pd.immovable) {
                    x[i * dimension + 0] = pd.nextPosition.x;
                    x[i * dimension + 1] = pd.nextPosition.y;
                }
            }

            // Checking collisions.
            if (data.size() > 1) {

                AtomicInteger ai = new AtomicInteger();
                Thread t[] = new Thread[cores];

                //for (int i = 0; i < data.size() - 1; i++) {
                for (int ti = 0; ti < cores; ti++) {

                    t[ti] = new Thread(() -> {

                        while (true) {

                            int i = ai.getAndAdd(1);

                            if (i >= data.size() - 1) {
                                //System.out.println(Thread.currentThread().getName() + " finished.");
                                return;
                            }

                            int cCounter2 = counter(i, data.size());

                            //System.out.println(Thread.currentThread().getName() + " i=" + i);
                            PackerData pdi = data.get(i);
                            for (int j = i + 1; j < data.size(); j++) {
                                PackerData pdj = data.get(j);

                                if (pdi.immovable && pdj.immovable) {
                                    cCounter2++;
                                    continue;
                                }

                                c[cCounter2++] = Math.sqrt(Math.pow(x[i * dimension] - x[j * dimension], 2d) + Math.pow(x[i * dimension + 1] - x[j * dimension + 1], 2d)) - pdi.radius - pdj.radius;
                            }
                        }
                    });

                    t[ti].start();
                }

                for (Thread tt : t) {
                    try {
                        tt.join();
                    } catch (InterruptedException ex) {

                    }
                }

                //System.out.println("-----------");
            }

            return dSum;
        };

        int xn = data.size() * dimension;
        double[] x = new double[xn];
        for (int i = 0; i < data.size(); i++) {
            PackerData pd = data.get(i);
            x[i * dimension + 0] = pd.nextPosition.x;
            x[i * dimension + 1] = pd.nextPosition.y;
        }

        int nc = data.size();
        nc += (data.size() > 1 ? combinations(data.size(), 2) : 0);
        System.out.println("nc=" + nc);

        CobylaExitStatus result = Cobyla.FindMinimum(calcfc, x.length, nc, x, RHO_BEGIN, RHO_END, PRINT_ITERATION_RESULTS, MAX_ITERATIONS);

        if (updatePlayers) {
            for (int i = 0; i < players.size(); i++) {
                PackerData pd = data.get(i);
                Player player = players.get(i);

                //player.doOneStep(new CurvePoint(new Vector3D(x[i * dimension + 0], x[i * dimension + 1], 0d), pd.nextPosition.t));
                player.setNextPosition(new CurvePoint(new Vector3D(x[i * dimension + 0], x[i * dimension + 1], 0d), pd.nextPosition.t));
                player.doOneStep(1,true);
            }
        }

        return data;
    }

    private static int counter(int i, int n) {
        if (i == 0) {
            return 0;
        }

        int r = 0;

        for (int j = 0; j < i; j++) {
            r += n - j - 1;
        }

        return r;
    }

    public static ArrayList<ArrayList<Player>> clusterize(ArrayList<Player> players) {

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

        return clusters;
    }

}
