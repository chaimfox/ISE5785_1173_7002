package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for geometries.Sphere class
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
        Sphere sphere = new Sphere(center,5);
        Point onSurface = new Point(6, 1, 1);
        Vector result = sphere.getNormal(onSurface);

        Vector normal = new Vector(1,0,0);
        // tc01: test if the normal is correct
        assertEquals(normal, result, "Sphere's normal is not the correct normal");
    }
}
