package hoe.physics;

public class CollisionInfo {

    private final Point p;
    private final Vector3D v;

    public CollisionInfo(Point p, Vector3D v) {
        this.p = p;
        this.v = v;
    }

    public void apply() {
        p.oldPos.subtract(v);
    }

}
