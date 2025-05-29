package renderer;

import geometries.Intersectable.Intersection;
import primitives.Color;
import primitives.Ray;
import scene.Scene;
import primitives.*;
import static primitives.Util.alignZero;
import lighting.LightSource;

import lighting.Light;
import primitives.*;
import java.util.List;
import static primitives.Util.alignZero;

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
     * Calculates the local lighting effects (diffuse and specular) at the intersection point.
     *
     * @param intersection the intersection data
     * @return the color at the point with local lighting applied
     */
    private Color calcColorLocalEffects(Intersection intersection) {
        Color color = intersection.geometry.getEmission();
        for (LightSource lightSource : scene.lights) {
            if(!setLightSource(intersection, lightSource)){
                continue;
            }
            color = color.add(
                    lightSource.getIntensity(intersection.point).scale(
                            calcDiffusive(intersection).add(calcSpecular(intersection))
                    )
            );
        }
        return color;
    }

    /**
     * Calculates the final color at the intersection point,
     * including ambient light and local lighting effects.
     *
     * @param intersection the intersection point
     * @param ray the ray that caused the intersection
     * @return the resulting color at the point
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        if (!preprocessIntersection(intersection, ray.getDirection())) {
            return Color.BLACK;
        }

        Color color = scene.ambientLight.getIntensity()
                .scale(intersection.geometry.getMaterial().kA);

        return color.add(calcColorLocalEffects(intersection));
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
        return calcColor(ray.findClosestIntersection(intersections), ray);
    }
    /**
     * Preprocesses the intersection by storing the normalized view vector,
     * the surface normal, and their dot product.
     *
     * @param intersection the intersection data to be updated
     * @param v the direction vector of the ray
     * @return true if the dot product is not zero, false otherwise
     */
    public boolean preprocessIntersection(Intersection intersection, Vector v) {
        intersection.v = v.normalize();
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        intersection.vNormal = alignZero(v.dotProduct(intersection.normal));
        return intersection.vNormal != 0;
    }

    /**
     * Sets the light source details for the intersection,
     * including the light vector, light source reference, and dot product.
     *
     * @param intersection the intersection data to be updated
     * @param lightSource the light source affecting the point
     * @return true if both dot products have the same sign, false otherwise
     */
    public boolean setLightSource(Intersection intersection, LightSource lightSource) {
        intersection.l = lightSource.getL(intersection.point).normalize();
        intersection.light = lightSource;
        intersection.lNormal = alignZero(intersection.l.dotProduct(intersection.normal));
        return intersection.lNormal * intersection.vNormal > 0;
    }



    /**
     * Calculates the specular component of the lighting at the intersection.
     *
     * @param intersection the intersection data
     * @return the specular reflection value
     */
    private Double3 calcSpecular(Intersection intersection){
        Vector r = intersection.l.subtract(intersection.normal.scale(2 * intersection.lNormal));
        return intersection.material.kS.scale(Math.pow(Math.max(0,alignZero(intersection.v.scale(-1).dotProduct(r))), intersection.material.nShininess));
    }

    /**
     * Calculates the diffusive component of the lighting at the intersection.
     *
     * @param intersection the intersection data
     * @return the diffusive reflection value
     */
    private Double3 calcDiffusive(Intersection intersection) {
        return intersection.material.kD.scale(Math.abs(intersection.lNormal));
    }

}
