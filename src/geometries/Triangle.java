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
        var intersections = plane.findIntersections(ray);
        // Check if the ray intersects the plane of the triangle
        if (intersections == null)
            return null;

        // Retrieve the vertices of the triangle
        Point p0 = vertices.getFirst();
        Point p1 = vertices.get(1);
        Point p2 = vertices.getLast();

        // Retrieve the direction vector and head point of the ray
        Vector rayDirection = ray.getDirection();
        Point rayPoint = ray.getHead();

        // Calculate vectors representing edges of the triangle
        Vector v1 = p0.subtract(rayPoint);
        Vector v2 = p1.subtract(rayPoint);
        // Calculate normal vectors to the triangle's edges
        Vector n1 = v1.crossProduct(v2).normalize();
        // Calculate dot products between the normal vectors and the ray direction
        double d1 = Util.alignZero(n1.dotProduct(rayDirection));
        // Check if the ray does not intersects the triangle.
        if (d1 == 0)
            return null;

        Vector v3 = p2.subtract(rayPoint);
        Vector n2 = v2.crossProduct(v3).normalize();
        double d2 = Util.alignZero(n2.dotProduct(rayDirection));
        // Check if the ray does not intersects the triangle
        if (d1 * d2 <= 0)
            return null;

        Vector n3 = v3.crossProduct(v1).normalize();
        double d3 = Util.alignZero(n3.dotProduct(rayDirection));
        // Check if the ray does not intersects the triangle
        if (d1 * d3 <= 0)
            return null;

        return intersections;
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