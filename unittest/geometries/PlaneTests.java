package geometries;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import primitives.*;

/**
 * Unit tests for geometries.Plane class
 * @authors alon greenstein, chaim fox
 */
public class PlaneTests {

    /**
     * Delta value for accuracy when comparing the numbers of type 'double' in
     * assertEquals
     */
    private final double DELTA = 0.000001;


    /**
     * Test method for {@link geometries.Plane#Plane(primitives.Point, primitives.Point, primitives.Point)}.
     */
    @Test
    void testCtorThreePoints() {

        // ============ Equivalence Partitions Tests ==============
        final Point a = new Point(0.5,2.3,3.4);
        final Point b = new Point(1.7,6.1,9);
        final Point c = new Point(5,2,3);
        Plane plane = new Plane(a,b,c);

        // TC01: ensure |normal| = 1
        assertEquals(1.0,plane.getNormal().length(),DELTA, "normal length doesn't equal to 1");

        Vector vec1 = a.subtract(b);
        Vector vec2 = b.subtract(c);

        // TC02: test if the normal is orthogonal to the plane
        assertEquals(0,vec1.dotProduct(plane.getNormal()),DELTA,"normal not orthogonal to the first vector");
        assertEquals(0,vec2.dotProduct(plane.getNormal()),DELTA,"normal not orthogonal to the second vector");


        // =============== Boundary Values Tests ==================

        // TC03: test if exception thrown in case that the first and second points are the same
        assertThrows(IllegalArgumentException.class, () -> new Plane(a,a,b),
                "first and second points the same and dose not throws exception");

        // TC04: test if exception thrown in case that the second and third points are the same
        assertThrows(IllegalArgumentException.class, () -> new Plane(a,b,b),
                "second and third points the same and dose not throws exception");

        // TC05: test if exception thrown in case that the first and third points are the same
        assertThrows(IllegalArgumentException.class, () -> new Plane(a,b,a),
                "first and third points the same and dose not throws exception");

        // TC06: test if exception thrown in case that all three points are the same
        assertThrows(IllegalArgumentException.class, () -> new Plane(a,a,a),
                "three points the same and dose not throws exception");

        // TC07: test if exception thrown in case that the three points are on the same line
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                new Point(1,1,1),new Point(2,2,2),new Point(3,3,3)),
                "three points on the same line dose not throw exception");
    }


    /**
     * Test method for {@link geometries.Plane#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {

        // ============ Equivalence Partitions Tests ==============
        final Point a = new Point(1, 0, 0);
        final Point b = new Point(0, 1, 0);
        final Point c = new Point(0, 0, 1);
        Plane plane = new Plane(a,b,c);
        Vector result = plane.getNormal(a);

        // TC01: ensure |result| = 1
        assertEquals(1, result.length(), DELTA, "Plane's normal is not a unit vector");

        // TC02: test if the normal is orthogonal to the plane
        assertEquals(0,result.dotProduct(new Vector(1, -1, 0)), DELTA, "Plane's normal is not orthogonal to the first vector");
        assertEquals(0,result.dotProduct(new Vector(0, 1, -1)), DELTA, "Plane's normal is not orthogonal to the second vector");


    }



}