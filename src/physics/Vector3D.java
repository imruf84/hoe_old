package physics;

public class Vector3D {

    public double x, y, z;

    public Vector3D() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D(Vector3D v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public Vector3D cpy() {
        return new Vector3D(x, y, z);
    }

    public void set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final void set(Vector3D v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public double length() {
        return (double) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double len() {
        return length();
    }

    public double lengthSq() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public void normalize() {
        double l = (double) (1 / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z));
        this.x *= l;
        this.y *= l;
        this.z *= l;
    }

    public Vector3D add(Vector3D v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;

        return this;
    }

    public static Vector3D add(Vector3D v1, Vector3D v2) {
        return new Vector3D(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
    }

    public static Vector3D add(Vector3D v1, Vector3D v2, Vector3D v3) {
        return new Vector3D(v1.x + v2.x + v3.x, v1.y + v2.y + v3.y, v1.z + v2.z + v3.z);
    }

    public static double distance(Vector3D v1, Vector3D v2) {
        return Math.sqrt((v1.x - v2.x) * (v1.x - v2.x) + (v1.y - v2.y) * (v1.y - v2.y) + (v1.z - v2.z) * (v1.z - v2.z));
    }

    public double distance(Vector3D v) {
        return distance(this, v);
    }

    public double get(int i) {
        switch (i) {
            case 0:
                return x;
            case 1:
                return y;
        }

        return z;
    }

    public Vector3D subtract(Vector3D v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;

        return this;
    }

    public Vector3D sub(Vector3D o) {
        return subtract(o);
    }

    public static Vector3D subtract(Vector3D v1, Vector3D v2) {
        return new Vector3D(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }

    public Vector3D scale(double s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;

        return this;
    }

    public static Vector3D scale(Vector3D v, double s) {
        return new Vector3D(s * v.x, s * v.y, s * v.z);
    }

    public double dot(Vector3D v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public static double dot(Vector3D v1, Vector3D v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public Vector3D cross(Vector3D v) {
        return new Vector3D(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
    }

    public static Vector3D cross(Vector3D v1, Vector3D v2) {
        return new Vector3D(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x);
    }

    @Override
    public String toString() {
        return "Vector3D{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vector3D other = (Vector3D) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return this.z == other.z;
    }

}
