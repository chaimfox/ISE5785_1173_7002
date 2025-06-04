package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * class for directional light
 */
public class DirectionalLight extends Light implements LightSource{
    private final Vector direction;

    /**
     * get intensity of the light at a specific point
     * @param color color of the light
     * @param direction direction of the light
     */
    public DirectionalLight(Color color, Vector direction) {
        super(color);
        this.direction = direction.normalize();
    }

    /**
     * get direction of the light at a specific point
     * @param p point to which the intensity is calculated
     * @return intensity of the light at a specific point
     */
    @Override
    public Vector getL(Point p) {
        return direction;
    }

    /**
     * get intensity of the light at a specific point
     * @param p point to which the intensity is calculated
     * @return intensity of the light at a specific point
     */
    @Override
    public Color getIntensity(Point p) {
        return intensity;
    }

    /**
     * get distance from the light source to a point
     * @param point the point to which the distance is calculated
     * @return the distance from the light source to the point
     */
    @Override
    public double getDistance(Point point) {
        return Double.POSITIVE_INFINITY;
    }
}