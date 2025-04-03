package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for primitives.Vector class
 *
 * @author alon greenstein, chaim fox
 */

class VectorTests {

    /**
     * Delta value for accuracy when comparing the numbers of type 'double' in
     * assertEquals
     */
    private static final double DELTA = 0.000001;

    /**
     * Test method for {@link primitives.Vector#Vector(double, double, double)}.
     */
    @Test
    void testCtorThreePoints() {
        // ============ Equivalence Partitions Tests ==============
        Vector vec = new Vector(1, 2, 3);

        // TC01: ensure that the parameters are set correctly
        assertEquals(1, vec.xyz.d1, DELTA, "Vector constructor wrong result");
        assertEquals(2, vec.xyz.d2, DELTA, "Vector constructor wrong result");
        assertEquals(3, vec.xyz.d3, DELTA, "Vector constructor wrong result");

        // =============== Boundary Values Tests ==================
        // TC02: ensure that if the vector is the zero vector an exception is thrown
        assertThrows(IllegalArgumentException.class, () -> new Vector(0, 0, 0), "Vector constructor does not throw exception for zero vector");
    }


    /**
     * Test method for {@link primitives.Vector#Vector(primitives.Double3)}.
     */
    @Test
    void testCtorDouble3() {
        // ============ Equivalence Partitions Tests ==============
        Double3 xyz = new Double3(1, 2, 3);
        Vector vec = new Vector(xyz);

        // TC01: ensure that the parameters are set correctly
        assertEquals(1, vec.xyz.d1, DELTA, "Vector constructor wrong result");
        assertEquals(2, vec.xyz.d2, DELTA, "Vector constructor wrong result");
        assertEquals(3, vec.xyz.d3, DELTA, "Vector constructor wrong result");

        // =============== Boundary Values Tests ==================
        // TC02: ensure that if the vector is the zero vector an exception is thrown
        assertThrows(IllegalArgumentException.class, () -> new Vector(Double3.ZERO), "Vector constructor does not throw exception for zero vector");
    }


    /**
     * Test method for {@link primitives.Vector#length}.
     */
    @Test
    void testLength() {
        // ============ Equivalence Partitions Tests ==============
        Vector vec = new Vector(3, 4, 0);

        // TC01: ensure that the length in positive vector is calculated correctly
        assertEquals(5.0, vec.length(), DELTA, "length() wrong result in positive vector");
        vec = new Vector(-3, -4, 0);

        // TC02: ensure that the length in negative vector is calculated correctly
        assertEquals(5.0, vec.length(), DELTA, "length() wrong result in negative vector");
    }


    /**
     * Test method for {@link primitives.Vector#lengthSquared}.
     */
    @Test
    void testLengthSquared() {
        // ============ Equivalence Partitions Tests ==============
        Vector vec = new Vector(3, 4, 0);

        // TC01: ensure that the length squared in positive vector is calculated correctly
        assertEquals(25.0, vec.lengthSquared(), DELTA, "lengthSquared() wrong result in positive vector");
        vec = new Vector(-3, -4, 0);

        // TC02: ensure that the length squared in negative vector is calculated correctly
        assertEquals(25.0, vec.lengthSquared(), DELTA, "lengthSquared() wrong result in negative vector");
    }

