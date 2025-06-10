package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Util;
import primitives.Vector;
import java.util.Random;

/**
 * PointLight class represents a light source with a specific position in the scene
 */
public class PointLight extends Light implements LightSource{
    /**
     * position of the light source
     */
    protected final Point position;
    private double kC = 1d;
    private double kL = 0d;
    private double kQ = 0d;

    // Soft shadow sampling parameters
    /** Radius of the circular area light (for area soft shadows). */
    private double radius = 0.0;
    /** Number of shadow-ray samples per shading point. */
    private int numSamples = 1;
    /** Random generator for jittering sample points on the disk. */
    private final Random random = new Random();

    /**
     * get intensity of the light at a specific point
     * @param color color of the light
     * @param position position of the light source
     */
    public PointLight(Color color, Point position) {
        super(color);
        this.position = position;
    }

    /**
     * get intensity of the light at a specific point
     * @param kC attenuation factor
     * @return intensity of the light at a specific point
     */
    public PointLight setKc(double kC) {
        this.kC = kC;
        return this;
    }

    /**
     * get intensity of the light at a specific point
     * @param kL attenuation factor
     * @return intensity of the light at a specific point
     */
    public PointLight setKl(double kL) {
        this.kL = kL;
        return this;
    }

    /**
     * get intensity of the light at a specific point
     * @param kQ attenuation factor
     * @return intensity of the light at a specific point
     */
    public PointLight setKq(double kQ) {
        this.kQ = kQ;
        return this;
    }

    /**
     * Sets the circular area radius for soft shadows.
     * Radius must be non-negative. A radius of zero yields a point-light.
     *
     * @param radius radius of the light disk
     * @return this PointLight for chaining
     */
    public PointLight setRadius(double radius) {
        this.radius = Math.max(0, radius);
        return this;
    }

    /**
     * Sets the number of samples for area soft shadows (>= 1).
     * More samples yield smoother shadows at higher cost.
     *
     * @param numSamples number of jittered shadow rays
     * @return this PointLight for chaining
     */
    public PointLight setNumSamples(int numSamples) {
        this.numSamples = Math.max(1, numSamples);
        return this;
    }

    /**
     * Returns the current area light radius.
     *
     * @return the radius of the light disk (0 for a point light)
     */
    public double getRadius() { return radius; }


    /**
     * Returns the number of samples for soft shadows.
     *
     * @return number of shadow-ray samples per shading point
     */
    public int getNumSamples() { return numSamples; }

    /**
     * get intensity of the light at a specific point
     * @param p point to which the intensity is calculated
     * @return the intensity
     */
    @Override
    public Color getIntensity(Point p) {
        double d = position.distance(p);
        double factor = kC + kL * d + kQ * d * d;
        if(Util.isZero(factor))
            return intensity.scale(Double.POSITIVE_INFINITY);

        return intensity.scale(1d/factor);
    }

    /**
     * get direction of the light at a specific point
     * @param p point to which the vector is calculated
     * @return vector from the light source to the point
     */
    @Override
    public Vector getL(Point p) {
        return p.subtract(position).normalize();
    }

    /**
     * get distance from the light source to a point
     * @param point the point to which the distance is calculated
     * @return the distance from the light source to the point
     */
    @Override
    public double getDistance(Point point) {
        return position.distance(point);
    }


    /**
     * Samples a random point on the circular disk around this light for soft shadows.
     * The disk lies in a plane orthogonal to vector (p - position).
     *
     * @param p shading point to cast shadows from
     * @return a jittered sample point on the area light disk
     */
    public Point getSamplePoint(Point p) {
        // Direction from light center to shading point
        Vector toP = p.subtract(position).normalize();
        // Choose arbitrary up vector not parallel to toP
        Vector up = Math.abs(toP.getX()) < 1e-6 && Math.abs(toP.getZ()) < 1e-6
                ? new Vector(1, 0, 0) : new Vector(0, 1, 0);
        // Build orthonormal basis (u, v) on plane of the disk
        Vector u = toP.crossProduct(up).normalize();
        Vector v = toP.crossProduct(u).normalize();

        // Generate random point in unit circle (uniform distribution)
        double r = Math.sqrt(random.nextDouble()) * radius;
        double theta = 2 * Math.PI * random.nextDouble();
        double xOff = r * Math.cos(theta);
        double yOff = r * Math.sin(theta);

        // Map to disk around position
        return position.add(u.scale(xOff)).add(v.scale(yOff));
    }




}