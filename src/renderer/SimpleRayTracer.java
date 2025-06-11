package renderer;


import geometries.Intersectable.Intersection;
import lighting.*;
import primitives.*;
import scene.Scene;

import java.util.LinkedList;
import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * SimpleRayTracer class is the basic class for ray tracing
 */
public class SimpleRayTracer extends RayTracerBase {

    private static final double DELTA = 0.1;
    /** Shadow‐ray bias to prevent acne. */
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    private static final double MIN_CALC_COLOR_K = 0.001;
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * Indicates whether soft shadows are enabled.
     * Soft shadows create a more realistic lighting effect by simulating
     * the gradual transition between light and shadow.
     */
    private boolean softShadows =  false;// Default setting for soft shadows

    /**
     * The resolution of the grid used for soft shadow calculations.
     * A higher resolution results in smoother shadows but increases computation time.
     */
    private int gridResolution = 5;

    /**
     * Constructor to initialize the scene
     *
     * @param scene the scene to be rendered
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Sets whether soft shadows should be enabled or disabled.
     * Soft shadows create a more realistic lighting effect by simulating
     * the gradual transition between light and shadow.
     *
     * @param softShadows true to enable soft shadows, false to disable them
     * @return the current instance of SimpleRayTracer for method chaining
     */
    public SimpleRayTracer setSoftShadows(boolean softShadows) {
        this.softShadows = softShadows;
        return this;
    }

    /**
     * Sets the resolution of the grid used for soft shadow calculations.
     * A higher resolution results in smoother shadows but increases computation time.
     *
     * @param grid the resolution of the grid (number of subdivisions)
     * @return the current instance of SimpleRayTracer for method chaining
     */
    public SimpleRayTracer setGridResolution(int grid) {
        this.gridResolution = grid;
        return this;
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

         if (intersection == null) {
             return scene.background;
         }
         //  Double3 k = new Double3(kt);
         double nv = alignZero(intersection.vNormal);/////????
         if (nv == 0) return Color.BLACK; // No contribution if the normal and ray direction are perpendicular
         Color color = intersection.geometry.getEmission();
         for (LightSource lightSource : scene.lights) {
             List<Vector> vectorsL = new LinkedList<>();
             // Add the light source vector
             setLightSource(intersection, lightSource);
             vectorsL.add(lightSource.getL(intersection.point));


             // Generate a grid of vectors from the light source
             if (softShadows && lightSource instanceof PointLight pointLight) {
                 vectorsL.addAll(pointLight.getLs(intersection.point, gridResolution));
             }

             Color colorBeam = Color.BLACK;
             for (Vector l : vectorsL) {
                 double nl = alignZero(intersection.normal.dotProduct(l));
                 if (nv * nl > 0) {
                     Double3 ktr = transparency(intersection, l);
                     if (ktr.lowerThan(MIN_CALC_COLOR_K)) continue;//////?????
                     Color iL = lightSource.getIntensity(intersection.point);
//
//                    colorBeam = colorBeam.add(  iL.scale(  (calcDiffusive(intersection, nl)  ).add(calcSpecular(intersection, l, nl))).scale(ktr));
                     // colorBeam = colorBeam.add(iL.scale(calcDiffusive(intersection,nl))).add(iL.scale(calcSpecular(intersection,l,nl)));

                     Color d = iL.scale(calcDiffusive(intersection, nl));
                     Color s = iL.scale(calcSpecular(intersection, l, nl));
                     colorBeam = colorBeam.add(d.add(s));
                     colorBeam = colorBeam.scale(ktr);
                 }
             }
             if (softShadows)
                 color = color.add(colorBeam.reduce(vectorsL.size()));
             else
                 color = color.add(colorBeam);
         }
         return color;
     }


    /**
     * Calculates the color at the intersection point.
     *
     * @param intersection the intersection object
     * @param ray          the ray that hit the object
     * @return the color at the intersection point
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        if (!preprocessIntersection(intersection, ray.getDirection())) return Color.BLACK;
        Color color = scene.ambientLight.getIntensity().scale(intersection.material.kA);
        color = color.add(calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K.d1()));
        return color;
    }



    /**
     * Calculates the color at the intersection point, including local and global effects.
     *
     * @param intersection the intersection object
     * @param level        the current recursion level
     * @param k            the contribution factor
     * @return the color at the intersection point
     */
    private Color calcColor(Intersection intersection, int level, double k) {
        Color color = calcColorLocalEffects(intersection);
        return 1 == level ? color : color.add(calcGlobalEffects(intersection, level, new Double3(k)));
    }

    /**
     * Calculates the global lighting effect for a given ray.
     *
     * @param ray  the ray to trace
     * @param level the current recursion level
     * @param k    the contribution factor
     * @param kx   the material's reflection or refraction coefficient
     * @return the global lighting color contribution
     */
    private Color calcColorGlobalEffect(Ray ray, int level, Double3 k, Double3 kx) {
        Double3 kkx = k.product(kx);
        if (kkx.lowerThan(MIN_CALC_COLOR_K)) return Color.BLACK;
        Intersection intersection = findClosestIntersection(ray);
        if (intersection == null) return scene.background.scale(kx);
        return preprocessIntersection(intersection, ray.getDirection())
                ? calcColor(intersection, level - 1, kkx.d1()).scale(kx) : Color.BLACK;
    }


