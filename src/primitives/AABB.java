package primitives;

/**
 * Axis-Aligned Bounding Box for spatial optimization
 * Stage 1: Conservative Boundary Region implementation
 * This class represents an AABB in 3D space defined by two points: min and max.
 * It provides methods to check if a ray intersects with the AABB,
 * combine two AABBs, and calculate properties like size, center, and surface area.
 */
public class AABB {
    /* * The AABB is defined by two points:
     * - min: the minimum corner of the box
     * - max: the maximum corner of the box
     */
    private final Point min;
    private final Point max;

    public AABB(Point min, Point max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Check if ray intersects with this AABB
     * @param ray the ray to check
     * @return true if intersection exists
     */
    public boolean intersect(Ray ray) {
        Point rayOrigin = ray.getPoint(0); // משתמש בgetPoint במקום getHead
        Vector rayDir = ray.getDirection();

        double tMin = Double.NEGATIVE_INFINITY;
        double tMax = Double.POSITIVE_INFINITY;

        // Check intersection with each axis
        double[] origins = {rayOrigin.getX(), rayOrigin.getY(), rayOrigin.getZ()};
        double[] directions = {rayDir.getX(), rayDir.getY(), rayDir.getZ()};
        double[] minBounds = {min.getX(), min.getY(), min.getZ()};
        double[] maxBounds = {max.getX(), max.getY(), max.getZ()};

        for (int i = 0; i < 3; i++) {
            if (Math.abs(directions[i]) < 1e-10) { // Ray is parallel to axis
                if (origins[i] < minBounds[i] || origins[i] > maxBounds[i]) {
                    return false;
                }
            } else {
                double t1 = (minBounds[i] - origins[i]) / directions[i];
                double t2 = (maxBounds[i] - origins[i]) / directions[i];

                if (t1 > t2) { // Swap if needed
                    double temp = t1;
                    t1 = t2;
                    t2 = temp;
                }

                tMin = Math.max(tMin, t1);
                tMax = Math.min(tMax, t2);

                if (tMin > tMax || tMax < 0) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Combine two AABBs into one that contains both
     */
    public static AABB combine(AABB box1, AABB box2) {
        // If either box is null, return the other box
        if (box1 == null) return box2;
        if (box2 == null) return box1;

        // Calculate the new minimum point by taking the minimum of each coordinate
        Point newMin = new Point(
                Math.min(box1.min.getX(), box2.min.getX()),
                Math.min(box1.min.getY(), box2.min.getY()),
                Math.min(box1.min.getZ(), box2.min.getZ())
        );

        // Calculate the new maximum point by taking the maximum of each coordinate
        Point newMax = new Point(
                Math.max(box1.max.getX(), box2.max.getX()),
                Math.max(box1.max.getY(), box2.max.getY()),
                Math.max(box1.max.getZ(), box2.max.getZ())
        );

        return new AABB(newMin, newMax);
    }

    // Getters
    public Point getMin() { return min; }
    public Point getMax() { return max; }
    public Vector getSize() { return max.subtract(min); }
    public Point getCenter() {
        Vector half = max.subtract(min).scale(0.5);
        return min.add(half);
    }

    /**
     * Calculate the surface area of the AABB
     * @return surface area
     */
    public double getSurfaceArea() {
        Vector size = getSize();
        double dx = size.getX();
        double dy = size.getY();
        double dz = size.getZ();
        return 2 * (dx * dy + dy * dz + dz * dx);
    }

    @Override
    public String toString() {
        return "AABB{min=" + min + ", max=" + max + "}";
    }
}