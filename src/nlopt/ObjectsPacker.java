package nlopt;

import java.util.ArrayList;
import physics.Vector3D;
import prototype.CurvePoint;
import prototype.Player;
import static prototype.Prototype.combinations;

public class ObjectsPacker {

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
                //dSum += tollerance+Math.sqrt(Math.pow(pd.nextPosition.x - x[i * dimension + 0], 2d) + Math.pow(pd.nextPosition.y - x[i * dimension + 1], 2d));
                dSum += pd.radius + Math.sqrt(Math.pow(pd.nextPosition.x - x[i * dimension + 0], 2d) + Math.pow(pd.nextPosition.y - x[i * dimension + 1], 2d));

                //c[cCounter++] = Math.sqrt(Math.pow(pd.previousPosition.x - pd.nextPosition.x, 2d) + Math.pow(pd.previousPosition.y - pd.nextPosition.y, 2d)) - Math.sqrt(Math.pow(pd.previousPosition.x - x[i * dimension], 2d) + Math.pow(pd.previousPosition.y - x[i * dimension + 1], 2d));
                c[cCounter++] = Math.min(pd.maxStep, Math.sqrt(Math.pow(pd.previousPosition.x - pd.nextPosition.x, 2d) + Math.pow(pd.previousPosition.y - pd.nextPosition.y, 2d))) - Math.sqrt(Math.pow(pd.previousPosition.x - x[i * dimension], 2d) + Math.pow(pd.previousPosition.y - x[i * dimension + 1], 2d));
                //c[cCounter] = Math.max(Math.min(maxSpeed, c[cCounter]), minSpeed);
                c[cCounter] = pd.radius - c[cCounter];

                if (pd.immovable) {
                    x[i * dimension + 0] = pd.nextPosition.x;
                    x[i * dimension + 1] = pd.nextPosition.y;
                }
            }

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
        maxfun = 1000;
        //maxfun = 500;
        CobylaExitStatus result = Cobyla.FindMinimum(calcfc, x.length, (int) combinations(data.size(), 2) + data.size(), x, rhobeg, rhoend, iprint, maxfun);
        //System.out.println(result.equals(CobylaExitStatus.Normal));

        if (updatePlayers) {
            for (int i = 0; i < players.size(); i++) {
                PackerData pd = data.get(i);
                Player player = players.get(i);
                /*player.getPosition().t = pd.nextPosition.t;
            player.getPosition().x = x[i*dimension+0];
            player.getPosition().y = x[i*dimension+1];
            player.update();*/

                player.doOneStep(new CurvePoint(new Vector3D(x[i * dimension + 0], x[i * dimension + 1], 0d), pd.nextPosition.t));

                //System.out.println(Vector3D.distance(pd.previousPosition, player.getPosition()));
            }
        }

        //System.out.println("----");
        return data;
    }

}
