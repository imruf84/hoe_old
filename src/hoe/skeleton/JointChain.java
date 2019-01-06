package hoe.skeleton;

import hoe.Log;
import hoe.nonlinear.Calcfc;
import hoe.nonlinear.Cobyla;
import hoe.nonlinear.CobylaExitStatus;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import org.joml.Vector3d;

public class JointChain {

    private final Vector3d offset = new Vector3d();
    private final Vector3d target = new Vector3d();
    private final LinkedList<Joint> joints = new LinkedList<>();

    public JointChain() {
    }

    public JointChain(Vector3d offset) {
        setOffset(offset);
    }

    public Vector3d getOffset() {
        return offset;
    }

    public final void setOffset(Vector3d offset) {
        getOffset().set(offset);
    }

    public Vector3d getTarget() {
        return target;
    }

    public void setTarget(Vector3d target) {
        getTarget().set(target);
    }

    public LinkedList<Joint> getJoints() {
        return joints;
    }

    public Joint getJoint(int index) {
        return getJoints().get(index);
    }

    public Joint appendJoint(Joint joint) {

        if (joint == null) {
            return null;
        }

        if (!isEmpty()) {

            Joint last = getJoints().getLast();

            if (last != null) {
                last.setNext(joint);
                joint.setPrev(last);
            }
        }

        getJoints().add(joint);

        return joint;
    }

    public boolean isEmpty() {
        return getJoints().isEmpty();
    }

    public void updatePositions() {

        if (isEmpty()) {
            return;
        }

        Joint first = getJoints().getFirst();

        if (first == null) {
            return;
        }

        first.updatePosition(0, getOffset());
    }

    public Vector3d getEndPosition() {

        if (getJoints().isEmpty()) {
            return getOffset();
        }

        return getJoints().getLast().getTail();
    }

    public LinkedList<Vector3d> getPositions(boolean basePointIncluded) {

        LinkedList<Vector3d> result = new LinkedList<>();

        if (basePointIncluded) {
            result.add(getOffset());
        }

        getJoints().forEach((j) -> {
            result.add(j.getTail());
        });

        return result;
    }

    public int size() {
        return getJoints().size();
    }

    public void solveTarget() {
        final double rhobeg = 0.5;
        final double rhoend = 1.0e-6;
        final int iprint = 0;
        final int maxfun = 3500;

        Vector3d g = new Vector3d();
        getEndPosition().sub(getOffset(), g).normalize();

        Calcfc calcfc = (int n, int m, double[] x, double[] c) -> {
            /*c[0] = 1.0 - x[0] * x[0] - x[1] * x[1];
            c[1] = x[1] - .5;
            c[2] = -c[1];
            return x[0] * x[1];*/

            for (int i = 0; i < size(); i++) {
                getJoint(i).setAngle(x[i]);
            }

            /*for (int i = 0; i < c.length; i++) {
                c[i]=Math.abs(x[i+1]-x[i]);
            }*/
            updatePositions();
            double sum = 0;
            int ci = 0;
            for (int i = 0; i < size(); i++) {
                //sum += getJoint(i).getTail().dot(g);
                //sum += Math.abs(x[i] - getJoint(i).getAngle());
                /*double diffl = 170;
                double diffh = 0;
                c[2 * i + 0] = calculateBoundsCondition(x[i], 0, diffl, diffh)[0];
                c[2 * i + 1] = calculateBoundsCondition(x[i], 0, diffl, diffh)[1];*/
                
                Joint j = getJoint(i);
                /*double init = (j.getMaxAngle()+j.getMinAngle())/2d;
                double diff = (j.getMaxAngle()-j.getMinAngle())/2d;
                c[2 * i + 0] = calculateBoundsCondition(x[i], init, diff, diff)[0];
                c[2 * i + 1] = calculateBoundsCondition(x[i], init, diff, diff)[1];*/

                /*c[2*i+0]=j.getMaxAngle()-x[i];
                c[2*i+1]=x[i]-j.getMinAngle();*/
                
                c[ci++]=j.getMaxAngle()-x[i];
                c[ci++]=x[i]-j.getMinAngle();
                
                //c[2*i+0]=30-x[i];
                //c[2*i+1]=x[i]-(-30);
            }

            //80<a<100
            //c[0]=x[0]-(90-10);
            //c[1]=(90+10)-x[0];
            /*int diff = 50;
            c[0] = calculateBoundsCondition(x[0], 90, diff, diff)[0];
            c[1] = calculateBoundsCondition(x[0], 90, diff, diff)[1];*/

            //System.out.println(sum);
            //return sum;
            return getEndPosition().distanceSquared(getTarget());
            //return getEndPosition().distance(getTarget())+sum;
            //return calculateSD(x)+getEndPosition().distanceSquared(getTarget());
        };

        double x[] = new double[size()];

        for (int i = 0; i < size(); i++) {
            x[i] = getJoint(i).getAngle();
        }

        /*double[] x = {1.0, 1.0};
        int nx = x.length;*/
        int nc = x.length * 2;
        for (int i = 0; i < 20; i++) {
            CobylaExitStatus result = Cobyla.FindMinimum(calcfc, x.length, nc, x, rhobeg, rhoend, iprint, maxfun);
            //System.out.println(result);
        }
        //Log.printArray(x);
    }
    
    public static double[] calculateBoundsCondition(double value, double mid, double lowerDiff, double upperDiff) {
        return new double[]{value - (mid - lowerDiff), (mid + upperDiff) - value};
    }

    public static double calculateSD(double numArray[]) {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;

        for (double num : numArray) {
            sum += num;
        }

        double mean = sum / length;

        for (double num : numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation / length);
    }

}
