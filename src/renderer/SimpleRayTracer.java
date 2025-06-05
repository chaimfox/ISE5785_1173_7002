package renderer;


import geometries.Intersectable.Intersection;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * SimpleRayTracer class is the basic class for ray tracing
 */
public class SimpleRayTracer extends RayTracerBase {

    private static final double DELTA = 0.1;
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    private static final double MIN_CALC_COLOR_K = 0.001;
    private static final Double3 INITIAL_K = Double3.ONE;


    /**
     * Constructor to initialize the scene
     *
     * @param scene the scene to be rendered
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Finds the closest intersection of a ray with objects in the scene
     *
     * @param ray The ray to trace
     * @return The closest intersection or null if none exists
     */
    private Intersection findClosestIntersection(Ray ray) {
        try {
            List<Intersection> intersections = scene.geometries.calculateIntersections(ray);
            if (intersections == null) {
                return null;
            }
            return ray.findClosestIntersection(intersections);
        } catch (IllegalArgumentException e) {
            // Handle zero vector case that can occur during intersection calculations
            return null;
        }
    }

     /**
     * Calculates the local lighting effects (diffuse and specular) at the intersection point,
     * with partial shadow support using transparency factor (ktr), according to stage 7 - part G.
     *
     * @param intersection the intersection data
     * @return the color at the point with local lighting effects and transparency-aware shadows
     */
    private Color calcColorLocalEffects(Intersection intersection) {
        Color color = intersection.geometry.getEmission(); // Start with emission color

        for (LightSource lightSource : scene.lights) {
            if (!setLightSource(intersection, lightSource)) {
                continue;
            }
            Double3 ktr = transparency(intersection);
            if (ktr.equals(Double3.ZERO)) {
                continue;
            }
            Color lightIntensity = lightSource.getIntensity(intersection.point).scale(ktr);

            Double3 diffuse = calcDiffusive(intersection);
            Double3 specular = calcSpecular(intersection);
            color = color.add(lightIntensity.scale(diffuse.add(specular)));
        }
        return color;
    }


    /**
     * Calculates the final color at the intersection point,
     * including ambient light and local lighting effects.
     *
     * @param intersection the intersection point
     * @param ray          the ray that caused the intersection
     * @return the resulting color at the point
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        if (!preprocessIntersection(intersection, ray.getDirection())) {
            return Color.BLACK;
        }
        return calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K);
    }


    private Color calcColor(Intersection intersection, int level, Double3 k) {
        if (alignZero(intersection.geometry.getNormal(intersection.point).dotProduct(intersection.v)) == 0)
            return Color.BLACK;

        Color color = calcColorLocalEffects(intersection);
        return 1 == level ? color : color.add(calcGlobalEffects(intersection, level, k));
    }

    /**
     * Calculates a single global effect (reflection or transparency) for a secondary ray.
     *
     * @param secondaryRay The secondary ray (reflection or transparency)
     * @param level        The recursion level
     * @param k            The accumulated attenuation factor
     * @param kEffect      The attenuation coefficient for this effect (kR or kT)
     * @return The color contribution from this global effect
     */
    private Color calcGlobalEffect(Ray secondaryRay, int level, Double3 k, Double3 kEffect) {
        Intersection intersection = findClosestIntersection(secondaryRay);
        if (intersection == null) {
            return Color.BLACK;
        }
        if (!preprocessIntersection(intersection, secondaryRay.getDirection())) {
            return Color.BLACK;
        }
        Color effectColor = calcColor(intersection, level - 1, k.product(kEffect));
        return effectColor.scale(kEffect);
    }


    /**
     * Calculates the global effects (reflection and transparency) at an intersection.
     *
     * @param intersection The intersection point
     * @param level        The recursion level
     * @param k            The accumulated attenuation factor
     * @return The combined color from global effects (transparency and reflection)
     */
    private Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
        Color color = intersection.geometry.getEmission();
        if (level == 1 || k.lowerThan(MIN_CALC_COLOR_K)) {
            return Color.BLACK;
        }

        if (scene.ambientLight.getIntensity() != null) {
            color = color.add(scene.ambientLight.getIntensity().scale(intersection.material.kA));
        }

        Vector v = intersection.v;
        Vector n = intersection.normal;
        Point point = intersection.point;
        double nv = alignZero(n.dotProduct(v));

