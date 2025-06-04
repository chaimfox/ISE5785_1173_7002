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

    private static final double DELTA = 0.1;

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
            if(!setLightSource(intersection, lightSource) || !unshaded(intersection)){
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
    private boolean preprocessIntersection(Intersection intersection, Vector v) {
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
     * @param light the light source affecting the point
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


    private boolean unshaded(Intersection intersection) {
        Vector lightDirection = intersection.l.scale(-1); // from point to light source
        Vector delta = intersection.normal.scale(intersection.lNormal < 0 ? DELTA : -DELTA);
        Ray lightRay = new Ray(intersection.point.add(delta), lightDirection);
        List<Intersection> intersections = scene.geometries.calculateIntersections(lightRay);

        if (intersections == null)
            return true;

        for (Intersection inter : intersections) {
            if (inter.point.distance(intersection.point) < intersection.light.getDistance(intersection.point))
                return false;
        }
        return true;
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
