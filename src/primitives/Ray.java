package primitives;

import java.util.List;
import geometries.Intersectable.Intersection;
import static primitives.Util.isZero;

/**
 * Represents a ray in three-dimensional space.
 */
public class Ray {

    /**
     * The starting point (head) of the ray.
     */
    private final Point head;

    /**
     * The direction vector of the ray.
     */
    private final Vector direction;

    /**
     * Constructs a new ray with the specified starting point and direction.
     *
     * @param head      The starting point (head) of the ray.
     * @param direction The direction vector of the ray.
     */
    public Ray(Point head, Vector direction) {
        this.head = head;
        this.direction = direction.normalize();
    }

    /**
     * Returns the starting point (head) of the ray.
     *
     * @return The starting point (head) of the ray.
     */
    public Point getHead() {
        return head;
    }

    /**
     * Returns the direction vector of the ray.
     *
     * @return The direction vector of the ray.
     */
    public Vector getDirection() {
        return direction;
    }

    /**
     * Checks if this ray is equal to another object.
     *
     * @param obj The object to compare to.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Ray other &&
                this.head.equals(other.head) &&
                this.direction.equals(other.direction);
    }

    /**
     * Returns a string representation of this ray.
     *
     * @return A string representation of this ray.
     */
    @Override
    public String toString() {
        return "Ray:" + head + "->" + direction;
    }


    /**
     * Returns a point on the ray at a distance t from the head.
     *
     * @param t The distance from the head to the point.
     * @return The point on the ray at distance t from the head.
     */
    public Point getPoint(double t) {
        if (isZero(t)) {
            return head;
        }
        return head.add(direction.scale(t));
    }

    /**
     * method to find the closest point to the head of the ray
     *
     * @param intersections the list of intersections
     * @return the closest point to the head of the ray
     */
    public Intersection findClosestIntersection(List<Intersection> intersections) {
        if (intersections == null || intersections.isEmpty()) {
            return null;
        }
        Intersection closest = null;
        double minDistance = Double.POSITIVE_INFINITY;

        for (Intersection inter : intersections) {
            double distance = head.distance(inter.point);
            if (distance < minDistance) {
                minDistance = distance;
                closest = inter;
            }
        }
        return closest;
    }



    public Point findClosestPoint(List<Point> points) {
        if (points == null || points.isEmpty()) {
            return null;
        }
        return findClosestIntersection(points.stream().map(p -> new Intersection(null, p,null)).toList()).point;
    }
}
