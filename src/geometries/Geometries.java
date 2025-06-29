package geometries;

import java.util.*;
import primitives.*;

/**
 * The abstract class Geometries represents a collection of intersectable
 * geometries. It implements the Intersectable interface.
 */
public class Geometries extends Intersectable {

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
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        List<Intersection> intersections = null;
        for (Intersectable geometry : intersectables) {
            var geometryIntersections = geometry.calculateIntersections(ray);
            if (geometryIntersections != null) {
                if (intersections == null)
                    intersections = new LinkedList<>();
                intersections.addAll(geometryIntersections);
            }
        }
        return intersections;
    }

    /**Add commentMore actions
     * Calculate the bounding box that contains all geometries in this collection.
     * Combines all individual bounding boxes into one that encompasses all geometries.
     *
     * @return AABB that contains all geometries, or null if collection is empty or no valid boxes
     */
    @Override
    protected AABB calculateBoundingBox() {
        if (intersectables.isEmpty()) {
            return null;
        }

        AABB combinedBox = null;

        // עבור על כל הגיאומטריות וחבר את ה-bounding boxes שלהן
        for (Intersectable geometry : intersectables) {
            AABB currentBox = geometry.getBoundingBox();
            if (currentBox != null) {
                if (combinedBox == null) {
                    combinedBox = currentBox;
                } else {
                    combinedBox = AABB.combine(combinedBox, currentBox);
                }
            }
        }

        return combinedBox;
    }

    /**
     * Get the number of geometries in this collection.
     * Useful for performance analysis and debugging.
     *
     * @return the number of geometries in the collection
     */
    public int size() {
        return intersectables.size();
    }

    /**
     * Check if the collection is empty.
     *
     * @return true if no geometries are in the collection
     */
    public boolean isEmpty() {
        return intersectables.isEmpty();
    }

    /**
     * Clear all geometries from the collection.
     * Useful for resetting the scene.
     */
    public void clear() {
        intersectables.clear();
        invalidateBoundingBox(); // Force recalculation of bounding box
    }


}
