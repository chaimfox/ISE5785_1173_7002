package lighting;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * SpotLight class represents a light source with a specific position in the scene
 */
public class SpotLight extends PointLight{
    private final Vector direction;


    /**
     * get intensity of the light at a specific point
     * @param color color of the light
     * @param direction direction of the light
     * @param position position of the light source
     */
    public SpotLight(Color color, Point position, Vector direction) {
        super(color, position);
        this.direction = direction.normalize();
    }

    @Override
    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }

    @Override
    public SpotLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }

    @Override
    public SpotLight setKq(double kQ) {
        super.setKq(kQ);
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        Color oldColor = super.getIntensity(p);
        return oldColor.scale(Math.max(0d, direction.dotProduct(getL(p))));
    }
}