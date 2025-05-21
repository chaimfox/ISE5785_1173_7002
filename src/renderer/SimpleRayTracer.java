package renderer;

import geometries.Intersectable.Intersection;
import primitives.Color;
import primitives.Ray;
import scene.Scene;

import java.util.List;

/**
 * SimpleRayTracer class is the basic class for ray tracing
 */
public class SimpleRayTracer extends RayTracerBase{
    /**
     * Constructor to initialize the scene
     * @param scene the scene to be rendered
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }


    /**
     * Calculates the color of the point
     * @param intersection the intersection point
     * @return the color of the point
     */
    private Color calcColor(Intersection intersection) {
        return scene.ambientLight.getIntensity().add(intersection.geometry.getEmission());
    }


    /**
     * Traces the ray and returns the color of the point
     * @param ray the ray to be traced
     * @return the color of the point
     */
    @Override
    public Color traceRay(Ray ray) {
        var intersections = scene.geometries.calculateIntersections(ray);
        if (intersections == null) {
            return scene.background;
        }
        return calcColor(ray.findClosestIntersection(intersections));
    }
}