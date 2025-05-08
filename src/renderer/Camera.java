package renderer;

import primitives.*;
import primitives.Vector;

import static primitives.Util.*;

import java.util.*;

/**
 * Camera class represents a camera in a 3D scene. The camera is characterized
 * by its location and orientation vectors (vRight, vUp, vTo). It also has
 * properties defining the view plane's dimensions and its distance from the
 * camera.
 */
public class Camera implements Cloneable {

    /**
     * The location of the camera.
     */
    private Point p0;

    /**
     * The forward direction vector of the camera.
     */
    private Vector vTo;

    /**
     * The up direction vector of the camera.
     */
    private Vector vUp;

    /**
     * The right direction vector of the camera.
     */
    private Vector vRight;

    /**
     * The height of the view plane.
     */
    private double height = 0;

    /**
     * The width of the view plane.
     */
    private double width = 0;

    /**
     * The distance from the camera to the view plane.
     */
    private double distanceFromCamera = 0;

    /**
     * The image writer responsible for writing pixels to an image.
     */
    private ImageWriter imageWriter;

    /**
     * The ray tracer responsible for tracing rays and computing colors.
     */
    private RayTracerBase rayTracer;

    /**
     * Private constructor to enforce the use of the builder.
     */
    private Camera() {
    }

    /**
     * Gets the height of the view plane.
     *
     * @return the height of the view plane.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Gets the width of the view plane.
     *
     * @return the width of the view plane.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Gets the distance from the camera to the view plane.
     *
     * @return the distance from the camera to the view plane.
     */
    public double getDistanceFromCamera() {
        return distanceFromCamera;
    }

    /**
     * Returns a new Builder instance for constructing a Camera.
     *
     * @return a new Builder instance.
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray from the camera through a specific pixel on the view plane.
     *
     * @param nX the number of pixels in the X direction.
     * @param nY the number of pixels in the Y direction.
     * @param j  the pixel index in the X direction.
     * @param i  the pixel index in the Y direction.
     * @return the constructed ray.
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        // Calculate the pixel's center point on the view plane
        Point pij = p0.add(vTo.scale(distanceFromCamera));

        // Calculate the x and y offsets of the pixel
        double xj = (j - ((nX - 1) / 2.0)) * (width / nX);
        double yi = (((nY - 1) / 2.0) - i) * (height / nY);

        if (!isZero(xj)) {
            pij = pij.add(vRight.scale(xj));
        }

        if (!isZero(yi)) {
            pij = pij.add(vUp.scale(yi));
        }
        // Return the constructed ray
        return new Ray(p0, pij.subtract(p0));

    }

    /**
     * Renders the image by tracing rays through each pixel and computing the color.
     * This method is currently not implemented and will throw an
     * UnsupportedOperationException if called.
     *
     * @return this
     * @throws UnsupportedOperationException when the method is called, indicating
     *                                       that it is not yet implemented.
     */
    public Camera renderImage() {
        int nX = imageWriter.getNx();
        int nY = imageWriter.getNy();
        for (int i = 0; i < nX; ++i)
            for (int j = 0; j < nY; ++j)
                castRay(nX, nY, j, i);

        return this;
    }

    /**
     * Private method to cast a ray through the center of a pixel, trace its color,
     * and write the color to the image.
     *
     * @param nX     The number of pixels in the X direction.
     * @param nY     The number of pixels in the Y direction.
     * @param column The pixel index in the X direction.
     * @param row    The pixel index in the Y direction.
     */
    private void castRay(int nX, int nY, int column, int row) {

        // Construct a ray from the camera through the center of the pixel
        Ray ray = constructRay(nX, nY, column, row);

        // Trace the ray to determine its color
        Color pixelColor = rayTracer.traceRay(ray);

        // Write the color to the image at pixel (row, column)
        imageWriter.writePixel(row, column, pixelColor);
    }

    /**
     * Draws a grid over the image with the specified interval and color. This
     * method overlays horizontal and vertical lines at specified intervals across
     * the entire image to help in visualizing the alignment and distribution of
     * elements.
     *
     * @param interval The spacing between the grid lines in pixels.
     * @param color    The color of the grid lines.
     * @return The current Camera instance, allowing for method chaining.
     * @throws MissingResourceException if the imageWriter is null.
     */
    public Camera printGrid(int interval, Color color) {
        // Get the number of pixels in the Y (vertical) and X (horizontal) dimensions
        int nY = imageWriter.getNy();
        int nX = imageWriter.getNx();

        // Draw horizontal grid lines at every 'interval' pixels
        for (int i = 0; i < nY; i += interval) {
            for (int j = 0; j < nX; j += 1) {
                imageWriter.writePixel(i, j, color);
            }
        }

        // Draw vertical grid lines at every 'interval' pixels
        for (int i = 0; i < nY; i += 1) {
            for (int j = 0; j < nX; j += interval) {
                imageWriter.writePixel(i, j, color);
            }
        }
        // Return the current Camera instance
        return this;
    }

