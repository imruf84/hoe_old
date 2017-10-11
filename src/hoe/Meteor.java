package hoe;

import java.awt.geom.Point2D;
import java.sql.SQLException;

/**
 * Meteor osztálya.
 * 
 * @author imruf84
 */
public class Meteor {
    
    public static final String OBJECT_TYPE = "meteor";
    /**
     * Azonosító.
     */
    private long ID = -1;
    /**
     * Tömeg.
     */
    private long mass = 0;
    /**
     * Átmérő.
     */
    private long diameter = 0;
    /**
     * Tulajdonos.
     */
    private String owner = "";
    /**
     * Pontszám.
     */
    private long points = 0;
    /**
     * Pozíció.
     */
    private final Point2D.Double position = new Point2D.Double();
    /**
     * Sebesség.
     */
    private final Point2D.Double velocity = new Point2D.Double();

    
    /**
     * Konstruktor.
     * 
     * @param mass tömeg
     * @param diameter átmérő
     * @param owner tulajdonos
     * @param points pontszám
     */
    public Meteor(long mass, long diameter, String owner, long points) {
        this.mass = mass;
        this.diameter = diameter;
        this.owner = owner;
        this.points = points;
    }
    
    /**
     * Konstruktor.
     * 
     * @param ID azonosító
     * @param mass tömeg
     * @param diameter átmérő
     * @param owner tulajdonos
     * @param points pontszám
     * @param x pozíció első koordinátája
     * @param y pozíció második koordinátája
     * @param vx sebesség első koordinátája
     * @param vy sebesség második koordinátája
     */
    public Meteor(long ID, long mass, long diameter, String owner, long points, double x, double y, double vx, double vy) {
        this.ID = ID;
        this.mass = mass;
        this.diameter = diameter;
        this.owner = owner;
        this.points = points;
        this.position.setLocation(x, y);
        this.velocity.setLocation(vx, vy);
    }

    /**
     * Konstruktor.
     * 
     * @param mass tömeg
     * @param diameter átmérő
     * @param owner tulajdonos
     * @param points pontszám
     * @param x pozíció első koordinátája
     * @param y pozíció második koordinátája
     * @param vx sebesség első koordinátája
     * @param vy sebesség második koordinátája
     */
    public Meteor(long mass, long diameter, String owner, long points, double x, double y, double vx, double vy) {
        this.mass = mass;
        this.diameter = diameter;
        this.owner = owner;
        this.points = points;
        this.position.setLocation(x, y);
        this.velocity.setLocation(vx, vy);
    }

    /**
     * Azonosító lekérdezése.
     * 
     * @return azonosító
     */
    public long getID() {
        return ID;
    }

    /**
     * Azonosító megadása.
     * 
     * @param ID azonosító
     */
    public void setID(long ID) {
        this.ID = ID;
    }

    /**
     * Tömeg lekérdezése.
     * 
     * @return tömeg
     */
    public long getMass() {
        return mass;
    }

    /**
     * Tömeg megadása.
     * 
     * @param mass tömeg
     */
    public void setMass(long mass) {
        this.mass = mass;
    }

    /**
     * Átmérő lekérdezése.
     * 
     * @return átmérő
     */
    public long getDiameter() {
        return diameter;
    }

    /**
     * Átmérő megadása.
     * 
     * @param diameter átmérő
     */
    public void setDiameter(long diameter) {
        this.diameter = diameter;
    }

    /**
     * Tulajdonos lekérdezése.
     * 
     * @return tulajdonos
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Tulajdonos megadása.
     *
     * @param owner tulajdonos
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Pontszám lekérdezése.
     * 
     * @return pontszám
     */
    public long getPoints() {
        return points;
    }

    /**
     * Pontszám megadása.
     * 
     * @param points pontszám
     */
    public void setPoints(long points) {
        this.points = points;
    }

    /**
     * Pozíció lekérdezése.
     * 
     * @return pozíció
     */
    public Point2D.Double getPosition() {
        return position;
    }

    /**
     * Sebesség lekérdezése.
     * 
     * @return sebesség
     */
    public Point2D.Double getVelocity() {
        return velocity;
    }
    
    /**
     * Meteor tárolása adatbázisban.
     * 
     * @return meteor
     * @throws SQLException kivétel
     */
    public Meteor storeToDataBase() throws SQLException {
        Universe.addMeteor(this);
        return this;
    }
    
    /**
     * Átalakítás json karakterlánccá.
     * 
     * @return json karakterlánc
     */
    public String toJson() {
        return "{\"a\":\"meteor\",\"d\":{\"id\":" + getID() + ",\"d\":" + getDiameter() + ",\"x\":" + getPosition().x + ",\"y\":" + getPosition().y + "}}";
    }
    
}
