package geometries;

import primitives.Point;

/**
 * Represents a triangle in three-dimensional space.
 */
public class Triangle extends Polygon {

    /**
     * Constructs a new triangle with the specified vertices.
     *
     * @param p1 The first vertex of the triangle.
     * @param p2 The second vertex of the triangle.
     * @param p3 The third vertex of the triangle.
     */
    public Triangle(Point p1, Point p2, Point p3) {
        super(new Point[]{p1, p2, p3});
    }


}