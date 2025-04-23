package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

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

    public List<Point> findIntersections(Ray ray) {
        // Find the intersections with the triangle's plane
        List<Point> intersections = plane.findIntersections(ray);
        if (intersections == null) {
            return null;
        }

        // Check if the intersection point is inside the triangle
        Point intersectionPoint = intersections.get(0);
        if (isPointInTriangle(intersectionPoint)) {
            return List.of(intersectionPoint);
        }
        return null;
    }

    private boolean isPointInTriangle(Point p) {
        // וקטורים מהנקודה לקודקודי המשולש
        Vector v1 = vertices.get(0).subtract(p);
        Vector v2 = vertices.get(1).subtract(p);
        Vector v3 = vertices.get(2).subtract(p);

        // מכפלות וקטוריות של וקטורים סמוכים
        Vector cross1 = v1.crossProduct(v2);
        Vector cross2 = v2.crossProduct(v3);
        Vector cross3 = v3.crossProduct(v1);

        // בדיקה אם לכל המכפלות הווקטוריות יש אותו סימן
        double dot12 = cross1.dotProduct(cross2);
        double dot23 = cross2.dotProduct(cross3);

        return dot12 > 0 && dot23 > 0;
    }
}

























/*
List<Point> intersections = super.findIntersections(ray);
        if (intersections == null || intersections.isEmpty()) {
            return null;
        }
        Point intersectionPoint = intersections.get(0);
        if (intersectionPoint.equals(vertices[0]) || intersectionPoint.equals(vertices[1]) || intersectionPoint.equals(vertices[2])) {
            return null;
        }
        return intersections;
 */