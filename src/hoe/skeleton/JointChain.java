package hoe.skeleton;

import hoe.Log;
import hoe.nonlinear.Calcfc;
import hoe.nonlinear.Cobyla;
import hoe.nonlinear.CobylaExitStatus;
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

        Vector3d g = new Vector3d(0, -1, 0).normalize();
        
        Calcfc calcfc = (int n, int m, double[] x, double[] c) -> {
            /*c[0] = 1.0 - x[0] * x[0] - x[1] * x[1];
            c[1] = x[1] - .5;
            c[2] = -c[1];
            return x[0] * x[1];*/
            
            for (int i = 0; i < size(); i++) {
                getJoint(i).setAngle(x[i]);
            }
            
            updatePositions();
            double sum = 0;
            for (int i = 0; i < size(); i++) {
                sum += getJoint(i).getTail().dot(g);
            }
            
            //System.out.println(sum);
            
            //return sum+getEndPosition().distance(getTarget());
            //return sum;
            return getEndPosition().distance(getTarget());
        };
        
        
        double x[] = new double[size()];
        
        for (int i = 0; i < size(); i++) {
            x[i]=getJoint(i).getAngle();
        }
        
        /*double[] x = {1.0, 1.0};
        int nx = x.length;*/
        int nc = 0;
        CobylaExitStatus result = Cobyla.FindMinimum(calcfc, x.length, nc, x, rhobeg, rhoend, iprint, maxfun);
        System.out.println(result);
        Log.printArray(x);
    }

}
