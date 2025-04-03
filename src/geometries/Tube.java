package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents a tube in three-dimensional space.
 */
public class Tube extends RadialGeometry {

    /** The axis of the tube. */
    protected final Ray axis;

    /**
     * Constructs a new tube with the specified radius and axis.
     *
     * @param radius The radius of the tube.
     * @param axis The axis of the tube.
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        this.axis = axis;
    }

    /**
     * Returns the normal vector at the specified point on the surface of the tube.
     *
     * @param pointOnSurface The point on the surface of the tube.
     * @return The normal vector at the specified point.
     */
    @Override
    public Vector getNormal(Point pointOnSurface) {
        Vector normal;
        Vector vec = pointOnSurface.subtract(axis.getHead()); // Vector from the axis to the point
        double t = axis.getDirection().dotProduct(vec);

        // if (p-p0) is orthogonal to the axis direction
        if (t == 0) {
            normal = pointOnSurface.subtract(axis.getHead());
            return normal.normalize();
        }
        Point o = axis.getHead().add(axis.getDirection().scale(t));
        normal = pointOnSurface.subtract(o);
        return normal.normalize();

    }
}