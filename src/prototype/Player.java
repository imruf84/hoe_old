package prototype;

import physics.Vector3D;

public class Player {
    
    private final String name;
    private final Vector3D position;
    private final Curve path = new Curve();

    public Player(String name, Vector3D position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public Curve getPath() {
        return path;
    }

    public Vector3D getPosition() {
        return position;
    }
    
    public void addNavigationPoint(Vector3D p) {
        
        Curve c = getPath();
        if (c.isEmpty()) {
            // HACK: origo in global space
            getPath().appendPoint(new Vector3D());
        }
        
        // HACK: p in global space
        p.x-=getPosition().x;
        p.y-=getPosition().y;
        getPath().appendPoint(p);
    }
}
