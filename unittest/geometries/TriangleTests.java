package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for geometries.Triangle class
 *
 * @authors alon greenstein, chaim fox
 */

public class TriangleTests {

    /**
     * Delta value for accuracy when comparing the numbers of type 'double' in
     * assertEquals
     */
    private final double DELTA = 0.000001;


    /**
     * Test method for {@link geometries.Triangle#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        Point a = new Point(1, 0, 0);
        Point b = new Point(0, 1, 0);
        Point c = new Point(0, 0, 1);
        Triangle triangle = new Triangle(a, b, c);
        Vector result = triangle.getNormal(a);

        // TC01: ensure |result| = 1
        assertEquals(1, result.length(), DELTA, "Triangle's normal is not a unit vector");

        // TC02: test if the normal is orthogonal to the Triangle
        assertEquals(0, result.dotProduct(new Vector(1, -1, 0)), DELTA, "Triangle's normal is not orthogonal to the first vector");
        assertEquals(0, result.dotProduct(new Vector(0, 1, -1)), DELTA, "Triangle's normal is not orthogonal to the second vector");

    }


    /**
     * Test method for {@link geometries.Triangle#findIntersections(primitives.Ray)}.
     */
    @Test
    void testfindIntersections() {
        Triangle triangle = new Triangle(new Point(0.2,0,0), new Point(2, 0, 0), new Point(0.2, 2, 0));

        // ============ Equivalence Partitions Tests ==============
        // TC01: Intersection point is inside the triangle
        Point p01 = new Point(0.5, 0.5,  -1);
        Vector vec01 = new Vector(0, 0, 1);
        List<Point> exp = List.of(new Point(0.5, 0.5, 0));

        final var result1 = triangle.findIntersections(new Ray(p01, vec01));
        assertNotNull(result1, "Can't be empty list");
        assertEquals(1, result1.size(), "Wrong number of points");
        assertEquals(exp, result1, "Incorrect cutoff value");

        // TC02: Intersection point is outside the triangle against the side
        p01 = new Point(-0.5, 0.5,  -1);
        assertNull(triangle.findIntersections(new Ray(p01, vec01)), "There should be 0 intersection points");

        // TC03: Intersection point is outside the triangle against the vertex
        p01 = new Point(-0.5, -0.5,  -1);
        assertNull(triangle.findIntersections(new Ray(p01, vec01)), "There should be 0 intersection points");

        // =============== Boundary Values Tests ==================

        // TC04: Intersection point is on the vertex
        assertNull(triangle.findIntersections(new Ray(new Point(2, 0, 0), vec01)), "There should be 0 intersection points");


        // TC05: Intersection point is on the side
        p01 = new Point(1, 0,  0);
        assertNull(triangle.findIntersections(new Ray(p01, vec01)), "There should be 0 intersection points");


        // TC06: Intersection point is further along the edge
        p01 = new Point(3, 0,  0);
        assertNull(triangle.findIntersections(new Ray(p01, vec01)), "There should be 0 intersection points");

    }
}
