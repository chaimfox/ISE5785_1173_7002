package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for primitives.Point class
 *
 * @author alon greenstein, chaim fox
 */

class PointTests {

    private static final double DELTA = 0.000001;

    /**
     * Test method for {@link primitives.Point#add(primitives.Vector)}.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============
        Point p1 = new Point(1, 2, 3);
        Vector v1 = new Vector(4, 5, 6);

        // TC01: Test that add works for a Point
        assertEquals(new Point(5, 7, 9), p1.add(v1), "add() wrong result");

    }


    /**
     * Test method for {@link primitives.Point#subtract(primitives.Point)}.
     */
    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(4, 5, 6);

        // TC02: Test that subtract works for a Point and creates a Correct Vector
        assertEquals(new Vector(3, 3, 3), p2.subtract(p1), "subtract() wrong result");

        // =============== Boundary Values Tests ==================
        // TC03: Test that subtract works for the same point
        assertEquals(0, p1.subtract(p1).length(), DELTA, "subtract() wrong result on the same point");
    }


    /**
     * Test method for {@link primitives.Point#distance(primitives.Point)}.
     */
    @Test
    void testDistance() {
        // ============ Equivalence Partitions Tests ==============
        Point p1 = new Point(5, 7, 2);
        Point p2 = new Point(2, 3, 2);

        assertEquals(5.0, p1.distance(p2), DELTA, "distance() wrong result");

        // =============== Boundary Values Tests ==================
        // TC04: Test that distance works for the same point
        assertEquals(0, p1.distance(p1), DELTA, "distance() wrong result for the same point");
    }


    /**
     * Test method for {@link primitives.Point#distanceSquared(primitives.Point)}.
     */
    @Test
    void testDistanceSquared() {
        // ============ Equivalence Partitions Tests ==============
        Point p1 = new Point(5, 7, 2);
        Point p2 = new Point(2, 3, 2);

        // TC05: Test that distanceSquared works for a Point
        assertEquals(25.0, p1.distanceSquared(p2), DELTA, "distanceSquared() wrong result");

        // =============== Boundary Values Tests ==================
        // TC06: Test that distanceSquared works for the same point
        assertEquals(0, p1.distanceSquared(p1), DELTA,"distanceSquared() wrong result for the same point");

    }


}