package prototype;

import java.util.LinkedList;
import physics.Vector3D;

public class Player {

    private final String name;
    private final CurvePoint position;
    private final Curve path = new Curve();

    public Player(String name, Vector3D position) {
        this.name = name;
        this.position = new CurvePoint(position, 0);
    }

    public String getName() {
        return name;
    }

    public Curve getPath() {
        return path;
    }

    public CurvePoint getPosition() {
        return position;
    }
    
    protected void recalculatePath(Vector3D newPos) {
        
        // Remove the control points behind the player.
        int n = (int) Math.floor(getPosition().t);
        for (int i = 0; i < n; i++) {
            removeNavigationPoint(0);
        }
        
        Vector3D lp = getPath().getPoints().get(0);
        lp.set(newPos);
        getPosition().t = 0;
    }
    
    public void update() {
    }

    public void oneStep() {
        LinkedList<CurvePoint> nextPoints = getPath().generatePathPointsByLength(getPosition().t, 2d, 2);
        CurvePoint nextPos = nextPoints.getLast();
        getPosition().set(nextPos);
        update();
    }
    
    public void removeNavigationPoint(int i) {
        getPath().getPoints().remove(i);
    }

    public void addNavigationPoint(Vector3D p) {

        Curve c = getPath();
        if (c.isEmpty()) {
            // HACK: origo in global space
            //getPath().appendPoint(new Vector3D());

            getPath().appendPoint(new Vector3D(getPosition()));
        }

        // HACK: p in global space
        //p.x-=getPosition().x;
        //p.y-=getPosition().y;
        getPath().appendPoint(p);
    }

}
