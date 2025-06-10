package renderer;

import primitives.*;
import scene.Scene;
import java.util.MissingResourceException;
import lighting.*;
import geometries.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import static java.lang.Math.abs;
import static primitives.Util.isZero;

/**
 * Camera class represents a camera in the 3D space
 */
public class Camera implements Cloneable {
    private Point p0;
    private Vector vUp;
    private Vector vTo;
    private Vector vRight;

    private double width = 0d;
    private double height = 0d;
    private double distance = 0d;

    private ImageWriter imageWriter;
    private RayTracerBase rayTracer;

    private int nX = 1;
    private int nY = 1;

    // === multithreading & progress tracking fields ===
    private int threadsCount       = 0;
    private static final int SPARE_THREADS = 2;
    private double printInterval   = 0;
    private PixelManager pixelManager = new PixelManager(0,0,0);





    /**
     * Camera constructor
     */
    private Camera() {
    }

    /**
     * Camera getter
     *
     * @return the location of the camera
     */
    public Point getP0() {
        return p0;
    }

    /**
     * Camera getter
     *
     * @return the up direction of the camera
     */
    public Vector getVUp() {
        return vUp;
    }

    /**
     * Camera getter
     *
     * @return the direction of the camera
     */
    public Vector getVTo() {
        return vTo;
    }

    /**
     * Camera getter
     *
     * @return the right direction of the camera
     */
    public Vector getVRight() {
        return vRight;
    }

    /**
     * Camera getter
     *
     * @return the width of the view plane
     */
    public double getWidth() {
        return width;
    }

    /**
     * Camera getter
     *
     * @return the height of the view plane
     */
    public double getHeight() {
        return height;
    }

    /**
     * Camera getter
     *
     * @return the distance between the camera and the view plane
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Camera getter
     *
     * @return number of pixels in the x direction
     */
    public int getNx() {
        return nX;
    }

    /**
     * Camera getter
     *
     * @return number of pixels in the y direction
     */
    public int getNy() {
        return nY;
    }

    /**
     * construct a ray through a pixel
     *
     * @param nX the number of pixels in the x direction
     * @param nY the number of pixels in the y direction
     * @param j  the x index of the pixel
     * @param i  the y index of the pixel
     * @return the ray that passes through the pixel
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        Point pIJ = p0;
        double yI = -(i - (nY - 1) / 2d) * height / nY;
        double xJ = (j - (nX - 1) / 2d) * width / nX;

        //check if xJ or yI are not zero, so we will not add zero vector
        if (!Util.isZero(xJ)) pIJ = pIJ.add(vRight.scale(xJ));
        if (!Util.isZero(yI)) pIJ = pIJ.add(vUp.scale(yI));

        // we need to move the point in the direction of vTo by distance
        pIJ = pIJ.add(vTo.scale(distance));

        return new Ray(p0, pIJ.subtract(p0).normalize());
    }


    /**
     * Set the image writer, use castRay to write the pixels
     *
     * @return the camera
     */
    /** Single‐threaded rendering. */
    public Camera renderImageNoThreads() {
        int nx = imageWriter.getNx();
        int ny = imageWriter.getNy();
        for (int i = 0; i < ny; i++) {
            for (int j = 0; j < nx; j++) {
                castRay(j, i);
            }
        }
        return this;
    }

    /** Parallel streams rendering. */
    private Camera renderImageStream() {
        int nx = imageWriter.getNx();
        int ny = imageWriter.getNy();
        IntStream.range(0, ny).parallel()
                .forEach(i -> IntStream.range(0, nx).parallel()
                        .forEach(j -> castRay(j, i)));
        return this;
    }


