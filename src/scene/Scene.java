package scene;

import geometries.*;
import lighting.AmbientLight;
import primitives.*;

/**
 * Class representing a scene in a 3D graphics application. A scene consists of
 * a name, background color, ambient light, and geometries.
 */
public class Scene {

    /**
     * The name of the scene.
     */
    public final String name;

    /**
     * The background color of the scene. Default is black.
     */
    public Color background = Color.BLACK;

    /**
     * The ambient light of the scene. Default is no ambient light.
     */
    public AmbientLight ambientLight = AmbientLight.NONE;

    /**
     * The collection of geometries in the scene.
     */
    public Geometries geometries = new Geometries();

    /**
     * Constructs a new scene with the specified name.
     *
     * @param name the name of the scene.
     */
    public Scene(String name) {
        this.name = name;
    }

    /**
     * Sets the background color of the scene.
     *
     * @param background the background color.
     * @return the current Scene object (for method chaining).
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the ambient light of the scene.
     *
     * @param ambientLight the ambient light.
     * @return the current Scene object (for method chaining).
     */
    public Scene setAbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    /**
     * Sets the geometries of the scene.
     *
     * @param geometries the collection of geometries.
     * @return the current Scene object (for method chaining).
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }
}