package physics;

public class Collision {

    public static double epsilon = 1d;
    
    public static int triangleTriangleCollision(Vector3D p1, Vector3D p2, Vector3D p3, Vector3D q1, Vector3D q2, Vector3D q3) {
        Vector3D nP = Vector3D.cross(Vector3D.subtract(p2, p1), Vector3D.subtract(p3, p1));
        Vector3D nQ = Vector3D.cross(Vector3D.subtract(q2, q1), Vector3D.subtract(q3, q1));
        nP.normalize();
        nQ.normalize();
        double htp1 = nQ.dot(Vector3D.subtract(p1, q1));
        double htp2 = nQ.dot(Vector3D.subtract(p2, q1));
        double htp3 = nQ.dot(Vector3D.subtract(p3, q1));

        // Nincs ütközés.
        if ((htp1 > epsilon && htp2 > epsilon && htp3 > epsilon) || (htp1 < -epsilon && htp2 < -epsilon && htp3 < -epsilon)) {
            return -1;
        }

        double htq1 = nP.dot(Vector3D.subtract(q1, p1));
        double htq2 = nP.dot(Vector3D.subtract(q2, p1));
        double htq3 = nP.dot(Vector3D.subtract(q3, p1));

        // Nincs ütközés.
        if ((htq1 > epsilon && htq2 > epsilon && htq3 > epsilon) || (htq1 < -epsilon && htq2 < -epsilon && htq3 < -epsilon)) {
            return -1;
        }
        
        
        
        //System.out.println("htp1:"+htp1 + " htp2:" + htp2 + " htp3:" + htp3+" htq1:"+htq1+" htq2:"+htq2+" htq3:"+htq3);
        
        return 0;
    }

    public static boolean triangleSegmentCollision(Vector3D A, Vector3D B, Vector3D C, Vector3D S, Vector3D T, Vector3D result) {
        Vector3D N = Vector3D.cross(Vector3D.subtract(B, A), Vector3D.subtract(C, A));
        N.normalize();
        double k0 = N.dot(A);
        double ks = N.dot(S) - k0;
        double kt = N.dot(T) - k0;
        //System.out.println(k0 + " " + ks + " " + kt);

        // Van metszéspont
        if ((ks < 0 && kt > 0) || (ks > 0 && kt < 0)) {
            Vector3D r = Vector3D.subtract(Vector3D.scale(S, kt), Vector3D.scale(T, ks));
            r.scale(1 / (kt - ks));
            result.set(r);
            return true;
        }

        return false;
    }
}