    /** Raw‐threads rendering via PixelManager. */
    private Camera renderImageRawThreads() {
        int nx = imageWriter.getNx(), ny = imageWriter.getNy();
        List<Thread> threads = new LinkedList<>();
        for (int t = 0; t < threadsCount; t++) {
            threads.add(new Thread(() -> {
                PixelManager.Pixel p;
                while ((p = pixelManager.nextPixel()) != null) {
                    castRay(p.col(), p.row());
                }
            }));
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) {
        try { t.join(); } catch (InterruptedException ignored) {}
    }
        return this;
}

/**
 * Render the image using the chosen strategy:
 *   threadsCount = 0  → no threads
 *                 -1 → parallel streams
 *                 >0 → raw threads
 */

public Camera renderImage() {
    pixelManager = new PixelManager(nY, nX, printInterval);

    return switch (threadsCount) {
        case  0  -> renderImageNoThreads();
        case -1  -> renderImageStream();
        default  -> renderImageRawThreads();
    };
}

    /**
     * Print a grid on the image
     *
     * @param interval the interval between the lines of the grid
     * @param color    the color of the grid
     * @return         the camera
     */
    public Camera printGrid(int interval, Color color) {
        for (int i = 0; i < imageWriter.getNy(); i++) {
            for (int j = 0; j < imageWriter.getNx(); j++) {
                if (i % interval == 0 || j % interval == 0) {
                    imageWriter.writePixel(j, i, color);
                }
            }
        }
        return this;
    }




    /**
     * Write the image to a file
     *
     * @param pictureFileName the name of the file
     * @return the camera
     */
    public Camera writeToImage(String pictureFileName) {
        imageWriter.writeToImage(pictureFileName);
        return this;
    }

    /**
     * Cast a ray through a pixel
     * @param x the x index of the pixel
     * @param y the y index of the pixel
     */
    private void castRay(int x, int y) {
        if(x < 0|| x >= nX || y < 0 || y >= nY)
            throw new IllegalArgumentException("x and y must be inside the image bounds");

        imageWriter.writePixel(x,y,rayTracer.traceRay(constructRay(nX, nY, x, y)));
        pixelManager.pixelDone();
    }



    /**
     * Camera builder
     */
    public static class Builder {
        private final Camera camera = new Camera();

        /**
         * Set the location of the camera
         *
         * @param p0 the location of the camera
         */
        public Builder setLocation(Point p0) {
            camera.p0 = p0;
            return this;
        }

        /**
         * Set the direction of the camera
         *
         * @param vTo the direction of the camera
         *            (the vector from the camera to the "look-at" point)
         * @param vUp the up direction of the camera
         *            (the vector from the camera to the up direction)
         */
        public Builder setDirection(Vector vTo, Vector vUp) {
            if (!Util.isZero(vTo.dotProduct(vUp))) {
                throw new IllegalArgumentException("vTo and vUp must be orthogonal");
            }
            camera.vTo = vTo.normalize();
            camera.vUp = vUp.normalize();
            return this;
        }

        /**
         * Set the direction of the camera
         *
         * @param target the point to look at
         * @param vUp    the up direction of the camera
         *               (the vector from the camera to the up direction)
         */
        public Builder setDirection(Point target, Vector vUp) {
            camera.vTo = target.subtract(camera.p0).normalize();
            camera.vRight = camera.vTo.crossProduct(vUp).normalize();
            camera.vUp = camera.vRight.crossProduct(camera.vTo).normalize();
            return this;
        }

        /**
         * Set the direction of the camera
         *
         * @param target the point to look at
         */
        public Builder setDirection(Point target) {
            return setDirection(target, new Vector(0, 1, 0));
        }

        /**
         * Set the size of the view plane
         *
         * @param width  the width of the view plane
         * @param height the height of the view plane
         */
        public Builder setVpSize(double width, double height) {
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("width and height must be positive");
            }
            camera.width = width;
            camera.height = height;
            return this;
        }

        /**
         * Set the distance between the camera and the view plane
         *
         * @param distance the distance between the camera and the view plane
         */
        public Builder setVpDistance(double distance) {
            if (distance <= 0) {
                throw new IllegalArgumentException("distance from camera to view must be positive");
            }
            camera.distance = distance;
            return this;
        }

