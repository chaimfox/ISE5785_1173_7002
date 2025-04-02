package geometries;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import primitives.*;

/**
 * Unit tests for geometries.Plane class
 * @authors alon greenstein, chaim fox
 */
public class PlaneTests {

    private final double DELTA = 0.000001;


    /**
     * Test method for {@link geometries.Plane#Plane(primitives.Point, primitives.Point, primitives.Point)}.
     */
    @Test
    void testCtorThreePoints() {
        // =============== Boundary Values Tests ==================

        // TC01: not throws exception from ctor
        assertThrows(IllegalArgumentException.class, () -> new Plane(
                new Point(0, 0, 1), new Point(0, 0, 1), new Point(1, 0, 0)), //
                "two point the same dose not throws exception");

        assertThrows(IllegalArgumentException.class,
                () -> new Plane(new Point(1, 1, 1), new Point(2, 2, 2), new Point(3, 3, 3)), //
                "three point are on the same line dose not throw exception");

    }


    /**
     * Test method for {@link geometries.Plane#Plane(primitives.Point, primitives.Vector)}.
     */
    @Test
    void testCtorPointVectorParam() {

    }




    /**
     * Test method for {@link geometries.Plane#getNormal()}.
     */
    @Test
    void testGetNormal() {

        // ============ Equivalence Partitions Tests ==============

        Plane plane = new Plane(new Point(0, 0, 1), new Point(0, 1, 0), new Point(1, 0, 0));
        Vector result = plane.getNormal(new Point(0, 0, 1));

        // TC01: ensure |result| = 1
        assertEquals(1, result.length(), DELTA, "Plane's normal is not a unit vector");
        // test if the normal is orthogonal to the plane
        assertEquals(0,result.dotProduct(new Vector(-1, 1, 0)), "Plane's normal is not orthogonal");
        assertEquals(0,result.dotProduct(new Vector(0, -1, 1)), "Plane's normal is not orthogonal");

    }


    /**
     * Test method for {@link geometries.Plane#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormalPointParam() {

    }


}