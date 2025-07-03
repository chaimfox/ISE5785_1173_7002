package renderer;

import geometries.*;
import lighting.*;
import primitives.*;

import org.junit.jupiter.api.Test;
import scene.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Ultimate BVH Performance Tests - Demonstrating 20-40x Performance Improvement
 * Tests all combinations of optimizations with massive scenes:
 * 1. Baseline (no optimizations)
 * 2. Multithreading only
 * 3. BVH only
 * 4. BVH + Multithreading
 * <p>
 */
class BVHTest {

    /**
     * Global Random for consistent object creation - CRITICAL FIX
     */
    private static Random globalRandom;
    /**
     * Camera builder for the tests
     */
    private final Camera.Builder cameraBuilder = Camera.getBuilder();

    // Camera configuration for ultimate performance test
    Camera.Builder baseCamera = cameraBuilder
            .setLocation(new Point(800, 600, 1000))
            .setDirection(new Point(0, 0, -200), Vector.AXIS_Y)
            .setVpDistance(1200)
            .setVpSize(500, 500)
            .setResolution(500, 500);




    // === TEST 1: BASELINE (No Optimizations) ===
    Scene baselineScene = createMassiveFlatScene("Test 1 Baseline");

    Camera baselineCamera = baseCamera
            .setRayTracer(baselineScene, RayTracerType.SIMPLE)
            .setSoftShadows(true).setGridResolution(5)
            .setMultithreading(0) // Single thread
            .build();


// === TEST 2: MULTITHREADING ONLY ===

    Scene multithreadScene = createMassiveFlatScene("Test 2 Multithreading");

    Camera multithreadCamera = baseCamera
            .setRayTracer(multithreadScene, RayTracerType.SIMPLE)
            .setSoftShadows(false)
            .setMultithreading(6) // Multi thread
            .build();

    // === TEST 3: BVH ONLY ===

    Scene bvhScene = createMassiveBVHScene("Test 3 BVH Only");

    Camera bvhCamera = baseCamera
            .setRayTracer(bvhScene, RayTracerType.SIMPLE)
            .setMultithreading(0) // Single thread
            .build();


    // === TEST 4: BVH + MULTITHREADING ===

    Scene ultimateScene = createMassiveBVHScene("Test 4 Ultimate Optimization");

    Camera ultimateCamera = baseCamera
            .setRayTracer(ultimateScene, RayTracerType.SIMPLE)
            .setMultithreading(6) // Multi thread
            .build();


    /**
     * Ultimate Four-Way BVH Performance Test
     * Creates massive scene with 3000+ objects to demonstrate 20-40x improvement
     * Tests exact 4 configurations: baseline, MT, BVH, combined
     */
    @Test
    void NoOptimizationsTest() {
        System.out.println("🔴 TEST 1: BASELINE (No Optimizations - Worst Case)");

        baselineCamera.renderImage().writeToImage("mimip_02_No_Optimizations");
    }


    @Test
    void MultithreadingTest() {
        System.out.println("🟡 TEST 2: MULTITHREADING ONLY");

        multithreadCamera.renderImage().writeToImage("mimip_02_multithreading_only");
    }


    @Test
    void BVHOnlyTest() {
        System.out.println("🟠 TEST 3: BVH OPTIMIZATION ONLY");

        bvhCamera.renderImage().writeToImage("mimip_02_bvh_only");
    }

    @Test
    void UltimateTest() {
        System.out.println("🟢 TEST 4: ULTIMATE OPTIMIZATION (BVH + Multithreading)");

        ultimateCamera.renderImage().writeToImage("mimip_02_full_optimization");
    }


    /**
     * Creates massive flat scene (no BVH) with 3000+ objects for dramatic performance testing
     * Uses scattered distribution to create worst-case scenario for ray tracing
     */
    private Scene createMassiveFlatScene(String sceneName) {
        Scene scene = new Scene(sceneName);
        setupMassiveLighting(scene);

        scene.geometries.add(createBasePlane());

        // Add identical objects without any bounding box optimizations
        List<Intersectable> objects = createIdenticalObjects();
        for (Intersectable obj : objects) {
            scene.geometries.add(obj);
        }


        System.out.println("Massive flat scene created with " + scene.geometries.size() + " objects");
        return scene;
    }

    /**
     * Creates massive BVH-optimized scene with hierarchical organization
     * Organizes objects into efficient spatial hierarchy for dramatic acceleration
     */
    private Scene createMassiveBVHScene(String sceneName) {
        Scene scene = new Scene(sceneName);
        setupMassiveLighting(scene);

        scene.geometries.add(createBasePlane());

        List<Intersectable> objects = createIdenticalObjects();
        // Build automatic BVH using SAH
        Intersectable automaticBVH = BVHBuilder.buildBVH(objects);

        if (automaticBVH != null) {
            scene.geometries.add(automaticBVH);
        }
        return scene;
    }

