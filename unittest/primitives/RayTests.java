package primitives;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RayTests {

    /**
     * Test method for {@link primitives.Ray#getPoint(double t)}.
     */
    @Test
    public void getPoint() {
        // ============ Equivalence Partitions Tests ==============
        Point p01 = new Point(1, 2, 0);
        Vector vec1 = new Vector(1, 0, 0);
        Ray ray = new Ray(p01, vec1);
        Point exp = new Point(3, 2, 0);


        // TC01: test if getPoint() returns the correct point for a positive scalar
        Point result = ray.getPoint(2);
        assertEquals(exp, result, "getPoint() wrong result in positive scalar");

        // TC02: test if getPoint() returns the correct point for a negative scalar
        result = ray.getPoint(-2);
        exp = new Point(-1, 2, 0);
        assertEquals(exp, result, "getPoint() wrong result in negative scalar");

        // =============== Boundary Values Tests ==================
        // TC03: test if getPoint() returns the correct point for a zero scalar
        result = ray.getPoint(0);
        assertEquals(p01, result, "getPoint() supposed to return the origin point when t=0");
    }


    /**
     * Test method for {@link primitives.Ray#findClosestPoint(List)}.
     */
    @Test
    void testFindClosestPoint() {
        final Point p1 = new Point(1, 0, 0);
        final Point p2 = new Point(2, 0, 0);
        final Point p3 = new Point(3, 0, 0);
        final Point p4 = new Point(4, 0, 0);
        Ray ray = new Ray(p1, new Vector(1, 0, 0));

        // ============ Equivalence Partitions Tests ==============
        // TC01: the point in the middle of the list is the closest
        assertEquals(p2, ray.findClosestPoint(List.of(p3, p2, p4)), "Bad findClosestPoint middle of list");

        // ================= Boundary Values Tests =================
        // TC02: the list is empty
        assertNull(ray.findClosestPoint(List.of()), "Bad findClosestPoint with empty list");

        // TC03: the point in the beginning of the list is the closest
        assertEquals(p2, ray.findClosestPoint(List.of(p2, p3, p4)), "Bad findClosestPoint beginning of list");

        // TC04: the point in the end of the list is the closest
        assertEquals(p2, ray.findClosestPoint(List.of(p4, p3, p2)), "Bad findClosestPoint end of list");

    }
}
