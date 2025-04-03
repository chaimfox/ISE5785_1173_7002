/**
 *
 */
package geometries;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Unit tests for geometries.Cylinder class
 *
 * @authors alon greenstein, chaim fox
 */

class CylinderTests {


    /**
     * Test method for {@link geometries.Cylinder#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        Point head = new Point(4, 0, 0);
        Vector diraction = new Vector(1, 0, 0);
        Ray axis = new Ray(head, diraction);
        Cylinder c = new Cylinder(6, axis, 2);

        Point onCasing = new Point(7, 2, 0);
        Vector n1 = c.getNormal(onCasing);

        Point firstBase = new Point(4, 0, 1);
        Vector n2 = c.getNormal(firstBase);

        Point secondBase = new Point(10, 0, 1);
        Vector n3 = c.getNormal(secondBase);

        Vector CasingNormal = new Vector(0, 1, 0);
        Vector firstBaseNormal = new Vector(-1, 0, 0);
        Vector secondBaseNormal = new Vector(1, 0, 0);

        // TC01: Test that in casing case, the normal is current.
        assertEquals(CasingNormal, n1, "getNormal() wrong result for Casing case");

        // TC02: Test that in first base case, the normal is current.
        assertEquals(firstBaseNormal, n2, "getNormal() wrong result for first base case");

        // TC03: Test that in second base case, the normal is current.
        assertEquals(secondBaseNormal, n3, "getNormal() wrong result for second base case");

        // =============== Boundary Values Tests ==================

        Point firstBaseCenter = new Point(4, 0, 0);
        n1 = c.getNormal(firstBaseCenter);

        Point secondBaseCenter = new Point(10, 0, 0);
        n2 = c.getNormal(secondBaseCenter);

        Point firstBaseEdge = new Point(4, 2, 0);
        n3 = c.getNormal(firstBaseEdge);

        Point secondBaseEdge = new Point(10, 2, 0);
        Vector n4 = c.getNormal(secondBaseEdge);

        // TC04: Test that in case of center point of the first base, the normal is current.
        assertEquals(firstBaseNormal, n1,
                "getNormal() wrong result for the center point of the first base");

        // TC05: Test that in case of center point of the second base, the normal is current.
        assertEquals(secondBaseNormal, n2,
                "getNormal() wrong result for the center point of the second base");

        // TC06: Test that in case of edge point in the first base, the normal is current.
        assertEquals(firstBaseNormal, n3,
                "getNormal() wrong result for a point on the edge of the first base");

        // TC07: Test that in case of edge point in the second base, the normal is current.
        assertEquals(secondBaseNormal, n4,
                "getNormal() wrong result for a point on the edge of the second base");

    }

}