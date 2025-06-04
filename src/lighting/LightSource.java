package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Interface LightSource is the basic interface representing a light source in the 3D space.
 */
public interface LightSource {
    /**
     * Getter for the intensity field.
     * @return the intensity of the light
     */
    Color getIntensity(Point p);

    /**
     * Getter for the vector from the light source to a p.
     * @param p the p to which the vector is calculated
     * @return the vector from the light source to the p
     */
    Vector getL(Point p);

    /**
     * Getter for the distance from the light source to a point.
     * @param point the point to which the distance is calculated
     * @return the distance from the light source to the point
     */
    double getDistance(Point point);
}