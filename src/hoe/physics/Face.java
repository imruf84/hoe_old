package hoe.physics;

public class Face extends Geometry {

    public Face(int i0, int i1, int i2) throws ArrayLengthException {
        super(new int[]{i0, i1, i2});
    }

    @Override
    protected int arrayMinLength() {
        return 3;
    }

    @Override
    protected String generateId() {
        return indexes[0] + ID_SEPARATOR + indexes[1] + ID_SEPARATOR + indexes[2];
    }
    
    public int getIndex0() {
        return indexes[0];
    }
    
    public int getIndex1() {
        return indexes[1];
    }
    
    public int getIndex2() {
        return indexes[2];
    }
    
    public static Vector3D calcNormal(Vector3D A, Vector3D B, Vector3D C) {
        Vector3D n = Vector3D.cross(Vector3D.subtract(B, A), Vector3D.subtract(C, A));
        n.normalize();
        return n;
    }
    
    public static Vector3D project(Vector3D A, Vector3D B, Vector3D C, Vector3D P) {
        return project(A, calcNormal(A, B, C), P);
    }
    
    public static Vector3D project(Vector3D A, Vector3D N, Vector3D P) {
        return Vector3D.subtract(P,Vector3D.scale(N,Vector3D.subtract(P, A).dot(N)));
    }
    
    public static double area(Vector3D A, Vector3D B, Vector3D C) {
        return area(Vector3D.subtract(A, B).length(),Vector3D.subtract(A, C).length(),Vector3D.subtract(B, C).length());
    }
    
    public static double area(double a, double b, double c) {
        double s = (a+b+c)/2d;
        return Math.sqrt(s*(s-a)*(s-b)*(s-c));
    }
}
