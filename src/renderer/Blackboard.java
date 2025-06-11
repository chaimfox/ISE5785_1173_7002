package renderer;

import primitives.Point;
import primitives.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static primitives.Util.*;

/**
 * The Blackboard class generates a grid of points on a plane perpendicular to a given normal vector.
 *
 *
 */
public class Blackboard {

    /**
     * The vector in the x direction.
     */
    private static final Vector VX = new Vector(1, 0, 0);
    /**
     * The vector in the y direction.
     */
    private static final Vector VY = new Vector(0, 1, 0);
    /**
     * The random object for generating random numbers.
     */
    private static final Random RANDOM = new Random();

    /**
     * Default constructor for the Blackboard class.
     * This constructor is provided for completeness but does not perform any specific initialization.
     */
    public Blackboard() {
        // No specific initialization required
    }

    /**
     * Generates a grid of points on a plane perpendicular to the given normal vector.
     *
     * @param n              The normal vector of the plane.
     * @param center         The center point of the grid.
     * @param halfGrid   The distance from the center to the edge of the grid.
     * @param gridResolution The number of cells along each dimension of the grid.
     * @return A list of points representing the grid.
     * @throws IllegalArgumentException if radius is negative or gridResolution is less than 1.
     */
    public static List<Point> generateGrid(Vector n, Point center, double halfGrid, int gridResolution) {
        if (isZero(halfGrid)) {
            return List.of(center);
        }

        if (gridResolution < 1 || halfGrid < 0) {
            throw new IllegalArgumentException("Radius must be non-negative and gridResolution must be at least 1");
        }

        Vector vRight = calculateRightVector(n);
        Vector vUp = n.crossProduct(vRight).normalize();

        double cellSize = 2 * halfGrid / gridResolution;
        Point topLeft = center.add(vRight.scale(-halfGrid).add(vUp.scale(halfGrid)));

        List<Point> points = new ArrayList<>(gridResolution * gridResolution);
        for (int i = 0; i < gridResolution; i++) {
            for (int j = 0; j < gridResolution; j++) {
                points.add(topLeft
                        .add(vRight.scale((i + RANDOM.nextDouble()) * cellSize))
                        .add(vUp.scale(-(j + RANDOM.nextDouble()) * cellSize))
                );
            }
        }

        return points;
    }

    /**
     * Generates a grid of points on a circle perpendicular to the given normal vector.
     * @param n                 The normal vector of the plane.
     * @param center            The center point of the grid.
     * @param radius            The radius of the circular area to generate points in.
     * @param gridResolution    The number of points to generate on the circle.
     * @return A list of points representing the grid.
     * @throws IllegalArgumentException if radius is negative or gridResolution is less than 1.
     */
    public static List<Point> generateCircleGrid(Vector n, Point center, double radius, int gridResolution) {
        return generateGrid(n, center, radius, gridResolution)
                .stream()
                .filter(p -> center.distance(p) <= radius)
                .toList();
    }


    /**
     * Calculates the right vector perpendicular to the given normal vector.
     *
     * @param n The normal vector.
     * @return The right vector.
     */
    private static Vector calculateRightVector(Vector n) {
        try {
            return n.crossProduct(VX).normalize();
        } catch (IllegalArgumentException e) {
            return n.crossProduct(VY).normalize();
        }
    }
}