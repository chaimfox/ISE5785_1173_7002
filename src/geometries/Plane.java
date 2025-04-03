package geometries;

import primitives.Point;
import primitives.Vector;

/**
 * Represents a plane in three-dimensional space.
 */
public class Plane extends Geometry {

    /** A point on the plane. */
    private final Point pointOnPlane;

    /** The normal vector to the plane. */
    private final Vector normalVector;

    /**
     * Constructs a plane passing through three points.
     * Calculates the normal vector using the cross product of two vectors formed by the points.
     * @param a The first point.
     * @param b The second point.
     * @param c The third point.
     */
    public Plane(Point a, Point b, Point c) {
        this.pointOnPlane = a;
        Vector v1 = b.subtract(a);
        Vector v2 = c.subtract(a);
        this.normalVector = v1.crossProduct(v2).normalize();
    }

    /**
     * Constructs a plane with the specified point on the plane and normal vector.
     *
     * @param pointOnPlane The point on the plane.
     * @param normalVector The normal vector to the plane.
     */
    public Plane(Point pointOnPlane, Vector normalVector) {
        this.pointOnPlane = pointOnPlane;
        this.normalVector = normalVector.normalize();
    }

    /**
     * Returns the normal vector to the plane.
     *
     * @param pointOnSurface (unused) A point on the surface of the plane.
     * @return The normal vector to the plane.
     */
    @Override
    public Vector getNormal(Point pointOnSurface) {
        return normalVector;
    }

    /**
     * Returns the normal vector to the plane.
     *
     * @return The normal vector to the plane.
     */
    public Vector getNormal() {
        return normalVector;
    }
}