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

    public static ArrayList<PackerData> packPlayerClusters(ArrayList<ArrayList<Player>> clusters, boolean updatePlayers) {
        
        int cores = Runtime.getRuntime().availableProcessors();
        
        return packPlayerClusters(clusters, updatePlayers, cores);
    }
    
    public static ArrayList<PackerData> packPlayerClusters(ArrayList<ArrayList<Player>> clusters, boolean updatePlayers, int threadsCount) {

        long time = Calendar.getInstance().getTimeInMillis();

        ArrayList<PackerData> result = new ArrayList<>();

        int lThreadsCount = threadsCount;

        Log.debug("Packing object clusters [" + clusters.size() + "]...");

        AtomicInteger ai = new AtomicInteger(0);
        Thread t[] = new Thread[lThreadsCount];
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
        for (Thread ti : t) {
            try {
                ti.join();
            } catch (InterruptedException ex) {
                Log.error(ex);
            }
        }

        Log.debug("Packer has been finished in " + Log.formatInterval(Calendar.getInstance().getTimeInMillis() - time));

        return result;
    }

    public static ArrayList<PackerData> packPlayers(ArrayList<Player> players, boolean updatePlayers) {

        ArrayList<PackerData> data = new ArrayList<>();

        for (Player p : players) {
            PackerData pd = new PackerData();
            pd.previousPosition = new CurvePoint(p.getPosition());
            pd.nextPosition = new CurvePoint(p.getNextPosition());
            pd.radius = p.getRadius();
            pd.maxStep = p.getMaxStep();
            pd.immovable = false;
            //pd.immovable = pd.previousPosition.equals(pd.nextPosition);
            //pd.immovable = pd.previousPosition.distance(pd.nextPosition)<.1d;
            /*switch (p.getName()) {
                case "P3":
                    pd.immovable = true;
                    break;
            }*/
            data.add(pd);
        }

        final int dimension = 2;

        Calcfc calcfc = (int n, int m, double[] x, double[] c) -> {
            int cCounter = 0;

            double dSum = 0;
            for (int i = 0; i < data.size(); i++) {
                PackerData pd = data.get(i);
                dSum += Math.sqrt(Math.pow(pd.nextPosition.x - x[i * dimension + 0], 2d) + Math.pow(pd.nextPosition.y - x[i * dimension + 1], 2d));
                //dSum += pd.radius + Math.sqrt(Math.pow(pd.nextPosition.x - x[i * dimension + 0], 2d) + Math.pow(pd.nextPosition.y - x[i * dimension + 1], 2d));

                //c[cCounter++] = Math.sqrt(Math.pow(pd.previousPosition.x - pd.nextPosition.x, 2d) + Math.pow(pd.previousPosition.y - pd.nextPosition.y, 2d)) - Math.sqrt(Math.pow(pd.previousPosition.x - x[i * dimension], 2d) + Math.pow(pd.previousPosition.y - x[i * dimension + 1], 2d));
                c[cCounter] = Math.min(pd.maxStep, Math.sqrt(Math.pow(pd.previousPosition.x - pd.nextPosition.x, 2d) + Math.pow(pd.previousPosition.y - pd.nextPosition.y, 2d))) - Math.sqrt(Math.pow(pd.previousPosition.x - x[i * dimension + 0], 2d) + Math.pow(pd.previousPosition.y - x[i * dimension + 1], 2d));
                //c[cCounter] = Math.max(Math.min(maxSpeed, c[cCounter]), minSpeed);
                //c[cCounter] = pd.radius - c[cCounter];
                cCounter++;

                if (pd.immovable) {
                    x[i * dimension + 0] = pd.nextPosition.x;
                    x[i * dimension + 1] = pd.nextPosition.y;
                }
            }
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

        double rhobeg = 0.5;
        double rhoend = 1.0e-6;
        int iprint = 0;
        int maxfun = 3500;
        //maxfun = 1000;
        //maxfun = 500;

        if (data.size() > 1) {

            CobylaExitStatus result = Cobyla.FindMinimum(calcfc, x.length, (int) combinations(data.size(), 2) + data.size(), x, rhobeg, rhoend, iprint, maxfun);
            //System.out.println(result.equals(CobylaExitStatus.Normal));
        } else {
            CobylaExitStatus result = Cobyla.FindMinimum(calcfc, x.length, (int) data.size(), x, rhobeg, rhoend, iprint, maxfun);
        }
        if (updatePlayers) {
            for (int i = 0; i < players.size(); i++) {
                PackerData pd = data.get(i);
                Player player = players.get(i);
                /*player.getPosition().t = pd.nextPosition.t;
            player.getPosition().x = x[i*dimension+0];
            player.getPosition().y = x[i*dimension+1];
            player.update();*/

                player.doOneStep(new CurvePoint(new Vector3D(x[i * dimension + 0], x[i * dimension + 1], 0d), pd.nextPosition.t));
                /*
                double d = Vector3D.distance(pd.previousPosition, player.getPosition());
                if (d>player.getMaxStep()+.01d) {
                    System.out.println(player.getMaxStep()+" "+d);
                }*/
            }
        }

        return data;
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
