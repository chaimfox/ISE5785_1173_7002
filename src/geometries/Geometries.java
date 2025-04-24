package geometries;

import java.util.*;
import primitives.Point;
import primitives.Ray;
/**
 * The abstract class Geometries represents a collection of intersectable
 * geometries. It implements the Intersectable interface.
 */
public class Geometries implements Intersectable {

    /** The list of intersectable geometries. */
    final private List<Intersectable> intersectables = new LinkedList<>();

    /**
     * Constructs an empty Geometries object.
     */
    public Geometries() {
    }

    /**
     * Constructs a Geometries object initialized with given geometries.
     *
     * @param geometries the array of geometries to add
     */
    public Geometries(Intersectable... geometries) {
        this.add(geometries);
    }

    /**
     * Adds geometries to this collection.
     *
     * @param geometries the array of geometries to add
     */
    public void add(Intersectable... geometries) {
        this.intersectables.addAll(List.of(geometries));
    }

    /**
     * Finds intersection points between a ray and the geometries in this
     * collection.
     *
     * @param ray the ray to intersect with the geometries
     * @return a list of intersection points, or null if there are no intersections
     */
    public List<Point> findIntersections(Ray ray) {

        List<Point> intersections = null;

        for (Intersectable geometry : intersectables) {
            // get all intersections
            List<Point> tempIntersections = geometry.findIntersections(ray);
            // if any intersections
            if (tempIntersections != null) {
                // for first add
                if (intersections == null)
                    intersections = new LinkedList<>(tempIntersections);
                else
                    intersections.addAll(tempIntersections);
            }
        }

        return intersections;
    }
}
