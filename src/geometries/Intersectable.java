package geometries;

import lighting.*;
import primitives.*;
import java.util.List;

/**
 * The Intersectable interface represents a geometric object that can be intersected by a ray.
 * Classes implementing this interface must provide a method to find the intersection points
 * between a ray and the object.
 */


public abstract class Intersectable {


    /**
     * Find intersections of a ray with the geometry
     * @param ray the ray to find intersections with
     * @return a list of intersection points
     */
    protected abstract List<Intersection> calculateIntersectionsHelper(Ray ray);

    /**
     * Find intersections of a ray with the geometry
     * @param ray the ray to find intersections with
     * @return a list of intersection points
     */
    public final List<Intersection> calculateIntersections(Ray ray){
        return calculateIntersectionsHelper(ray);
    }

    /**
     * Find intersections of a ray with the geometry
     * @param ray the ray to find intersections with
     * @return a list of intersection points
     */
    public final List<Point> findIntersections(Ray ray) {
        var list = calculateIntersections(ray);
        return list == null ? null : list.stream().map(intersection -> intersection.point).toList();
    }



    /**
     * The Intersection class represents an intersection point between a ray and a geometry.
     * It contains the geometry and the intersection point.
     */
    public static class Intersection {
        public final Geometry geometry;
        public final Point point;
        public final Material material;
        public Vector v;
        public Vector normal;
        public double vNormal;
        public LightSource light;
        public Vector l;
        public double lNormal;

        /**
         * Constructor for Intersection
         * @param geometry the geometry
         * @param point the point
         */
        public Intersection(Geometry geometry, Point point, Material material) {
            this.geometry = geometry;
            this.point = point;
            this.material = material;
        }

        /**
         * Returns the geometry of the intersection.
         * @return the geometry
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            return obj instanceof Intersection other &&
                    geometry == other.geometry && point.equals(other.point);
        }

        /**
         * Returns a string representation of the intersection.
         * @return a string representation
         */
        @Override
        public String toString() {
            return "Intersection{" + "geometry=" + geometry + ", point=" + point + '}';
        }
    }
}
