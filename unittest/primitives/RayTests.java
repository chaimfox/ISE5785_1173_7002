package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
}
