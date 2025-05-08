package lighting;

import primitives.*;

/**
 * Class representing ambient light in a scene. Ambient light is a global light
 * source that illuminates all objects equally, providing a base level of light
 * without direction.
 */
public class AmbientLight {

    /**
     * The intensity of the ambient light.
     */
    final private Color intensity;

    /**
     * A constant representing no ambient light (intensity is black).
     */
    public static AmbientLight NONE = new AmbientLight(Color.BLACK, 0);

    /**
     * Constructs an AmbientLight with a specified color and intensity coefficient.
     *
     * @param ia the color of the ambient light.
     * @param ka the intensity coefficient of the ambient light (as a Double3
     *           object).
     */
    public AmbientLight(Color ia, Double3 ka) {
        intensity = ia.scale(ka);
    }

    /**
     * Constructs an AmbientLight with a specified color and intensity coefficient.
     *
     * @param ia the color of the ambient light.
     * @param ka the intensity coefficient of the ambient light (as a double).
     */
    public AmbientLight(Color ia, double ka) {
        intensity = ia.scale(ka);
    }

    /**
     * Gets the intensity of the ambient light.
     *
     * @return the intensity of the ambient light.
     */
    public Color getIntensity() {
        return intensity;
    }
}