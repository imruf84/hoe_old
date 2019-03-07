package hoe;

import java.awt.geom.Point2D;
import java.sql.SQLException;

public class Meteor {

    public static final String OBJECT_TYPE = "meteor";
    private long ID = -1;
    private long mass = 0;
    private long diameter = 0;
    private String owner = "";
    private long points = 0;
    private final Point2D.Double position = new Point2D.Double();
    private final Point2D.Double velocity = new Point2D.Double();

    public Meteor(long ID) {
        setID(ID);
    }
    
    public Meteor(long mass, long diameter, String owner, long points) {
        this.mass = mass;
        this.diameter = diameter;
        this.owner = owner;
        this.points = points;
    }

    public Meteor(long ID, long mass, long diameter, String owner, long points, double x, double y, double vx, double vy) {
        this.ID = ID;
        this.mass = mass;
        this.diameter = diameter;
        this.owner = owner;
        this.points = points;
        this.position.setLocation(x, y);
        this.velocity.setLocation(vx, vy);
    }

    public Meteor(long mass, long diameter, String owner, long points, double x, double y, double vx, double vy) {
        this.mass = mass;
        this.diameter = diameter;
        this.owner = owner;
        this.points = points;
        this.position.setLocation(x, y);
        this.velocity.setLocation(vx, vy);
    }

    public long getID() {
        return ID;
    }

    public final void setID(long ID) {
        this.ID = ID;
    }

    public long getMass() {
        return mass;
    }

    public void setMass(long mass) {
        this.mass = mass;
    }

    public long getDiameter() {
        return diameter;
    }

    public void setDiameter(long diameter) {
        this.diameter = diameter;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public void setPosition(double x, double y) {
        getPosition().setLocation(x, y);
    }
    
    public Point2D.Double getPosition() {
        return position;
    }
    
    public void setVelocity(double x, double y) {
        getVelocity().setLocation(x, y);
    }

    public Point2D.Double getVelocity() {
        return velocity;
    }

    public Meteor storeToDataBase() throws SQLException {
        SceneManager.addMeteor(this);
        return this;
    }
    
    public Meteor getFromDataBase() throws SQLException {
        SceneManager.addMeteor(this);
        return this;
    }

    public String toJson() {
        return "{\"a\":\"meteor\",\"d\":{\"id\":" + getID() + ",\"d\":" + getDiameter() + ",\"x\":" + getPosition().x + ",\"y\":" + getPosition().y + "}}";
    }

}
