package geometries;

import primitives.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Automatic BVH hierarchy builder
 * Stage 3: Automatic spatial partitioning using SAH (Surface Area Heuristic)
 */
public class BVHBuilder {
    /**
     * Maximum number of objects per leaf node
     * This is a trade-off between performance and memory usage.
     * A smaller number leads to deeper trees, while a larger number leads to shallower trees.
     */
    private static final int MAX_OBJECTS_PER_LEAF = 4;
    /**
     * Maximum depth of the BVH tree
     * This prevents infinite recursion and limits the tree's height.
     */
    private static final int MAX_DEPTH = 20;

    /**
     * Build BVH automatically from a collection of geometries
     */
    public static Intersectable buildBVH(List<Intersectable> objects) {
        // Validate input
        if (objects.isEmpty()) {
            return null;
        }

        // If there's only one object, return it directly
        if (objects.size() == 1) {
            return objects.get(0);
        }

        // Filter out objects without bounding boxes (like infinite planes)
        List<Intersectable> finiteObjects = new ArrayList<>();
        List<Intersectable> infiniteObjects = new ArrayList<>();

        // Separate finite and infinite objects
        for (Intersectable obj : objects) {
            if (obj.getBoundingBox() != null) {
                finiteObjects.add(obj);
            } else {
                infiniteObjects.add(obj);
            }
        }

        // Build BVH for finite objects only
        Intersectable bvhRoot = null;
        if (!finiteObjects.isEmpty()) {
            if (finiteObjects.size() == 1) {
                bvhRoot = finiteObjects.get(0);
            } else {
                bvhRoot = buildBVHRecursive(finiteObjects, 0);
            }
        }

        // If we have infinite objects, create a combined structure
        if (!infiniteObjects.isEmpty()) {
            Geometries infiniteGroup = new Geometries();
            for (Intersectable obj : infiniteObjects) {
                infiniteGroup.add(obj);
            }

            // If we have a BVH root, combine it with infinite objects
            if (bvhRoot != null) {
                // Combine BVH with infinite objects
                Geometries combined = new Geometries();
                combined.add(bvhRoot);
                combined.add(infiniteGroup);
                return combined;
            } else {
                return infiniteGroup;
            }
        }

        return bvhRoot;
    }

    /**
     * Recursively build the BVH using SAH
     * @param objects List of intersectable objects
     * @param depth Current depth in the BVH tree
     * @return The root of the BVH
     */
    private static Intersectable buildBVHRecursive(List<Intersectable> objects, int depth) {
        // Base cases
        if (objects.size() <= MAX_OBJECTS_PER_LEAF || depth >= MAX_DEPTH) {
            return createLeafNode(objects);
        }

        // Find best split using SAH (Surface Area Heuristic)
        Split bestSplit = findBestSplit(objects);

        if (bestSplit == null) {
            return createLeafNode(objects);
        }

        // Recursively build left and right subtrees
        Intersectable left = buildBVHRecursive(bestSplit.leftObjects, depth + 1);
        Intersectable right = buildBVHRecursive(bestSplit.rightObjects, depth + 1);

        return new BVHNode(left, right);
    }

    /**
     * Create a leaf node for the BVH
     * If there's only one object, return it directly.
     * If there are multiple objects, create a Geometries collection.
     * @param objects List of intersectable objects
     * @return A leaf node containing the objects
     */
    private static Intersectable createLeafNode(List<Intersectable> objects) {
        if (objects.size() == 1) {
            return objects.get(0);
        }

        // Create a Geometries collection for multiple objects
        Geometries leaf = new Geometries();
        for (Intersectable obj : objects) {
            // יש להניח שיש מתודה add() ב-Geometries
            leaf.add(obj);
        }
        return leaf;
    }

    /**
     * Find the best split for the given objects using Surface Area Heuristic (SAH)
     * @param objects List of intersectable objects
     * @return The best split found, or null if no valid split exists
     */
    private static Split findBestSplit(List<Intersectable> objects) {
        // Calculate overall bounding box
        AABB overallBox = calculateBoundingBox(objects);
        if (overallBox == null) return null;

        Vector size = overallBox.getSize();

        // Find the longest axis
        int bestAxis = 0;
        double maxSize = size.getX();

        if (size.getY() > maxSize) {
            bestAxis = 1;
            maxSize = size.getY();
        }
        if (size.getZ() > maxSize) {
            bestAxis = 2;
        }

        // Sort objects by their center coordinate on the best axis
        final int axis = bestAxis;
        objects.sort((a, b) -> {
            Point centerA = a.getBoundingBox().getCenter();
            Point centerB = b.getBoundingBox().getCenter();
            return Double.compare(getCoordinate(centerA, axis), getCoordinate(centerB, axis));
        });

        // Try different split positions and find the best one using SAH
        Split bestSplit = null;
        double bestCost = Double.POSITIVE_INFINITY;

        // Iterate through possible split positions
        for (int i = 1; i < objects.size(); i++) {
            List<Intersectable> leftObjects = objects.subList(0, i);
            List<Intersectable> rightObjects = objects.subList(i, objects.size());

            double cost = calculateSAHCost(leftObjects, rightObjects, overallBox);

            // If the cost is better than the best found so far, update
            if (cost < bestCost) {
                bestCost = cost;
                bestSplit = new Split(
                        new ArrayList<>(leftObjects),
                        new ArrayList<>(rightObjects)
                );
            }
        }

        return bestSplit;
    }

