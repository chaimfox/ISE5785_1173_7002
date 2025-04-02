package geometries;

/**
 * The abstract class RadialGeometry represents a geometric shape defined by a radius.
 * It extends the Geometry interface, inheriting the method to calculate the normal vector.
 */
public abstract class RadialGeometry extends Geometry {

    /** The radius of the radial geometry. */
    protected final double radius;

    /**
     * Constructs a new radial geometry with the specified radius.
     *
     * @param radius The radius of the radial geometry.
     */
    public RadialGeometry(double radius) {
        this.radius = radius;
    }

}