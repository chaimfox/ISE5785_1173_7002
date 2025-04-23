package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for geometries.Sphere class
 *
 * @authors alon greenstein, chaim fox
 */

public class SphereTests {


    /**
     * Test method for {@link geometries.Sphere#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {

        // ============ Equivalence Partitions Tests ==============
        Point center = new Point(1, 1, 1);
        Sphere sphere = new Sphere(center, 5);
        Point onSurface = new Point(6, 1, 1);
        Vector result = sphere.getNormal(onSurface);

        Vector normal = new Vector(1, 0, 0);
        // tc01: test if the normal is correct
        assertEquals(normal, result, "Sphere's normal is not the correct normal");
    }


    /**
     * A point used in some tests
     */
    private final Point p001 = new Point(0, 0, 1);
    /**
     * A point used in some tests
     */
    private final Point p100 = new Point(1, 0, 0);
    /**
     * A vector used in some tests
     */
    private final Vector v001 = new Vector(0, 0, 1);

    /**
     * Test method for {@link geometries.Sphere#findIntersections(primitives.Ray)}.
     */
    @Test
    public void testFindIntersections() {
        Sphere sphere = new Sphere(p100, 1d);

        final Point gp1 = new Point(0.0651530771650466, 0.355051025721682, 0);
        final Point gp2 = new Point(1.53484692283495, 0.844948974278318, 0);
        final Point gp3 = new Point(0.5, 0.86602540378443864676372317075294, 0);
        List<Point> exp = List.of(gp1, gp2);

        Vector vecUp = new Vector(1, 1, 0);
        Vector vecDown = new Vector(3, 1, 0);

        Point p01 = new Point(-1, 0, 0);


        // ============ Equivalence Partitions Tests ==============
        // TC01: Ray's line is outside the sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(p01, vecUp)), "There should be 0 intersection points");

        // TC02: Ray starts before and crosses the sphere (2 points)
        final var result1 = sphere.findIntersections(new Ray(p01, vecDown));
        assertNotNull(result1, "Can't be empty list");
        assertEquals(2, result1.size(), "Wrong number of points");
        assertEquals(exp, result1, "Incorrect cutoff value");

        // TC03: Ray starts inside the sphere (1 point)
        vecUp = new Vector(0, 1, 0);
        p01 = new Point(0.5, 0.5, 0);
        exp = List.of(gp3);

        final var result2 = sphere.findIntersections(new Ray(p01, vecUp));
        assertNotNull(result2, "Can't be empty list");
        assertEquals(1, result2.size(), "Wrong number of points");
        assertEquals(exp, result2, "Incorrect cutoff value");

        // TC04: Ray starts after the sphere (0 points)
        p01 = new Point(0.5, 2, 0);
        assertNull(sphere.findIntersections(new Ray(p01, vecUp)), "There should be 0 intersection points");

        // =============== Boundary Values Tests ==================

        // **** Group 1: Ray's line crosses the sphere (but not the center)
        // TC11: Ray starts at sphere and goes inside (1 points)
        
        vecDown = new Vector(0, -1, 0);
        exp = List.of(new Point(0.5, -0.86602540378443864676372317075294, 0));

        final var result3 = sphere.findIntersections(new Ray(gp3, vecDown));
        assertNotNull(result3, "Can't be empty list");
        assertEquals(1, result3.size(), "Wrong number of points");
        assertEquals(exp, result3, "Incorrect cutoff value");

        // TC12: Ray starts at sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(gp3, vecUp)), "There should be 0 intersection points");


        // **** Group 2: Ray's line goes through the center
        // TC21: Ray starts before the sphere (2 points)
        p01 = new Point(1, 2, 0);
        exp = List.of(new Point(1, 1, 0), new Point(1, -1, 0));

        final var result4 = sphere.findIntersections(new Ray(p01, vecDown));
        assertNotNull(result4, "Can't be empty list");
        assertEquals(2, result4.size(), "Wrong number of points");
        assertEquals(exp, result4, "Incorrect cutoff value");

        // TC22: Ray starts at sphere and goes inside (1 points)
        p01 = new Point(1, 1, 0);
        exp = List.of(new Point(1, -1, 0));

        final var result5 = sphere.findIntersections(new Ray(p01, vecDown));
        assertNotNull(result5, "Can't be empty list");
        assertEquals(1, result5.size(), "Wrong number of points");
        assertEquals(exp, result5, "Incorrect cutoff value");
        
        
        // TC23: Ray starts inside (1 points)
        p01 = new Point(1, 0.5, 0);
        exp = List.of(new Point(1,1,0));

        final var result6 = sphere.findIntersections(new Ray(p01, vecUp));
        assertNotNull(result6, "Can't be empty list");
        assertEquals(1, result6.size(), "Wrong number of points");
        assertEquals(exp, result6, "Incorrect cutoff value");

        // TC24: Ray starts at the center (1 points)
        p01 = new Point(1, 0, 0);

        final var result7 = sphere.findIntersections(new Ray(p01, vecUp));
        assertNotNull(result7, "Can't be empty list");
        assertEquals(1, result7.size(), "Wrong number of points");
        assertEquals(exp, result7, "Incorrect cutoff value");

        // TC25: Ray starts at sphere and goes outside (0 points)
        p01 = new Point(1, 1, 0);
        assertNull(sphere.findIntersections(new Ray(p01, vecUp)), "There should be 0 intersection points");

        // TC26: Ray starts after sphere (0 points)
        p01 = new Point(1, 2, 0);
        assertNull(sphere.findIntersections(new Ray(p01, vecUp)), "There should be 0 intersection points");


        // **** Group 3: Ray's line is tangent to the sphere (all tests 0 points)
        // TC31: Ray starts before the tangent point
        p01 = new Point(2, -1, 0);
        assertNull(sphere.findIntersections(new Ray(p01, vecUp)), "There should be 0 intersection points");

        // TC32: Ray starts at the tangent point
        p01 = new Point(2, 0, 0);
        assertNull(sphere.findIntersections(new Ray(p01, vecUp)), "There should be 0 intersection points");


        // TC33: Ray starts after the tangent point
        p01 = new Point(2, 1, 0);
        assertNull(sphere.findIntersections(new Ray(p01, vecUp)), "There should be 0 intersection points");


        // **** Group 4: Special cases
        // TC41: Ray's line is outside sphere, ray is orthogonal to ray start to sphere's center line
        p01 = new Point(3, 0, 0);
        assertNull(sphere.findIntersections(new Ray(p01, vecUp)), "There should be 0 intersection points");

        // TC42: Ray's starts inside, ray is orthogonal to ray start to sphere's center line
        p01 = new Point(0.5, 0, 0);
        exp = List.of(gp3);

        final var result8 = sphere.findIntersections(new Ray(p01, vecUp));
        assertNotNull(result8, "Can't be empty list");
        assertEquals(1, result8.size(), "Wrong number of points");
        assertEquals(exp, result8, "Incorrect cutoff value");

    }
}
