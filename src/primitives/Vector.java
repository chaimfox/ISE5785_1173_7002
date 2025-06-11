package primitives;

/**
 * Represents a vector in three-dimensional space.
 */
public class Vector extends Point {

    public static Vector AXIS_X = new Vector(1d, 0d, 0d);
    public static Vector AXIS_Y = new Vector(0d, 1d, 0d);
    public static Vector AXIS_Z = new Vector(0d, 0d, 1d);

    /**
     * Constructs a new vector with the specified coordinates.
     *
     * @param x The x-coordinate of the vector.
     * @param y The y-coordinate of the vector.
     * @param z The z-coordinate of the vector.
     * @throws IllegalArgumentException if the vector has zero length.
     */
    public Vector(double x, double y, double z) {
        super(x, y, z);
        if (xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Invalid parameter: Vector cannot have zero length");
        }
    }

    /**
     * Constructs a new vector with the specified coordinates as a Double3 object.
     *
     * @param xyz The coordinates of the vector as a Double3 object.
     * @throws IllegalArgumentException if the vector has zero length.
     */
    public Vector(Double3 xyz) {
        super(xyz);
        if (xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Invalid parameter: Vector cannot have zero length");
        }
    }



    /**
     * Checks if this vector is equal to another object.
     *
     * @param obj The object to compare to.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Vector other && super.equals(other);
    }

    /**
     * Returns a string representation of this vector.
     *
     * @return A string representation of this vector.
     */
    @Override
    public String toString() {
        return "->" + super.toString();
    }

    /**
     * Calculates the square of the length of this vector.
     *
     * @return The square of the length of this vector.
     */
    public double lengthSquared() {
        return dotProduct(this);
    }

    /**
     * Calculates the length of this vector.
     *
     * @return The length of this vector.
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Adds another vector to this vector, returning a new vector.
     *
     * @param vec The vector to add.
     * @return The resulting vector after adding the other vector.
     */
    public Vector add(Vector vec) {
        return new Vector(this.xyz.add(vec.xyz));
    }

    /**
     * Scales this vector by a scalar value, returning a new vector.
     *
     * @param scalar The scalar value to scale by.
     * @return The resulting scaled vector.
     */
    public Vector scale(double scalar) {
        return new Vector(xyz.scale(scalar));
    }

    /**
     * Calculates the dot product of this vector with another vector.
     *
     * @param vec The other vector.
     * @return The dot product of this vector with the other vector.
     */
    public double dotProduct(Vector vec) {
        return xyz.d1 * vec.xyz.d1 +
                xyz.d2 * vec.xyz.d2 +
                xyz.d3 * vec.xyz.d3;
    }

    /**
     * Calculates the cross product of this vector with another vector.
     *
     * @param vec The other vector.
     * @return The cross product of this vector with the other vector.
     */
    public Vector crossProduct(Vector vec) {
        return new Vector(xyz.d2 * vec.xyz.d3 - xyz.d3 * vec.xyz.d2,
                xyz.d3 * vec.xyz.d1 - xyz.d1 * vec.xyz.d3,
                xyz.d1 * vec.xyz.d2 - xyz.d2 * vec.xyz.d1);
    }

    /**
     * Normalizes this vector, returning a new vector with length 1.
     *
     * @return The normalized vector.
     */
    public Vector normalize() {
        return scale(1 / length());
    }
}