        /**
         * Set the ray tracer
         *
         * @param type the ray tracer
         * @return the camera builder
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE)
                camera.rayTracer = new SimpleRayTracer(scene);
            else
                camera.rayTracer = null;
            return this;
        }


        /**
         * Set the resolution of the image
         *
         * @param nX the number of pixels in the x direction
         * @param nY the number of pixels in the y direction
         * @return the camera builder
         */
        public Builder setResolution(int nX, int nY) {
            camera.nX = nX;
            camera.nY = nY;
            return this;
        }


        /**
         * Set the image writer
         *
         * @param imageWriter the image writer
         * @return the camera builder
         */
        public Builder setImageWriter(ImageWriter imageWriter) {
            camera.imageWriter = imageWriter;
            return this;
        }


        /**
         * Set the number of rays in the grid for the depth of field
         *
         * @param n the number of rays in the grid
         * @return the camera builder
         */
        public Builder setNx(int n) {
            camera.nX = n;
            return this;
        }


        /**
         * Set the number of rays in the grid for the
         *
         * @param n the number of rays in the grid
         * @return the camera builder
         */
        public Builder setNy(int n) {
            camera.nY = n;
            return this;
        }

        /**
         * Configure multithreading:Add commentMore actions
         *   -2 → auto (cores – SPARE_THREADS)
         *   -1 → parallel streams
         *    0 → off
         *   >0 → exact thread count
         */
        public Builder setMultithreading(int threads) {
            if (threads < -2)
                throw new IllegalArgumentException("Multithreading parameter must be ≥ -2");
            if (threads == -2) {
                int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
                camera.threadsCount = Math.max(1, cores);
            } else {
                camera.threadsCount = threads;
            }
            return this;
        }

        /**
        * Enable progress printing every `interval` percent (0 = off).Add commentMore actions
        */
        public Builder setDebugPrint(double interval) {
            if (interval < 0)
            throw new IllegalArgumentException("Print interval must be non‐negative");
            camera.printInterval = interval;
            return this;
        }

        /**
         * Build the camera
         *
         * @return the camera
         */
        public Camera build() {
            final String className = "Camera";
            final String description = "values not set: ";

            if (camera.p0 == null)
                throw new MissingResourceException(description, className, "p0");
            if (camera.vUp == null)
                throw new MissingResourceException(description, className, "vUp");
            if (camera.vTo == null)
                throw new MissingResourceException(description, className, "vTo");
            if (camera.width == 0d)
                throw new MissingResourceException(description, className, "width");
            if (camera.height == 0d)
                throw new MissingResourceException(description, className, "height");
            if (camera.distance == 0d)
                throw new MissingResourceException(description, className, "distance");

            if (camera.rayTracer == null)
                setRayTracer(null, RayTracerType.SIMPLE);

            camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();

            if (!Util.isZero(camera.vTo.dotProduct(camera.vRight)) ||
                    !Util.isZero(camera.vTo.dotProduct(camera.vUp)) ||
                    !Util.isZero(camera.vRight.dotProduct(camera.vUp)))
                throw new IllegalArgumentException("vTo, vUp and vRight must be orthogonal");


// Maybe revert to the old != 1 test
            if (!isZero(camera.vTo.length() - 1) || !isZero(camera.vUp.length() - 1) || !isZero(camera.vRight.length() - 1)) {
                throw new IllegalArgumentException("vTo, vUp and vRight must be normalized");
            }

            if (camera.width <= 0 || camera.height <= 0)
                throw new IllegalArgumentException("width and height must be positive");

            if (camera.distance <= 0)
                throw new IllegalArgumentException("distance from camera to view must be positive");

            if (camera.nX <= 0)
                throw new IllegalArgumentException("nX must be positive");

            if (camera.nY <= 0)
                throw new IllegalArgumentException("nY must be positive");

            camera.imageWriter = new ImageWriter(camera.nX, camera.nY);

            if(camera.rayTracer == null)
                camera.rayTracer = new SimpleRayTracer(null);


            try {
                return (Camera) camera.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Builder getter
     *
     * @return the camera builder
     */
    public static Builder getBuilder() {
        return new Builder();
    }

}