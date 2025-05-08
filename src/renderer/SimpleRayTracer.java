package renderer;

import primitives.*;
import scene.Scene;

/**
 * A simple implementation of the RayTracerBase class. This class provides basic
 * ray tracing functionality for a 3D scene.
 */
public class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructs a new SimpleRayTracer with the specified scene.
     *
     * @param scene The scene to be rendered.
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Traces a ray through the scene, computes the color at the intersection point
     * (if any), and returns the background color of the scene if no intersection is
     * found.
     *
     * @param ray The ray to trace through the scene.
     * @return The color at the intersection point or the background color if no
     *         intersection is found.
     */
    @Override
    public Color traceRay(Ray ray) {
        // Find intersections of the ray with the scene geometries
        var intersections = scene.geometries.findIntersections(ray);

        // If no intersections found, return the background color of the scene
        return intersections == null ? scene.background //
                : calcColor(ray.findClosestPoint(intersections));
    }

    /**
     * Private method to calculate the color at a given point in the scene. For now,
     * returns the ambient light intensity of the scene.
     *
     * @param closestPoint a point to find its color
     * @return The color at the specified point.
     */
    private Color calcColor(Point closestPoint) {
        return scene.ambientLight.getIntensity(); // Return ambient light intensity for now
    }
}