    /**
     * Writes the image to a file using the imageWriter. This method delegates the
     * call to the imageWriter's writeToImage method.
     *
     * @throws MissingResourceException if the imageWriter is null.
     */
    public void writeToImage() {
        imageWriter.writeToImage();
    }

    /**
     * Builder class for constructing a Camera instance.
     */
    public static class Builder {

        /**
         * A new Camera instance being built.
         */
        final private Camera camera = new Camera();

        /**
         * Sets the location of the camera.
         *
         * @param p the point representing the camera's location.
         * @return the builder instance.
         */
        public Builder setLocation(Point p) {
            camera.p0 = p;
            return this;
        }

        /**
         * Sets the direction of the camera. This method ensures that the provided
         * vectors are orthogonal and normalizes them before setting.
         *
         * @param vUp the up direction vector.
         * @param vTo the forward direction vector.
         * @return the builder instance.
         * @throws IllegalArgumentException if the vectors are not orthogonal.
         */
        public Builder setDirection(Vector vTo, Vector vUp) {
            if (!isZero(vUp.dotProduct(vTo))) {
                throw new IllegalArgumentException("vUp and vTo must be orthogonal");
            }
            camera.vTo = vTo.normalize();
            camera.vUp = vUp.normalize();
            camera.vRight = camera.vTo.crossProduct(camera.vUp);
            return this;
        }

        /**
         * Sets the size of the view plane.
         *
         * @param width  the width of the view plane.
         * @param height the height of the view plane.
         * @return the builder instance.
         * @throws IllegalArgumentException if the length or height are not greater than
         *                                  0.
         */
        public Builder setVpSize(double width, double height) {
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("width and height must be greater than 0");
            }
            camera.width = width;
            camera.height = height;
            return this;
        }

        /**
         * Sets the distance from the camera to the view plane.
         *
         * @param distanceFromCamera the distance from the camera to the view plane.
         * @return the builder instance.
         * @throws IllegalArgumentException if the distance from the camera is not
         *                                  greater than 0.
         */
        public Builder setVpDistance(double distanceFromCamera) {
            if (distanceFromCamera < 0) {
                throw new IllegalArgumentException("distanceFromCamera must be greater than 0");
            }
            camera.distanceFromCamera = distanceFromCamera;
            return this;
        }

        /**
         * Sets the image writer for the camera configuration.
         *
         * @param imageWriter The image writer to be set.
         * @return This Builder object.
         */
        public Builder setImageWriter(ImageWriter imageWriter) {
            camera.imageWriter = imageWriter;
            return this;
        }

        /**
         * Sets the ray tracer for the camera configuration.
         *
         * @param rayTracer The ray tracer to be set.
         * @return This Builder object.
         */
        public Builder setRayTracer(RayTracerBase rayTracer) {
            camera.rayTracer = rayTracer;
            return this;
        }

        /**
         * Builds the Camera instance.
         *
         * @return a clone of the constructed Camera.
         * @throws MissingResourceException if any of the required fields are not set.
         */
        public Camera build() {

            final String MISSING_RENDERING_DATA = "Missing rendering data";

            if (camera.p0 == null)
                throw new MissingResourceException(MISSING_RENDERING_DATA, Camera.class.getName(),
                        "Location (Point p)");
            if (camera.vTo == null)
                throw new MissingResourceException(MISSING_RENDERING_DATA, Camera.class.getName(), "vTo vector");
            if (camera.vUp == null)
                throw new MissingResourceException(MISSING_RENDERING_DATA, Camera.class.getName(), "vUp vector");
            if (isZero(camera.vTo.dotProduct(camera.vTo)))
                throw new MissingResourceException(MISSING_RENDERING_DATA, Camera.class.getName(),
                        "vTo vector and vUp vectore");
            if (camera.vRight == null)
                camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();

            if (isZero(camera.width))
                throw new MissingResourceException(MISSING_RENDERING_DATA, Camera.class.getName(), "View plane width");
            if (isZero(camera.height))
                throw new MissingResourceException(MISSING_RENDERING_DATA, Camera.class.getName(), "View plane height");
            if (camera.width < 0 || camera.height < 0)
                throw new IllegalStateException("Invalid view plane size. Width and height must be greater than zero.");
            if (isZero(camera.distanceFromCamera))
                throw new MissingResourceException(MISSING_RENDERING_DATA, Camera.class.getName(),
                        "Distance from camera");
            if (camera.distanceFromCamera < 0)
                throw new IllegalStateException("Invalid distance from camera. Must be greater than zero.");

            if (!(camera.imageWriter instanceof ImageWriter))
                throw new MissingResourceException(MISSING_RENDERING_DATA, Camera.class.getName(),
                        "View plane imageWriter");
            if (!(camera.rayTracer instanceof RayTracerBase))
                throw new MissingResourceException(MISSING_RENDERING_DATA, Camera.class.getName(),
                        "View plane rayTracer");

            try {
                return (Camera) camera.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }

    }

}