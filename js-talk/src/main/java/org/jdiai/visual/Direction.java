package org.jdiai.visual;

import com.epam.jdi.tools.func.JFunc2;
import org.openqa.selenium.Point;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.String.format;

public class Direction {
    public static JFunc2<Direction, Direction, Boolean> VECTOR_SIMILARITY =
            (v1, v2) -> v1.angle == v2.angle && v1.length == v2.length;

    private int angle;
    private int length;

    public Direction(int angle, int length) {
        this.angle = angle;
        this.length = length;
    }
    public Direction(Point point1, Point point2) {
        double difX = point2.x - point1.x;
        double difY = point2.y - point1.y;
        angle = calculateAngle(difX, difY);
        length = (int) sqrt(pow(difY, 2) + pow(difX, 2));
    }
    private int calculateAngle(double difX, double difY) {
        float angle = (float) Math.toDegrees(Math.atan2(-difY, difX));
        if(angle < 0){
            angle += 360;
        }
        return (int) angle;
    }
    public int angle() { return angle; }
    public double length() { return length; }
    public boolean isSimilarTo(Direction vector) {
        return VECTOR_SIMILARITY.execute(this, vector);
    }
    @Override
    public String toString() {
        return format("(angle:%s, length:%s)", angle, length);
    }
}
