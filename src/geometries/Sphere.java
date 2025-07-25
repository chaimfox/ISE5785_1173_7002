package geometries;

import primitives.*;
import java.util.List;
import static primitives.Util.alignZero;

/**
 * Represents a sphere in three-dimensional space.
 */
public class Sphere extends RadialGeometry {

    /**
     * The center point of the sphere.
     */
    private final Point centerPoint;

    /**
     * Constructs a new sphere with the specified center point and radius.
     *
     * @param centerPoint The center point of the sphere.
     * @param radius      The radius of the sphere.
     */
    public Sphere(Point centerPoint, double radius) {
        super(radius);
        this.centerPoint = centerPoint;
    }

    /**
     * Returns the normal vector at the specified point on the surface of the sphere.
     *
     * @param pointOnSurface The point on the surface of the sphere.
     * @return The normal vector at the specified point.
     */
    @Override
    public Vector getNormal(Point pointOnSurface) {
        return pointOnSurface.subtract(centerPoint).normalize();
    }


    /**
     * Finds the intersection points of a ray with the sphere.
     *
     * @param ray The ray to check for intersections.
     * @return A list of intersection points, or null if there are no intersections.
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray)  {
        if (ray.getHead().equals(centerPoint)) {
            // If the ray's head is at the center of the sphere, return one intersection point
            return List.of(new Intersection(this, ray.getPoint(radius), this.getMaterial()));
        }
        // Create a vector from the ray's head to the sphere's center
        Vector u = centerPoint.subtract(ray.getHead());
        // Calculate the projection of u onto the ray's direction
        double tm = ray.getDirection().dotProduct(u);
        // Calculate the distance from the sphere's center to the projection
        double d = Math.sqrt(u.lengthSquared() - tm * tm);

        // If d is greater than the sphere's radius, there are no intersections
        if (d >= radius) return null;

        // Calculate th and t0, t1
        double th = Math.sqrt(radius * radius - d * d);

        double t0 = alignZero(tm - th);
        double t1 = alignZero(tm + th);

        // If t0 and t1 are both positive, return both intersection points
        if (t0 > 0 && t1 > 0) {
            return List.of(
                    new Intersection(this, ray.getPoint(t0), this.getMaterial()),
                    new Intersection(this, ray.getPoint(t1), this.getMaterial()));
        }

        // If only one of them is positive, return that intersection point
        if (t0 > 0) {
            return List.of(new Intersection(this, ray.getPoint(t0), this.getMaterial()));
        }

        if (t1 > 0) {
            return List.of(new Intersection(this, ray.getPoint(t1), this.getMaterial()));
        }

        // If both are negative, return null
        return null;
    }

    /**
     * Calculates the bounding box of the sphere
     *
     * @return the bounding box of the sphere
     */
    @Override
    protected AABB calculateBoundingBox() {
        Point min = new Point(
                centerPoint.getX() - radius,
                centerPoint.getY() - radius,
                centerPoint.getZ() - radius
        );

        Point max = new Point(
                centerPoint.getX() + radius,
                centerPoint.getY() + radius,
                centerPoint.getZ() + radius
        );

        return new AABB(min, max);
    }


}