        Double3 kR = intersection.material.kR;
        if (!kR.equals(Double3.ZERO)) {
            Vector r = v.subtract(n.scale(nv * 2));
            Ray reflectedRay = new Ray(point, r, n);
            color = color.add(calcGlobalEffect(reflectedRay, level, k, kR));
        }

        Double3 kT = intersection.material.kT;
        if (!kT.equals(Double3.ZERO)) {
            Ray reflectedRay = new Ray(point, v, n);
            color = color.add(calcGlobalEffect(reflectedRay, level, k, kT));
        }

        return color;
    }


    /**
     * Traces the ray and returns the color of the point
     *
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
     * @param v            the direction vector of the ray
     * @return true if the dot product is not zero, false otherwise
     */
    private boolean preprocessIntersection(Intersection intersection, Vector v) {
        intersection.v = v.normalize();
        intersection.normal = intersection.geometry.getNormal(intersection.point);
        intersection.vNormal = alignZero(v.dotProduct(intersection.normal));
        return !isZero(intersection.vNormal);
    }

    /**
     * Sets the light source details for the intersection,
     * including the light vector, light source reference, and dot product.
     *
     * @param intersection the intersection data to be updated
     * @param light        the light source affecting the point
     * @return true if both dot products have the same sign, false otherwise
     */
    private boolean setLightSource(Intersection intersection, LightSource light) {
        intersection.light = light;
        intersection.l = light.getL(intersection.point).normalize();
        intersection.lNormal = alignZero(intersection.l.dotProduct(intersection.normal));
        return intersection.lNormal * intersection.vNormal > 0;
    }


    /**
     * Calculates the specular component of the lighting at the intersection.
     *
     * @param intersection the intersection data
     * @return the specular reflection value
     */
    private Double3 calcSpecular(Intersection intersection) {
        Vector nDirection = intersection.v.scale(-1.0);
        double refraction = nDirection.dotProduct(calcReflection(intersection));
        double factor = Math.pow(refraction <= 0 ? 0 : refraction, intersection.material.nShininess);
        return intersection.material.kS.scale(factor);
    }

    /**
     * Calculates the diffusive component of the lighting at the intersection.
     *
     * @param intersection the intersection data
     * @return the diffusive reflection value
     */
    private Double3 calcDiffusive(Intersection intersection) {
        Double3 res = intersection.material.kD.scale(intersection.lNormal);
        double q1 = res.d1() < 0 ? -res.d1() : res.d1();
        double q2 = res.d2() < 0 ? -res.d2() : res.d2();
        double q3 = res.d3() < 0 ? -res.d3() : res.d3();
        return new Double3(q1, q2, q3);
    }


    /**
     * Computes the accumulated transparency factor (ktr) between a point and its light source.
     * This is used for partial shadow calculation: if an object between the point and light is semi-transparent,
     * it contributes partially to the shadow.
     *
     * @param intersection the intersection point for which transparency is evaluated
     * @return a Double3 representing how much light passes through (1 = full light, 0 = full shadow)
     */
    private Double3 transparency(Intersection intersection) {
        Vector lightDir = intersection.l.scale(-1.0);
        Vector offset = intersection.normal.scale(intersection.vNormal < 0 ? DELTA : -DELTA);
        Point shadowOrigin = intersection.point.add(offset);
        Ray shadowRay = new Ray(shadowOrigin, lightDir);

        double lightDistance = intersection.light.getDistance(intersection.point);
        List<Intersection> shadowIntersections = scene.geometries.calculateIntersections(shadowRay);

        Double3 ktr = Double3.ONE;
        if (shadowIntersections == null) return ktr;

        for (Intersection s : shadowIntersections) {
            if (s.point.distance(intersection.point) < lightDistance) {
                ktr = ktr.product(s.material.kT);
                if (ktr.lowerThan(MIN_CALC_COLOR_K)) {
                    return Double3.ZERO;
                }
            }
        }

        return ktr;
    }


    /**
     * Calculates the reflection vector based on the intersection data.
     * This is used for specular reflection calculations.
     *
     * @param intersection the intersection data containing the normal and light vector
     * @return the reflection vector
     */
    private Vector calcReflection(Intersection intersection) {
        Vector normal = intersection.normal;
        return intersection.l.add((normal.scale(intersection.l.dotProduct(normal)).scale(-2.0)));
    }

}