    /**
     * Get the coordinate of a point based on the specified axis
     * @param point The point to get the coordinate from
     * @param axis The axis (0 for X, 1 for Y, 2 for Z)
     * @return The coordinate value on the specified axis
     */
    private static double getCoordinate(Point point, int axis) {
        return switch (axis) {
            case 0 -> point.getX();
            case 1 -> point.getY();
            case 2 -> point.getZ();
            default -> throw new IllegalArgumentException("Invalid axis: " + axis);
        };
    }

    /**
     * Calculate the SAH cost for a given split of objects
     * @param leftObjects List of objects in the left partition
     * @param rightObjects List of objects in the right partition
     * @param overallBox The overall bounding box of all objects
     * @return The SAH cost for the split
     */
    private static double calculateSAHCost(List<Intersectable> leftObjects,
                                           List<Intersectable> rightObjects,
                                           AABB overallBox) {
        AABB leftBox = calculateBoundingBox(leftObjects);
        AABB rightBox = calculateBoundingBox(rightObjects);

        if (leftBox == null || rightBox == null) {
            return Double.POSITIVE_INFINITY;
        }

        // Calculate surface areas
        double overallArea = overallBox.getSurfaceArea();
        double leftArea = leftBox.getSurfaceArea();
        double rightArea = rightBox.getSurfaceArea();

        // SAH cost function
        return (leftArea / overallArea) * leftObjects.size() +
                (rightArea / overallArea) * rightObjects.size();
    }

    /**
     * Calculate the bounding box for a list of objects
     * @param objects List of intersectable objects
     * @return The combined bounding box, or null if no objects are present
     */
    private static AABB calculateBoundingBox(List<Intersectable> objects) {
        if (objects.isEmpty()) return null;

        AABB result = objects.get(0).getBoundingBox();
        for (int i = 1; i < objects.size(); i++) {
            AABB box = objects.get(i).getBoundingBox();
            if (box != null) {
                result = AABB.combine(result, box);
            }
        }

        return result;
    }

    /**
     * Represents a split of objects into left and right partitions
     */
    private static class Split {
        final List<Intersectable> leftObjects;
        final List<Intersectable> rightObjects;

        Split(List<Intersectable> left, List<Intersectable> right) {
            this.leftObjects = left;
            this.rightObjects = right;
        }
    }

    /**
     * Print BVH statistics for debugging and analysis
     */
    public static void printBVHStatistics(Intersectable bvh) {
        if (bvh == null) {
            System.out.println("BVH is null");
            return;
        }

        BVHStats stats = collectStatistics(bvh, 0);
        System.out.println("BVH Statistics:");
        System.out.println("Total nodes: " + stats.totalNodes);
        System.out.println("Leaf nodes: " + stats.leafNodes);
        System.out.println("Internal nodes: " + stats.internalNodes);
        System.out.println("Max depth: " + stats.maxDepth);
        System.out.println("Average objects per leaf: " +
                (stats.leafNodes > 0 ? (double)stats.totalObjects / stats.leafNodes : 0));
    }

    /**
     * Collect statistics about the BVH structure
     * @param node The current node in the BVH
     * @param depth The current depth in the BVH tree
     * @return Statistics about the BVH
     */
    private static BVHStats collectStatistics(Intersectable node, int depth) {
        BVHStats stats = new BVHStats();
        stats.maxDepth = depth;
        stats.totalNodes = 1;

        if (node instanceof BVHNode) {
            stats.internalNodes = 1;
            BVHNode bvhNode = (BVHNode) node;
            BVHStats leftStats = collectStatistics(bvhNode.getLeft(), depth + 1);
            BVHStats rightStats = collectStatistics(bvhNode.getRight(), depth + 1);
            stats.combine(leftStats);
            stats.combine(rightStats);
        } else {
            stats.leafNodes = 1;
            stats.totalObjects = 1; // אובייקט יחיד או Geometries
        }

        return stats;
    }

    /**
     * Represents a node in the BVH tree
     */
    private static class BVHStats {
        // Statistics for the BVH
        int totalNodes = 0;
        int leafNodes = 0;
        int internalNodes = 0;
        int maxDepth = 0;
        int totalObjects = 0;

        /**
         * Combine statistics from another BVHStats object
         * @param other The other BVHStats to combine with
         */
        void combine(BVHStats other) {
            totalNodes += other.totalNodes;
            leafNodes += other.leafNodes;
            internalNodes += other.internalNodes;
            maxDepth = Math.max(maxDepth, other.maxDepth);
            totalObjects += other.totalObjects;
        }
    }
}