package physics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Mesh {

    public List<Point> points = new ArrayList<>();
    public HashMap<String, Face> faces = new HashMap<>();
    public HashMap<String, Edge> edges = new HashMap<>();
    public List<Contraint> contraints = new ArrayList<>();
    public double totalMass = 0;

    public static Mesh createPlane(double size, Transform t) throws ArrayLengthException {
        Mesh mesh = new Mesh();
        
        mesh.points.add(new Point(-size, -size, 0, 0));
        mesh.points.add(new Point(-size, size, 0, 0));
        mesh.points.add(new Point(size, size, 0, 0));
        mesh.points.add(new Point(size, -size, 0, 0));
        mesh.addFace(new Face(0, 2, 1));
        mesh.addFace(new Face(3, 2, 0));

        if (null != t) {
            mesh.transform(t);
        }
        
        mesh.updateContraints();
        mesh.calculateTotalMass();

        return mesh;
    }
    
    public static Mesh createTetrahedron(double size, Transform t) throws ArrayLengthException {
        Mesh mesh = new Mesh();
        mesh.points.add(new Point(0, 0, 0));
        mesh.points.add(new Point(size, 0, 0));
        mesh.points.add(new Point(0, size, 0));
        mesh.points.add(new Point(0, 0, size));
        mesh.addFace(new Face(0, 2, 1));
        mesh.addFace(new Face(0, 1, 3));
        mesh.addFace(new Face(1, 2, 3));
        mesh.addFace(new Face(0, 3, 2));
        mesh.contraints.add(new Contraint(0, 1));
        mesh.contraints.add(new Contraint(1, 2));
        mesh.contraints.add(new Contraint(2, 0));
        mesh.contraints.add(new Contraint(0, 3));
        mesh.contraints.add(new Contraint(1, 3));
        mesh.contraints.add(new Contraint(2, 3));
        
        if (null != t) {
            mesh.transform(t);
        }
        
        mesh.updateContraints();
        mesh.calculateTotalMass();

        return mesh;
    }
    
    public static Mesh createCube(double s, Transform t) throws ArrayLengthException {
        Mesh mesh = new Mesh();
        mesh.points.add(new Point(-s/2, -s/2, -s/2));
        mesh.points.add(new Point(s/2, -s/2, -s/2));
        mesh.points.add(new Point(s/2, s/2, -s/2));
        mesh.points.add(new Point(-s/2, s/2, -s/2));
        mesh.points.add(new Point(-s/2, -s/2, s/2));
        mesh.points.add(new Point(s/2, -s/2, s/2));
        mesh.points.add(new Point(s/2, s/2, s/2));
        mesh.points.add(new Point(-s/2, s/2, s/2));
        
        mesh.addFace(new Face(0, 2, 1));
        mesh.addFace(new Face(0, 3, 2));
        mesh.addFace(new Face(4, 5, 6));
        mesh.addFace(new Face(4, 6, 7));
        mesh.addFace(new Face(0, 1, 4));
        mesh.addFace(new Face(4, 1, 5));
        mesh.addFace(new Face(1, 6, 5));
        mesh.addFace(new Face(1, 2, 6));
        mesh.addFace(new Face(2, 7, 6));
        mesh.addFace(new Face(2, 3, 7));
        mesh.addFace(new Face(3, 4, 7));
        mesh.addFace(new Face(3, 0, 4));
        
        mesh.contraints.add(new Contraint(0, 1));
        mesh.contraints.add(new Contraint(1, 2));
        mesh.contraints.add(new Contraint(2, 3));
        mesh.contraints.add(new Contraint(3, 0));
        mesh.contraints.add(new Contraint(0, 2));
        mesh.contraints.add(new Contraint(1, 3));
        
        mesh.contraints.add(new Contraint(4, 5));
        mesh.contraints.add(new Contraint(5, 6));
        mesh.contraints.add(new Contraint(6, 7));
        mesh.contraints.add(new Contraint(7, 4));
        mesh.contraints.add(new Contraint(5, 7));
        mesh.contraints.add(new Contraint(4, 6));
        
        mesh.contraints.add(new Contraint(0, 4));
        mesh.contraints.add(new Contraint(1, 5));
        mesh.contraints.add(new Contraint(2, 6));
        mesh.contraints.add(new Contraint(3, 7));
        
        mesh.contraints.add(new Contraint(0, 5));
        mesh.contraints.add(new Contraint(1, 4));
        mesh.contraints.add(new Contraint(1, 6));
        mesh.contraints.add(new Contraint(2, 5));
        mesh.contraints.add(new Contraint(2, 7));
        mesh.contraints.add(new Contraint(3, 6));
        mesh.contraints.add(new Contraint(0, 7));
        mesh.contraints.add(new Contraint(3, 4));
        
        mesh.contraints.add(new Contraint(0, 6));
        mesh.contraints.add(new Contraint(1, 7));
        mesh.contraints.add(new Contraint(2, 4));
        mesh.contraints.add(new Contraint(3, 5));
        
        if (null != t) {
            mesh.transform(t);
        }
        
        mesh.updateContraints();
        mesh.calculateTotalMass();

        return mesh;
    }
    
    public static Mesh createOnePointMesh(Point p, Transform t) throws ArrayLengthException {
        Mesh mesh = new Mesh();
        mesh.points.add(p);
        
        if (null != t) {
            mesh.transform(t);
        }
        
        mesh.updateContraints();
        mesh.calculateTotalMass();

        return mesh;
    }
    
    public void setPointsMass(double m) {
        for (Point p : points) {
            p.mass = m;
        }
    }

    public void transform(Vector3D position) {
        Transform t = new Transform();
        t.translate(position.x, position.y, position.z);

        transform(t);
    }

    public void transform(Transform t) {
        for (Point p : points) {
            p.curPos.set(t.transform(p.curPos));
            p.oldPos.set(p.curPos);
        }
    }
    
    public void translate(Vector3D t) {
        for (Point p : points) {
            p.curPos.add(t);
            p.oldPos.add(t);
        }
    }

    public void updateContraints() {
        for (Contraint c : contraints) {
            c.restLength = Vector3D.subtract(points.get(c.index1).curPos, points.get(c.index2).curPos).length();
        }
    }

    public int addPoint(Point p) {
        this.points.add(p);
        return points.size() - 1;
    }

    public Collection<Face> getFaces() {
        return faces.values();
    }

    public void addEdge(Edge e) {
        String id = e.getId();

        if (edges.containsKey(id)) {
            return;
        }

        edges.put(id, e);
    }

    public void addFace(Face f) throws ArrayLengthException {
        String id = f.getId();

        if (faces.containsKey(id)) {
            return;
        }

        addEdge(new Edge(f.indexes[0], f.indexes[1]));
        addEdge(new Edge(f.indexes[0], f.indexes[2]));
        addEdge(new Edge(f.indexes[1], f.indexes[2]));

        faces.put(id, f);
    }

    public boolean isInnerPoint(Point p) {
        if (isConvex()) {
            for (Face f : getFaces()) {
                if (!Intersection.isInside(points.get(f.getIndex0()).curPos, points.get(f.getIndex1()).curPos, points.get(f.getIndex2()).curPos, p.curPos)) {
                    return false;
                }
            }
        } else {

        }

        return true;
    }

    private boolean isConvex() {
        return true;
    }

    public Vector3D calculateFaceNormal(Face f) {
        return Face.calcNormal(getPoint(f.getIndex0()).curPos, getPoint(f.getIndex1()).curPos, getPoint(f.getIndex2()).curPos);
    }

    public Point getPoint(int i) {
        return points.get(i);
    }

    public Vector3D getClosestPoint(Point P, Vector3D N, AtomicInteger faceIndex) {

        Vector3D result = null;
        double dMin = Double.MAX_VALUE;
        for (Face f : getFaces()) {
            Point A = getPoint(f.getIndex0());
            Point B = getPoint(f.getIndex1());
            Point C = getPoint(f.getIndex2());
            
            N.set(Face.calcNormal(A.curPos, B.curPos, C.curPos));
            Vector3D R = Face.project(A.curPos, N, P.curPos);
            double d = R.distance(P.curPos);
            if (d < dMin) {
                dMin = d;
                result = R;
            }
        }
        
        /*if (dMin > 1d) {
            result.set(P.curPos);
        }*/

        return result;
    }
    
    public void calculateTotalMass() {
        totalMass = 0;
        for (Point p : points) {
            totalMass += p.mass;
        }
    }

    public double getTotalMass() {
        return totalMass;
    }
    
    
}
