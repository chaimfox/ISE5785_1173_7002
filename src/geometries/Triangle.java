package geometries;

import primitives.Point;

/**
 * Represents a triangle in three-dimensional space.
 */
public class Triangle extends Polygon {

    public Triangle(Point p1, Point p2, Point p3) {
        super(new Point[]{p1, p2, p3});
    }


}