package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for geometries.Tube class
 * @authors alon greenstein, chaim fox
 */

public class TubeTests {

    /**
     * Delta value for accuracy when comparing the numbers of type 'double' in
     * assertEquals
     */
    private final double DELTA = 0.000001;

    /**
     * Test method for {@link geometries.Tube#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============
        Point head = new Point(1, 1, 0);
        Vector direction = new Vector(5,0,0);
        Ray axis = new Ray(head,direction);
        Tube tube = new Tube(1,axis);
        Point onSurface = new Point(5,0,0);
        Vector result = tube.getNormal(onSurface);

        // TC01: test if the normal is correct
        Vector normal = new Vector(0,-1,0);
        assertEquals(normal, result, "Tube's normal is not the correct normal");

        // =============== Boundary Values Tests ==================
        onSurface = new Point(1,0,0);
        result = tube.getNormal(onSurface);

        // TC02: test if the normal is correct when orthogonal's
        assertEquals(normal, result, "Tube's normal is not the correct normal when orthogonal's");
    }
}