    /**
     * Calculates the global lighting effects (reflection and refraction) at the intersection point.
     *
     * @param intersection the intersection object
     * @param level        the current recursion level
     * @param k            the contribution factor
     * @return the global lighting color contribution
     */
    private Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
        return calcColorGlobalEffect(constractRefractedRay(intersection),
                level, k, intersection.material.kT)
                .add(calcColorGlobalEffect(constractReflectedRay(intersection),
                        level, k, intersection.material.kR));
    }



    /**
     * Traces a ray through the scene and returns the color at the closest intersection point.
     * If no intersections are found, the background color is returned.
     *
     * @param ray the ray to trace
     * @return the color at the closest intersection point, or the background color if no intersections are found
     */
    @Override
    public Color traceRay(Ray ray) {
        var closestPoint = findClosestIntersection(ray);
        return closestPoint == null ? scene.background : calcColor(closestPoint, ray);
    }

    /**
     * Preprocesses the intersection data by setting the normal, ray direction, and dot product.
     *
     * @param intersection the intersection object
     * @param rayDir       the direction of the ray
     * @return true if the intersection is valid, false otherwise
     */
    public boolean preprocessIntersection(Intersection intersection, Vector rayDir) {
//        intersection.rayDir = rayDir.scale(-1);
        intersection.v = rayDir;
        intersection.normal = intersection.geometry.getNormal(intersection.point);
//        intersection.scaleNR = alignZero(intersection.rayDir.dotProduct(intersection.normal));
        intersection.vNormal = alignZero(intersection.normal.dotProduct(intersection.v));
        return !isZero(intersection.vNormal) && intersection.material != null;

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
     * Calculates the specular component of the light at the intersection point.
     * According to the Phong model: kS * (R·V)^nShininess.
     *
     * @param intersection the intersection data
     * @param l the light direction vector
     * @param nl the dot product of the normal vector and the light direction vector
     * @return the specular component as Double3
     */
    private Double3 calcSpecular(Intersection intersection, Vector l ,double nl ) {
        Vector n = intersection.normal.normalize();
        Vector v = intersection.v.normalize(); // inverse of ray direction

        // Calculate reflection vector R = L - 2 * (N·L) * N
        Vector r = l.subtract(n.scale(2 * nl)).normalize();

        // Calculate R·V (viewer direction)
        double rv = r.dotProduct(v) * -1;
        if (rv <= 0)
            return Double3.ZERO; // no specular if angle > 90 degrees

        // Calculate specular component: kS * (R·V)^nShininess
        return intersection.material.kS.scale(Math.pow(rv, intersection.material.nShininess));
    }

    /**
     * Calculates the diffusive component of the light at the intersection point.
     * According to the Phong model: kD * max(0, N·L).
     *
     * @param intersection the intersection data
     * @param nl the dot product of the normal vector and the light direction vector
     * @return the diffusive component as Double3
     */
    private Double3 calcDiffusive(Intersection intersection, double nl ) {
        // According to Phong model: kD * max(0, N·L)
        if (nl < 0) {
            return intersection.material.kD.scale(nl * -1);
        }
        return intersection.material.kD.scale(nl);
    }


    /**
     * Calculates the transparency factor for the intersection point.
     * This is done by casting a shadow ray and checking for intersections with other geometries.
     *
     * @param intersection the intersection object
     * @param vector the direction vector from the intersection point to the light source
     * @return the transparency factor as Double3
     */
    private Double3 transparency(Intersection intersection, Vector vector) {
        Vector lightDirection = vector.scale(-1);/////////// -1
        Vector delta = intersection.normal.scale(intersection.vNormal < 0 ? DELTA : -DELTA);
        Ray shadowRay = new Ray(intersection.point.add(delta), lightDirection, intersection.normal);
        List<Intersection> shadowIntersections = scene.geometries.calculateIntersections(shadowRay);

        if (shadowIntersections == null) return Double3.ONE;

        Double3 ktr = Double3.ONE;
        double lightDistance = intersection.light.getDistance(intersection.point);

        for (Intersection hit : shadowIntersections) {
            if (alignZero(hit.point.distance(intersection.point) - lightDistance) < 0) {
                ktr = ktr.product(hit.geometry.getMaterial().kT);
                if (ktr.lowerThan(MIN_CALC_COLOR_K)) return Double3.ZERO;
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

    /**
     * Constructs a refracted ray for the given intersection point.
     *
     * @param intersection the intersection object
     * @return the refracted ray
     */
    private Ray constractRefractedRay(Intersection intersection) {
        return new Ray(intersection.point, intersection.v, intersection.geometry.getNormal(intersection.point));
    }

    /**
     * Constructs a reflected ray for the given intersection point.
     *
     * @param intersection the intersection object
     * @return the reflected ray originating from the intersection point
     */
    private Ray constractReflectedRay(Intersection intersection) {
        Vector v = intersection.v;
        Vector n = intersection.geometry.getNormal(intersection.point);
        double vn = v.dotProduct(n);
        return new Ray(intersection.point, v.subtract(n.scale(2 * vn)), n);
    }


}
