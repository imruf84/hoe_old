package hoe.physics;

public class Edge extends Geometry {

    public Edge(int i0, int i1) throws ArrayLengthException {
        super(new int[]{i0, i1});
    }

    @Override
    protected int arrayMinLength() {
        return 2;
    }

    @Override
    protected String generateId() {
        return Math.min(indexes[0], indexes[1]) + ID_SEPARATOR + Math.max(indexes[0], indexes[1]);
    }
    
    public int getIndex0() {
        return indexes[0];
    }
    
    public int getIndex1() {
        return indexes[1];
    }

}
