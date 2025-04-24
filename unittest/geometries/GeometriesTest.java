/**
 *
 */
package geometries;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import geometries.*;
import primitives.*;

/**
 *
 */
class GeometriesTest {

    /**
     * Test method for
     * {@link geometries.Geometries#findIntersections(primitives.Ray)}.
     */
    @Test
    void testFindIntersections() {

        Geometries geometries = new Geometries(new Sphere(new Point(1, 0, 0), 1), //
                new Plane(new Point(0, 0, 1), new Point(0, 1, 0), new Point(1, 0, 0)), //
                new Triangle(new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 0, 1)));

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects 2 out of 3 geometries
        var result = geometries.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 1, 1)));
        assertEquals(3, result.size(), "There should be three intersections");

        // =============== Boundary Values Tests ==================
        // TC11: No geometries exist (0 points)
        Geometries emptyGeometries = new Geometries();
        assertNull(emptyGeometries.findIntersections(new Ray(new Point(0, 0, 0), new Vector(1, 1, 1))),
                "There shouldn't be any intersections");

        // TC12: Ray does not intersect with any geometries (0 points)
        assertNull(geometries.findIntersections(new Ray(new Point(0, 2, 0), new Vector(1, 1, 1))),
                "There shouldn't be any intersections");

        // TC13: Ray only intersects one geometry (1 point)
        assertEquals(1, geometries.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(0, 0, 1.5))).size(),
                "There should be one intersection");

        // TC14: Ray's line intersects with all geometries (4 points)
        geometries = new Geometries(new Sphere(new Point(6, 2, 0), 1),
                new Plane(new Point(2, 2, 1), new Vector(-1, 0, 0)),
                new Triangle(new Point(19, -20, 26), new Point(20, 20, 1), new Point(18, -18, -28)));
        result = geometries.findIntersections(new Ray(new Point(0, 2, 0), new Vector(1, 0, 0)));
        assertEquals(4, result.size(), "There should be four intersections");
    }

}
