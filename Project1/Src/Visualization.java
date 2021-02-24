package sample;


import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Visualization {
    private double r = 0.0D;
    private int c = 0;
    double g = 0.3D;
    double b = 0.1D;
    private boolean increment = true;
    private double timesTableNum;
    private double radius;

    public Visualization(double timesTableNum, double radius) {
        this.timesTableNum = timesTableNum;
        this.radius = radius;
    }

    public double getR() {
        return this.r;
    }

    public double getRadius() {
        return this.radius;
    }

    public double getTimesTableNum() {
        return this.timesTableNum;
    }

    public void setTimesTableNum(double timesTableNum) {
        this.timesTableNum = timesTableNum;
    }

    public void incrementTimesTableNum(double stepNum) {
        this.timesTableNum += stepNum;
    }

    private void incrementRed() {
        if (Double.compare(this.r, 0.9D) == 1) {
            this.increment = false;
        }

        if (Double.compare(this.r, 0.0D) == 0) {
            this.increment = true;
        }

        if (this.increment) {
            this.r += 0.10000000149011612D;
        } else {
            this.r -= 0.10000000149011612D;
        }

    }

    public Group generateLines(double numPoints) {
        this.incrementRed();
        Color color = Color.color(this.r, this.g, this.b);
        Group lines = new Group();
        sample.PointOnCircle[] points = sample.PointOnCircle.generatePoints(this.radius, numPoints);
        int PointID = 0;
        sample.PointOnCircle[] var7 = points;
        int var8 = points.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            sample.PointOnCircle poc = var7[var9];
            ++PointID;
            double correspondingPointId = (double)(PointID - 1) * this.timesTableNum % numPoints;
            sample.PointOnCircle pointTo = points[(int)correspondingPointId];
            sample.PointOnCircle pointFrom = points[poc.getId()];
            Line line = new Line(pointFrom.getX(), pointFrom.getY(), pointTo.getX(), pointTo.getY());
            line.setStroke(color);
            lines.getChildren().add(line);
        }

        return lines;
    }
}
