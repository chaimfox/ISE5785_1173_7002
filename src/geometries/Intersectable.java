package geometries;

import primitives.Point;
import primitives.Ray;
import java.util.List;

/**
 * The Intersectable interface represents a geometric object that can be intersected by a ray.
 * Classes implementing this interface must provide a method to find the intersection points
 * between a ray and the object.
 */


public interface Intersectable {


    /**
     * Finds the intersection points between a ray and the object implementing this interface.
     *
     * @param ray The ray to check for intersections with.
     * @return A list of intersection points. If there are no intersections, an empty list is returned.
     */
    public List<Point> findIntersections(Ray ray);
}
