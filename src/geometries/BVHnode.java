package geometries;

import primitives.AABB;
import primitives.Ray;
import java.util.List;
import java.util.ArrayList;

/**
 * BVH Node for hierarchical spatial organization
 * Stage 2: Manual hierarchy building for improved ray tracing performance
 *
 * @author Yehuda Rabin and Arye Hacohen
 */
public class BVHNode extends Intersectable {
    /* * Left and right children of the BVH node
     * If this is a leaf node, left will contain the geometry
     * If this is an internal node, left and right will contain child nodes or geometries
     */
    private Intersectable left;
    /* * Right child of the BVH node
     * Can be null if this is a leaf node
     */
    private Intersectable right;
    /* * Flag to indicate if this node is a leaf (contains a single geometry)
     * If true, left contains the geometry and right is null
     * If false, left and right contain child nodes or geometries
     */
    private boolean isLeaf;

    /**
     * Constructor for leaf node (single geometry)
     * @param geometry the geometry object to wrap
     */
    public BVHNode(Intersectable geometry) {
        this.left = geometry;
        this.right = null;
        this.isLeaf = true;
    }

    /**
     * Constructor for internal node (two children)
     * @param left left child node or geometry
     * @param right right child node or geometry
     */
    public BVHNode(Intersectable left, Intersectable right) {
        this.left = left;
        this.right = right;
        this.isLeaf = false;
    }

    @Override
    protected AABB calculateBoundingBox() {
        AABB leftBox = left.getBoundingBox();

        if (isLeaf || right == null) {
            return leftBox;
        }

        AABB rightBox = right.getBoundingBox();
        return AABB.combine(leftBox, rightBox);
    }

    /**
     * Calculate intersections with a ray
     * @param ray the ray to test for intersections
     * @param maxDistance maximum distance to check for intersections
     * @return list of intersections, or null if no intersections found
     */
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
        // 🔥 CRITICAL: Add bounding box check FIRST!
        if (!getBoundingBox().intersect(ray)) {
            return null;
        }

        List<Intersection> result = null;

        // Check left child
        List<Intersection> leftIntersections = left.calculateIntersections(ray, maxDistance);
        if (leftIntersections != null) {
            result = new ArrayList<>(leftIntersections);
        }

        // Check right child only if not leaf
        if (!isLeaf && right != null) {
            List<Intersection> rightIntersections = right.calculateIntersections(ray, maxDistance);
            if (rightIntersections != null) {
                if (result == null) {
                    result = new ArrayList<>(rightIntersections);
                } else {
                    result.addAll(rightIntersections);
                }
            }
        }

        return result;
    }

    /**
     * Get the left and right children
     * @return left and right intersectable objects
     */
    public Intersectable getLeft() { return left; }
    /**
     * Get the right child
     * @return right intersectable object, or null if leaf
     */
    public Intersectable getRight() { return right; }
    /**
     * Check if this node is a leaf (contains a single geometry)
     * @return true if leaf, false if internal node
     */
    public boolean isLeaf() { return isLeaf; }

    @Override
    public String toString() {
        return "BVHNode{" + (isLeaf ? "leaf" : "internal") +
                ", boundingBox=" + getBoundingBox() + "}";
    }
}