package hoe.physics;

public class Point {
    Vector3D curPos;
    Vector3D oldPos;
    Vector3D force = new Vector3D();
    boolean unmovable;
    double mass = 1d;

    public Point() {
        curPos = new Vector3D();
        oldPos = new Vector3D();
        unmovable = false;
    }
    
    public Point(Vector3D v) {
        curPos = new Vector3D(v);
        oldPos = new Vector3D(v);
        this.mass = 1;
        unmovable = false;
    }

    public Point(double x, double y, double z) {
        this.curPos = new Vector3D(x,y,z);
        this.oldPos = new Vector3D(x,y,z);
        this.mass = 1;
        this.unmovable = false;
    }
    
    public Point(Vector3D v, double mass) {
        curPos = new Vector3D(v);
        oldPos = new Vector3D(v);
        this.mass = mass;
        unmovable = false;
    }

    public Point(double x, double y, double z, double mass) {
        this.curPos = new Vector3D(x,y,z);
        this.oldPos = new Vector3D(x,y,z);
        this.mass = mass;
        this.unmovable = false;
    }
    
    public Vector3D calculateVelocity() {
        return Vector3D.subtract(curPos, oldPos);
    }

}
