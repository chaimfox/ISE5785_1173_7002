package primitives;

/**
 * Represents a ray in three-dimensional space.
 */
public class Ray {

    /** The starting point (head) of the ray. */
    private final Point head;

    /** The direction vector of the ray. */
    private final Vector direction;

    /**
     * Constructs a new ray with the specified starting point and direction.
     *
     * @param head The starting point (head) of the ray.
     * @param direction The direction vector of the ray.
     */
    public Ray(Point head, Vector direction) {
        this.head = head;
        this.direction = direction.normalize();
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

}