package hoe.physics;

public class Intersection {

    public static Vector3D segmentTriangleIntersection(Vector3D A, Vector3D B, Vector3D C, Vector3D P, Vector3D Q) {
        Vector3D n = Face.calcNormal(A,B,C);
        Vector3D d = Vector3D.subtract(Q, P);
        double nd = n.dot(d);
        if (nd == 0) {
            return null;
        }
        double t = (n.dot(A) - n.dot(P)) / nd;

        if (t < 0 || t > 1) {
            return null;
        }

        d.scale(t);
        Vector3D M = Vector3D.add(P, d);
        
        if (n.dot(Vector3D.cross(Vector3D.subtract(B, A), Vector3D.subtract(M, A))) < 0) {
            return null;
        }

        if (n.dot(Vector3D.cross(Vector3D.subtract(C, B), Vector3D.subtract(M, B))) < 0) {
            return null;
        }

        if (n.dot(Vector3D.cross(Vector3D.subtract(A, C), Vector3D.subtract(M, C))) < 0) {
            return null;
        }

        return M;
    }
    
    public static boolean isInside(Vector3D A, Vector3D B, Vector3D C, Vector3D P) {
        Vector3D n = Face.calcNormal(A,B,C);
        
        return n.dot(Vector3D.subtract(P, A)) <= 0;
    }

}
