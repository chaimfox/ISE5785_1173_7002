package geometries;

import primitives.*;


/**
 * Represents a cylinder in three-dimensional space.
 * A cylinder is defined by its height and a ray that defines its axis.
 */
public class Cylinder extends Tube {

    /**
     * The height of the cylinder.
     */
    private final double height;

    /**
     * Constructs a new cylinder with the specified height, axis ray, and radius.
     *
     * @param height  The height of the cylinder.
     * @param axisRay The ray that defines the axis of the cylinder.
     * @param radius  The radius of the cylinder.
     */
    public Cylinder(double height, Ray axisRay, double radius) {
        super(radius, axisRay);
        this.height = height;
    }


    /**
     * Returns the normal vector at the specified point on the surface of the cylinder.
     *
     * @param pointOnSurface The point on the surface of the cylinder.
     * @return The normal vector at the specified point.
     */
    @Override
    public Vector getNormal(Point pointOnSurface) {

        Point firstBaseCenter = axis.getHead();
        Vector dir = axis.getDirection();
        Point secondBaseCenter = firstBaseCenter.add(dir.scale(height));

        // If the point is at the center of first base, return the opposite of the direction vector
        if (pointOnSurface.equals(firstBaseCenter)) {
            return dir.scale(-1);
        }

        // If the point is at the center of the second base, return the direction vector
        if (pointOnSurface.equals(secondBaseCenter)){
            return dir;
        }

        // Check if the point is on the first base
        Vector firstCenterToPoint = pointOnSurface.subtract(firstBaseCenter);
        if (firstCenterToPoint.dotProduct(dir) == 0) {
            return dir.scale(-1);
        }

        // Check if the point is on the second base
        Vector secondCenterToPoint = pointOnSurface.subtract(secondBaseCenter);
        if (secondCenterToPoint.dotProduct(dir) == 0) {
            return dir;
        }

        // If the point is on the lateral surface, delegate to Tube's normal calculation
        return super.getNormal(pointOnSurface);
    }

}