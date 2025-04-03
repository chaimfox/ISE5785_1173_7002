package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for geometries.Triangle class
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
        Triangle triangle = new Triangle(a,b,c);
        Vector result = triangle.getNormal(a);

        // TC01: ensure |result| = 1
        assertEquals(1, result.length(), DELTA, "Triangle's normal is not a unit vector");

        // TC02: test if the normal is orthogonal to the Triangle
        assertEquals(0,result.dotProduct(new Vector(1, -1, 0)), DELTA, "Triangle's normal is not orthogonal to the first vector");
        assertEquals(0,result.dotProduct(new Vector(0, 1, -1)), DELTA, "Triangle's normal is not orthogonal to the second vector");

    }

}