    /**
     * Test method for {@link primitives.Vector#add(primitives.Vector)}.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============
        Vector vec1 = new Vector(1, 0, 0);
        Vector vec2 = new Vector(1, 1, 0);

        // TC01: ensure that the addition of two vectors is calculated correctly
        assertEquals(new Vector(2, 1, 0), vec1.add(vec2), "add() wrong result");

        // =============== Boundary Values Tests ==================
        final Vector vec3 = new Vector(-1, 0, 0);

        // TC2: test if add for same size with opposite directions throw an exception of zero vector
        assertThrows(IllegalArgumentException.class, () -> vec1.add(vec3),
                "add() for opposite directions case doesn't throw an exception");
    }


    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============
        final Vector vec1 = new Vector(1, 1, 0);
        final Vector vec2 = new Vector(1, 0, 0);

        // TC01: ensure that the subtraction of two vectors is calculated correctly
        assertEquals(new Vector(0, 1, 0), vec1.subtract(vec2), "subtract() wrong result");

        // =============== Boundary Values Tests ==================

        // TC2: test if subtraction for same size and same directions vectors throw an exception of zero vector
        assertThrows(IllegalArgumentException.class, () -> vec1.subtract(vec1),
                "subtract() for same size and same directions vectors doesn't throw an exception");

    }


    /**
     * Test method for {@link primitives.Vector#scale(double)}.
     */
    @Test
    void testScale() {
        // ============ Equivalence Partitions Tests ==============
        Vector vec1 = new Vector(2, 1, 0);

        // TC01: ensure that the scaling of a vector is calculated correctly
        assertEquals(new Vector(-7, -3.5, 0), vec1.scale(-3.5), "scale() wrong result");

        // =============== Boundary Values Tests ==================


        // TC02: test if scale of vector by 0 throw an exception of zero vector
        assertThrows(IllegalArgumentException.class, () -> vec1.scale(0),
                "scale() of vector by 0 doesn't throw an exception");

    }


    /**
     * Test method for {@link primitives.Vector#dotProduct(primitives.Vector)}.
     */
    @Test
    void testDotProduct() {
        // ============ Equivalence Partitions Tests ==============
        Vector vec1 = new Vector(1, 2, 3);
        Vector vec2 = new Vector(3, 2, 1);

        // TC01: ensure that the dot product of two vectors is calculated correctly
        assertEquals(10.0, vec1.dotProduct(vec2), DELTA, "dotProduct() wrong result");

        // =============== Boundary Values Tests ==================
        vec1 = new Vector(4, 0, 0);
        vec2 = new Vector(0, 3, 0);

        // TC02: ensure that the dot product of two orthogonal vectors is 0
        assertEquals(0, vec1.dotProduct(vec2), DELTA, "dotProduct() wrong result in orthogonal vectors");
        vec1 = new Vector(1, 0, 0);
        vec2 = new Vector(3, 4, 5);

        // TC03: ensure that the dot product of unit vector is calculated correctly
        assertEquals(3.0, vec1.dotProduct(vec2), DELTA, "dotProduct() wrong result in unit vector");
    }


    /**
     * Test method for {@link primitives.Vector#crossProduct(primitives.Vector)}.
     */
    @Test
    void testCrossProduct() {
        // ============ Equivalence Partitions Tests ==============
        Vector vec1 = new Vector(1, 2, 3);
        Vector vec2 = new Vector(3, 2, 1);

        // TC01: ensure that the cross product of two vectors is calculated correctly
        assertEquals(new Vector(-4, 8, -4), vec1.crossProduct(vec2), "crossProduct() wrong result");

        // =============== Boundary Values Tests ==================
        final Vector vec3 = new Vector(4, 0, 0);
        final Vector vec4 = new Vector(1, 0, 0);

        // TC02: test if crossProduct() for parallel vectors throw an exception of zero vector
        assertThrows(IllegalArgumentException.class, () -> vec3.crossProduct(vec4),
                "crossProduct() for parallel vectors doesn't throw an exception");

        vec2 = new Vector(0, 2, 0);

        // TC03: ensure that the cross product of two orthogonal vectors is calculated correctly
        assertEquals(new Vector(0, 0, 8), vec3.crossProduct(vec2), "crossProduct() wrong result in orthogonal vectors case");
    }


    /**
     * Test method for {@link primitives.Vector#normalize}.
     */
    @Test
    void testNormalize() {
        // ============ Equivalence Partitions Tests ==============
        Vector vec1 = new Vector(3, 4, 0).normalize();

        // TC01: ensure that the normalization of a vector gives a unit vector
        assertEquals(1.0, vec1.length(), DELTA, "normalize() did not produce a unit vector");
        Vector vec2 = new Vector(4.5, 6, 0).normalize();

        // TC02: ensure that the normalization of two vectors in the same diraction gives the same vector
        assertEquals(vec1, vec2, "normalize() did not produce the same vector for different lengths");

    }
}