package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * Abstract class for ray tracing
 */
public abstract class RayTracerBase {

    /**
     * Ray tracer types
     */
    public enum RayTracerType {
        /** Simple (basic) ray tracer */
        SIMPLE,
        /** Ray tracer using regular grid */
        GRID
    }


    /**
     * The scene to be rendered
     */
    protected final Scene scene;

    /**
     * Constructor to initialize the scene
     * @param scene the scene to be rendered
     */
    public RayTracerBase(Scene scene) {
        this.scene = scene;
    }

    /**
     * Method to trace the rays
     * @param ray the ray to be traced
     * @return the color of the ray
     */
    abstract public Color traceRay(Ray ray);
}