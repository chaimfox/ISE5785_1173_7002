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
     * Calculates the local lighting effects (diffuse and specular) at the intersection point.
     *
     * @param intersection the intersection data
     * @return the color at the point with local lighting applied
     */
    private Color calcColorLocalEffects(Intersection intersection) {
        Color color = intersection.geometry.getEmission();
        for (LightSource lightSource : scene.lights) {
            if (!setLightSource(intersection, lightSource) || !unshaded(intersection)) {
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
     Vector n = intersection.geometry.getNormal(intersection.point);
     Vector direction = ray.getDirection();

     Color color = intersection.geometry.getEmission();

     Point point = intersection.point;

     //store the values of the material
     int nShininess = intersection.geometry.getMaterial().nShininess;
     Double3 kD = intersection.geometry.getMaterial().kD;
     Double3 kS = intersection.geometry.getMaterial().kS;

     double nv = Util.alignZero(n.dotProduct(direction));
     if (nv == 0d) return Color.BLACK;

     // Calculate the color of the point by adding the diffusive and specular components
     for (var lightSource : scene.lights) {
     Vector l = lightSource.getL(point).normalize();
     double nl = n.dotProduct(l);

     if (nl * nv > 0d && unshaded(intersection, lightSource, l, n, nl)) {
     Color lightIntensity = lightSource.getIntensity(intersection.point);
     color = color.add(calcDiffusive(kD, nl, lightIntensity))
     .add(calcSpecular(kS, l, n, nl, direction, nShininess, lightIntensity));
     }
     }
     return color;
     */


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

        Color color = scene.ambientLight.getIntensity()
                .scale(intersection.geometry.getMaterial().kA).add(calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K));

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

        // Initialize the intersection with the secondary ray's direction
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
        // If the level of recursion is at base or the effect is negligible - stop recursion
        Color color = intersection.geometry.getEmission();
        if (level == 1 || k.lowerThan(MIN_CALC_COLOR_K)) {
            return Color.BLACK;
        }

        // Add ambient light contribution
        if (scene.ambientLight.getIntensity() != null) {
            color = color.add(scene.ambientLight.getIntensity().scale(intersection.material.kA));
        }

        Vector v = intersection.v;
        Vector n = intersection.normal;
        Point point = intersection.point;
        double nv = alignZero(n.dotProduct(v));

        // Calculate reflection contribution if material has reflection
        Double3 kR = intersection.material.kR;
        if (!kR.equals(Double3.ZERO)) {
            Vector r = v.subtract(n.scale(nv * 2));
            Ray reflectedRay = new Ray(point, r, n);
            color = color.add(calcGlobalEffect(reflectedRay, level, k, kR));
        }

        // Calculate transparency/refraction contribution if material has transparency
        Double3 kT = intersection.material.kT;
        if (!kT.equals(Double3.ZERO)) {
            // For transparency, ray continues in the same direction
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


    private boolean unshaded(Intersection intersection) {
        Vector lightDirection = intersection.l.scale(-1.0); // from point to light source
        Vector delta = intersection.normal.scale(intersection.lNormal < 0 ? DELTA : -DELTA);
        Ray lightRay = new Ray(intersection.point.add(delta), lightDirection);
        double lightDistance = intersection.light.getDistance(intersection.point);
        List<Intersection> intersections = scene.geometries.calculateIntersections(lightRay);

        if (intersections == null)
            return true;

        for (Intersection i : intersections) {
            if (i.material.kT.lowerThan(MIN_CALC_COLOR_K))
                return false;
        }
        return true;
    }

    private Vector calcReflection(Intersection intersection) {
        Vector normal = intersection.normal;
        return intersection.l.add((normal.scale(intersection.l.dotProduct(normal)).scale(-2.0)));
    }



//    private boolean unshaded(GeoPoint gp, LightSource lightSource, Vector l, Vector n, double nl) {
//        Ray lightRay = new Ray(gp.point, l.scale(-1), n);
//        List<GeoPoint> intersections = scene.geometries.findGeoIntersections(lightRay, lightSource.getDistance(gp.point));
//
//
//        if (intersections == null)
//            return true;
//
//        for(GeoPoint point : intersections){
//            if (point.geometry.getMaterial().kT.equals(Double3.ZERO))
//                return false;
//        }
//        return true;
//
//    }

}
