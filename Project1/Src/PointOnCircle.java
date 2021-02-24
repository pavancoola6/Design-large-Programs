package sample;

public class PointOnCircle {
    private int id;
    private double x;
    private double y;

    public PointOnCircle(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getX() {
        return this.x + 900.0D;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y + 512.0D;
    }

    public void setY(double y) {
        this.y = y;
    }

    public static PointOnCircle[] generatePoints(double radius, double num) {
        PointOnCircle[] points = new PointOnCircle[(int)num];
        int i = 0;
        double pointSeparation = 360.0D / num;

        for(double angle = 180.0D; angle < 540.0D; angle += pointSeparation) {
            double x = Math.cos(Math.toRadians(angle)) * radius;
            double y = StrictMath.sin(Math.toRadians(angle)) * radius;
            points[i] = new PointOnCircle(i, x, y);
            ++i;
        }

        return points;
    }
}