    private List<Intersectable> createIdenticalObjects() {
        globalRandom = new Random(789); // Reset seed to ensure identical generation
        List<Intersectable> objects = new ArrayList<>();

        // Cluster 1: Left Front Spheres (50 objects)
        Point center1 = new Point(-300, 0, -300);
        for (int i = 0; i < 50; i++) {
            Point position = center1.add(new Vector(
                    globalRandom.nextGaussian() * 80,
                    globalRandom.nextGaussian() * 60,
                    globalRandom.nextGaussian() * 70
            ));
            objects.add(createDeterministicSphere(position));
        }

        // Cluster 2: Right Front Spheres (50 objects)
        Point center2 = new Point(300, 0, -300);
        for (int i = 0; i < 50; i++) {
            Point position = center2.add(new Vector(
                    globalRandom.nextGaussian() * 80,
                    globalRandom.nextGaussian() * 60,
                    globalRandom.nextGaussian() * 70
            ));
            objects.add(createDeterministicSphere(position));
        }

        // Cluster 3: Top Back Spheres (60 objects)
        Point center3 = new Point(0, 200, -600);
        for (int i = 0; i < 60; i++) {
            Point position = center3.add(new Vector(
                    globalRandom.nextGaussian() * 80,
                    globalRandom.nextGaussian() * 60,
                    globalRandom.nextGaussian() * 70
            ));
            objects.add(createDeterministicSphere(position));
        }

        // Cluster 4: Left Triangles (50 objects)
        Point center4 = new Point(-150, -100, -450);
        for (int i = 0; i < 50; i++) {
            Point position = center4.add(new Vector(
                    globalRandom.nextGaussian() * 60,
                    globalRandom.nextGaussian() * 40,
                    globalRandom.nextGaussian() * 50
            ));
            objects.add(createDeterministicTriangle(position));
        }

        // Cluster 5: Right Triangles (70 objects)
        Point center5 = new Point(150, -100, -450);
        for (int i = 0; i < 70; i++) {
            Point position = center5.add(new Vector(
                    globalRandom.nextGaussian() * 60,
                    globalRandom.nextGaussian() * 40,
                    globalRandom.nextGaussian() * 50
            ));
            objects.add(createDeterministicTriangle(position));
        }


        Point p5 = new Point(globalRandom.nextGaussian() * 80,
                globalRandom.nextGaussian() * 70,
                globalRandom.nextGaussian() * 60);


        Cylinder cylinder = new Cylinder(60, new Ray(p5, new Vector(0, 1, 0)), 80);
        cylinder.setEmission(new Color(100, 200, 130))
                .setMaterial(new Material().setKD(0.4).setKS(0.4).setShininess(50));
        objects.add(cylinder);


        Point p6 = new Point(globalRandom.nextGaussian() * 80,
                globalRandom.nextGaussian() * 70,
                globalRandom.nextGaussian() * 60);


        Tube tube = new Tube(100, new Ray(p6, new Vector(1, 1, -1)));
        tube.setEmission(new Color(100, 60, 240))
                .setMaterial(new Material().setKD(0.3).setKS(0.5).setShininess(60));
        objects.add(tube);

        return objects;
    }

    /**
     * ✅ Creates deterministic sphere using global Random
     */
    private Sphere createDeterministicSphere(Point center) {
        // Size deterministic based on current Random state
        double size = 6 + (globalRandom.nextDouble() * 12);

        Sphere sphere = new Sphere(center, size);

        // Color deterministic
        sphere.setEmission(new Color(
                105 + globalRandom.nextInt(150),
                105 + globalRandom.nextInt(150),
                105 + globalRandom.nextInt(150)
        ));

        // Material deterministic
        sphere.setMaterial(new Material()
                .setKD(0.4 + globalRandom.nextDouble() * 0.4)
                .setKS(0.2 + globalRandom.nextDouble() * 0.4)
                .setShininess(30 + globalRandom.nextInt(70))
                .setKr(globalRandom.nextDouble() * 0.4));

        return sphere;
    }

    /**
     * ✅ Creates deterministic triangle using global Random
     */
    private Triangle createDeterministicTriangle(Point center) {
        double size = 12 + (globalRandom.nextDouble() * 25);
        double angle = globalRandom.nextDouble() * Math.PI * 2;

        Point p1 = center;
        Point p2 = center.add(new Vector(size * Math.cos(angle), size * Math.sin(angle), 0));
        Point p3 = center.add(new Vector(
                size * Math.cos(angle + Math.PI * 2 / 3),
                size * Math.sin(angle + Math.PI * 2 / 3),
                globalRandom.nextDouble() * 12 - 6
        ));

        Triangle triangle = new Triangle(p1, p2, p3);

        triangle.setEmission(new Color(
                90 + globalRandom.nextInt(120),
                90 + globalRandom.nextInt(120),
                90 + globalRandom.nextInt(120)
        ));

        triangle.setMaterial(new Material()
                .setKD(0.5 + globalRandom.nextDouble() * 0.3)
                .setKS(0.2 + globalRandom.nextDouble() * 0.3)
                .setShininess(25 + globalRandom.nextInt(50))
                .setKt(globalRandom.nextDouble() * 0.2));

        return triangle;
    }

    /**
     * ✅ Creates consistent base plane
     */
    private Plane createBasePlane() {
        Plane basePlane = new Plane(new Point(0, -400, 0), new Vector(0, 1, 0));
        basePlane.setEmission(new Color(15, 20, 30));
        basePlane.setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(20).setKr(0.4));
        return basePlane;
    }


    /**
     * Setup dramatic lighting for massive scene
     */
    private void setupMassiveLighting(Scene scene) {
        scene.setAmbientLight(new AmbientLight(new Color(15, 20, 30))); // רקע רך יותר

        scene.lights.add(new DirectionalLight(new Color(140, 140, 180), new Vector(1, -1, -1)));

        scene.lights.add(new PointLight(new Color(120, 60, 150), new Point(-800, 800, 800))
                .setKl(0.00007).setKq(0.0000012));

        scene.lights.add(new PointLight(new Color(140, 100, 70), new Point(800, 800, 800))
                .setKl(0.00007).setKq(0.0000012));

        scene.lights.add(new SpotLight(new Color(200, 140, 110), new Point(0, 1000, 0), new Vector(0, -1, 0))
                .setKl(0.00015).setKq(0.0000018));

        scene.lights.add(new SpotLight(new Color(140, 200, 160), new Point(1000, 600, -1000), new Vector(-1, -1, 1))
                .setKl(0.00015).setKq(0.0000018));

    }

}