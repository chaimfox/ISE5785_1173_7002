package geometries;

import primitives.Point;
import primitives.Vector;

/**
 * The Geometry interface represents a geometric shape in three-dimensional space.
 * Classes that implement this interface must provide a method to calculate
 * the normal vector at a specified point on the surface of the shape.
 */
public interface Geometry {

    /**
     * Calculates the normal vector at the specified point on the surface of the geometry.
     *
     * @param pointOnSurface The point on the surface of the geometry.
     * @return The normal vector at the specified point.
     */
    public Vector getNormal(Point pointOnSurface);

}
