package hoe.math;

import org.joml.Vector3d;
import org.joml.Vector3dc;

public class Rayd extends org.joml.Rayd {

    private final Vector3d origin = new Vector3d();
    private final Vector3d direction = new Vector3d();
    
    public Rayd(Vector3dc origin, Vector3dc direction) {
        super(origin, direction);
        this.origin.set(origin);
        this.direction.set(direction);
    }

    public Vector3d getOrigin() {
        return origin;
    }

    public Vector3d getDirection() {
        return direction;
    }
    
    public Lined toLine() {
        return new Lined(getOrigin(), getOrigin().add(getDirection(),new Vector3d()));
    }
}
