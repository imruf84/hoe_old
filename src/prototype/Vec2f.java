package prototype;

public class Vec2f {

    public float x, y;

    public Vec2f(float nx, float ny) {
        x = nx;
        y = ny;
    }

    public Vec2f() {
    }

    public Vec2f set(float nx, float ny) {
        x = nx;
        y = ny;
        return this;
    }

    public Vec2f midPoint(Vec2f o) {
        return new Vec2f((x + o.x) / 2, (y + o.y) / 2);
    }

    public Vec2f scale(float s) {
        x *= s;
        y *= s;
        return this;
    }

    public Vec2f add(Vec2f o) {
        x += o.x;
        y += o.y;
        return this;
    }

    public Vec2f sub(Vec2f o) {
        x -= o.x;
        y -= o.y;
        return this;
    }

    public Vec2f nor() {
        float nx = -y, ny = x;
        x = nx;
        y = ny;
        return this;
    }

    public Vec2f normalize() {
        float len = len();
        x /= len;
        y /= len;
        return this;
    }

    public Vec2f cpy() {
        return new Vec2f(x, y);
    }

    public Vec2f add(float nx, float ny) {
        x += nx;
        y += ny;
        return this;
    }

    public float len() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public boolean equals(Vec2f o) {
        return (x == o.x && y == o.y);
    }

    @Override
    public String toString() {
        return String.format("(%.3f, %.3f)", x, y);
    }
}
