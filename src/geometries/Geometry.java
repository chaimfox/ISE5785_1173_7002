package geometries;

import primitives.Color;
import primitives.Point;
import primitives.Vector;


/**
 * The Geometry interface represents a geometric shape in three-dimensional space.
 * Classes that implement this interface must provide a method to calculate
 * the normal vector at a specified point on the surface of the shape.
 */
public abstract class Geometry extends Intersectable {

    /**
     * The color of the geometry
     */
    protected Color emission = Color.BLACK;

    /**
     * The material of the geometry
     */
    public Color getEmission() {
        return emission;
    }

    /**
     * Set the emission color of the geometry
     *
     * @param emission the emission color to set
     * @return the geometry
     */
    public Geometry setEmission(Color emission) {
        this.emission = emission;
        return this;
    }

    /**
     * Calculates the normal vector at the specified point on the surface of the geometry.
     *
     * @param pointOnSurface The point on the surface of the geometry.
     * @return The normal vector at the specified point.
     */
    public abstract Vector getNormal(Point pointOnSurface);

}
