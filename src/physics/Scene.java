package physics;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Scene {

    public double dt = .01d;
    //public double dt = .04d; //25fps
    public BigInteger frames = new BigInteger("0");

    List<Mesh> meshes = new ArrayList<>();

    void update() {

        // Erőhatások.
        for (Mesh m : meshes) {
            for (Point point : m.points) {
                // Gravitáció.
                //point.force.add(new Vector3D(0, 0, -9.8d));
            }
        }

        // Verlet.
        Vector3D temp = new Vector3D();
        for (Mesh m : meshes) {
            for (Point p : m.points) {
                temp.set(p.curPos);
                p.curPos.x += p.curPos.x - p.oldPos.x + p.mass * p.force.x * dt * dt;
                p.curPos.y += p.curPos.y - p.oldPos.y + p.mass * p.force.y * dt * dt;
                p.curPos.z += p.curPos.z - p.oldPos.z + p.mass * p.force.z * dt * dt;
                p.oldPos.set(temp);
                
                // Init the forces container.
                p.force.set(0,0,0);
            }
        }

        LinkedList<CollisionInfo> collisions = new LinkedList<>();
        int collisionIteration = 10;
        int contraintsIterations = 4;
        for (int ci = 0; ci < collisionIteration; ci++) {

            // Kényszerítők.
            for (Mesh m : meshes) {
                for (Contraint c : m.contraints) {
                    for (int i = 0; i < contraintsIterations; i++) {
                        Point p1 = m.points.get(c.index1);
                        Point p2 = m.points.get(c.index2);
                        Vector3D v1 = p1.curPos;
                        Vector3D v2 = p2.curPos;
                        Vector3D delta = Vector3D.subtract(v1, v2);
                        double deltalength = delta.length();
                        // ha nincs tömege a részecskéknek
                        /*
                    double diff = (deltalength - c.restLength) / deltalength;
                    v1.subtract(Vector3D.scale(delta, .5d * diff));
                    v2.add(Vector3D.scale(delta, .5d * diff));
                    
                    // saját megoldás tömeggel rendelkező részecskékre
                    double m1 = p1.mass;
                    double m2 = p2.mass;
                    double m = m1+m2;
                    v1.subtract(Vector3D.scale(delta, m1/m * diff));
                    v2.add(Vector3D.scale(delta, m2/m * diff));*/

                        // jegyzet megoldása részecskékre
                        double invm1 = 1 / p1.mass;
                        double invm2 = 1 / p2.mass;
                        double diff = (deltalength - c.restLength) / (deltalength * (invm1 + invm2));
                        v1.subtract(Vector3D.scale(delta, invm1 * diff));
                        v2.add(Vector3D.scale(delta, invm2 * diff));
                    }
                }
            }

            // Ütközésvizsgálat.
            for (int i = 0; i < meshes.size() - 1; i++) {
                Mesh m1 = meshes.get(i);

                for (Point P : m1.points) {

                    Vector3D ua = P.calculateVelocity();
                    double ma = P.mass;

                    for (int j = 0; j < meshes.size(); j++) {

                        // Nem ütköztetjük a testet önmagával.
                        if (i == j) {
                            continue;
                        }

                        Mesh m2 = meshes.get(j);

                        if (m2.getFaces().isEmpty()) {
                            continue;
                        }

                        // Mozdulatlan testeket nem ütköztetünk egymással.
                        if (m1.getTotalMass() == 0 && m2.getTotalMass() == 0) {
                            continue;
                        }

                        // Ha belső pont, akkor van ütközés.
                        if (m2.isInnerPoint(P)) {

                            Vector3D N = null;
                            // Hit point on face.
                            Vector3D Q = null;
                            Point A = null;
                            Point B = null;
                            Point C = null;

                            // Find closest point (P->Q) on the surface.
                            double dMin = Double.MAX_VALUE;
                            for (Face f : m2.getFaces()) {
                                A = m2.getPoint(f.getIndex0());
                                B = m2.getPoint(f.getIndex1());
                                C = m2.getPoint(f.getIndex2());

                                Vector3D n = Face.calcNormal(A.curPos, B.curPos, C.curPos);
                                Vector3D R = Face.project(A.curPos, n, P.curPos);
                                double d = R.distance(P.curPos);
                                if (d < dMin) {
                                    dMin = d;
                                    Q = R;
                                    N = n;
                                }
                            }

                            if (ci == 0) {
                                
                                // Point's collision response.
                                Vector3D v = Vector3D.subtract(P.curPos, P.oldPos);
                                double vl = v.length();
                                Vector3D vn = Vector3D.scale(N, Vector3D.dot(v, N));
                                Vector3D vp = Vector3D.subtract(v, vn);
                                double friction = .4d;
                                double bounce = .6d;
                                Vector3D vr = Vector3D.subtract(Vector3D.scale(vp, friction), Vector3D.scale(vn, bounce));
                                double vrl = vr.length();
                                //collisions.add(new CollisionInfo(P, vr));
                                //vr.scale(2d);
                                //P.force.add(vr);

                                // Triangle's collision response.
                                /*if (m2.getTotalMass() != 0) {
                                    vr.scale(-1d);
                                    collisions.add(new CollisionInfo(A, vr));
                                    collisions.add(new CollisionInfo(B, vr));
                                    collisions.add(new CollisionInfo(C, vr));
                                }*/

                                
                                Vector3D vA = A.calculateVelocity();
                                Vector3D vB = B.calculateVelocity();
                                Vector3D vC = C.calculateVelocity();
                                double areaABC = Face.area(A.curPos, B.curPos, C.curPos);
                                double tA = Face.area(Q, B.curPos, C.curPos) / areaABC;
                                double tB = Face.area(Q, A.curPos, C.curPos) / areaABC;
                                double tC = Face.area(Q, A.curPos, B.curPos) / areaABC;
                                vA.scale(tA);
                                vB.scale(tB);
                                vC.scale(tC);
                                
                                Vector3D ub = Vector3D.add(vA, vB, vC);
                                double mb = A.mass * tA + B.mass * tB + C.mass * tC;
                                
                                double CRA = 1.0d;
                                double CRB = .2d;
                                
                                //mb*=1000000;
                                //ub.set(0,0,0);
                                Vector3D va = Vector3D.scale(Vector3D.add(Vector3D.scale(ua, ma),Vector3D.scale(ub, mb),Vector3D.scale(Vector3D.subtract(ub, ua), mb*CRA)), 1d/(ma+mb));
                                va = vr;
                                Vector3D vb = Vector3D.scale(Vector3D.add(Vector3D.scale(ua, ma),Vector3D.scale(ub, mb),Vector3D.scale(Vector3D.subtract(ua, ub), ma*CRB)), 1d/(ma+mb));
                                Vector3D impulse = Vector3D.scale((Vector3D.scale(N,Vector3D.dot(Vector3D.subtract(ua, ub),N))),(ma*mb)/(ma+mb));
                                double iLength = impulse.length();
                                
                                //collisions.add(new CollisionInfo(P, va));
                                if (iLength != 0) {
                                    //va.scale(impulse.length());
                                }
                                collisions.add(new CollisionInfo(P, va));
                                //P.force.add(va);
                                
                                if (m2.getTotalMass() != 0) {
                                    // Apply the new velocity to the whole mesh.
                                    double dSum = 0;
                                    List<PointDoublePair> t = new LinkedList<>();
                                    for (Point R : m2.points) {
                                        double d = Vector3D.distance(R.curPos, Q);
                                        d *= d;
                                        dSum += d;
                                        t.add(new PointDoublePair(R, d));
                                    }
                                    
                                    int n = t.size();
                                    for (PointDoublePair dP : t) {
                                        double d = dP.d;
                                        d = (dSum-d)/((n-1)*dSum);
                                        collisions.add(new CollisionInfo(dP.P, Vector3D.scale(vb,d)));
                                    }
                                    
                                    t.clear();
                                }
                            }

                            P.curPos.set(Q);
                            P.oldPos.set(Q);
                        }

                    }
                }

            }

        }

        // Ütközésválaszok.
        for (CollisionInfo c : collisions) {
            c.apply();
        }

        collisions.clear();

        frames = frames.add(BigInteger.ONE);
        //System.out.println("frame:"+frames.toString());

    }
}
