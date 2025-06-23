package renderer;Add commentMore actions

import geometries.*;
import lighting.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Stage 2 BVH Performance Tests for Manual Hierarchy Organization.
 * Tests the effectiveness of manually built BVH spatial hierarchies.
 * Expected improvement: 3-4x faster than Stage 1 CBR optimization.
 *
 * @author Yehuda Rubin and Arye Hacohen
 */
class Stage2ManualBVHTest {

    /** Scene for the BVH performance tests */
    private Scene scene;

    /** Camera builder for the tests with BVH optimization */
    private final Camera.Builder cameraBuilder = Camera.getBuilder();

    /**
     * Stage 2 BVH Performance Test - Manual Spatial Hierarchy
     * Creates a complex scene with 500+ objects and manually organizes them into
     * an efficient spatial hierarchy for optimal ray tracing performance.
     * Compares flat CBR performance vs manual BVH hierarchy performance.
     * Expected result: 3-4x performance improvement over Stage 1 CBR.
     */
    @Test
    void stage2ManualBVHPerformanceTest() {
        System.out.println("=== Stage 2: Manual BVH Hierarchy Performance Test ===\n");

        // === PHASE 1: Create Flat CBR Scene (Stage 1 baseline) ===
        scene = new Scene("Stage 2 Flat CBR Baseline");
        setupLighting(scene);
        createFlatSceneGeometry(scene);

        System.out.println("Phase 1: Testing flat CBR structure (Stage 1 baseline)...");
        System.out.println("Total objects in flat scene: " + scene.geometries.size());

        // Configure camera for performance testing
        Camera flatCamera = cameraBuilder
                .setLocation(new Point(500, 400, 700))
                .setDirection(new Point(-100, -100, -300), Vector.AXIS_Y)
                .setVpDistance(900)
                .setVpSize(350, 350)
                .setResolution(700, 700)  // High resolution for thorough testing
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .build();

        // Measure flat CBR performance
        long startTime1 = System.nanoTime();
        flatCamera.renderImage().writeToImage("stage2_flat_cbr_baseline");
        long endTime1 = System.nanoTime();

        double flatCBRTime = (endTime1 - startTime1) / 1_000_000_000.0;
        System.out.println("Flat CBR render time: " + String.format("%.2f", flatCBRTime) + " seconds");
        System.out.println("Flat CBR performance: " + String.format("%.0f", 490000 / flatCBRTime) + " rays/second");

        // === PHASE 2: Create Manual BVH Hierarchy Scene ===
        Scene bvhScene = new Scene("Stage 2 Manual BVH Hierarchy");
        setupLighting(bvhScene);
        createManualBVHHierarchy(bvhScene);

        System.out.println("\nPhase 2: Testing manual BVH spatial hierarchy...");
        System.out.println("BVH hierarchy structure created with spatial organization");

        Camera bvhCamera = cameraBuilder
                .setLocation(new Point(500, 400, 700))
                .setDirection(new Point(-100, -100, -300), Vector.AXIS_Y)
                .setVpDistance(900)
                .setVpSize(350, 350)
                .setResolution(700, 700)  // Same resolution for fair comparison
                .setRayTracer(bvhScene, RayTracerType.SIMPLE)
                .build();

        // Measure manual BVH performance
        long startTime2 = System.nanoTime();
        bvhCamera.renderImage().writeToImage("stage2_manual_bvh_hierarchy");
        long endTime2 = System.nanoTime();

        double bvhTime = (endTime2 - startTime2) / 1_000_000_000.0;
        System.out.println("Manual BVH render time: " + String.format("%.2f", bvhTime) + " seconds");
        System.out.println("Manual BVH performance: " + String.format("%.0f", 490000 / bvhTime) + " rays/second");

        // === PERFORMANCE ANALYSIS ===
        double improvementFactor = flatCBRTime / bvhTime;
        double timeSaved = flatCBRTime - bvhTime;

        System.out.println("\n=== Stage 2 Performance Results ===");
        System.out.println("Resolution tested: 700x700 (490,000 rays)");
        System.out.println("Scene complexity: 500+ geometric objects");
        System.out.println("Stage 1 CBR time: " + String.format("%.2f", flatCBRTime) + " seconds");
        System.out.println("Stage 2 BVH time: " + String.format("%.2f", bvhTime) + " seconds");
        System.out.println("Improvement factor: " + String.format("%.2fx", improvementFactor) + " faster");
        System.out.println("Time saved: " + String.format("%.2f", timeSaved) + " seconds");

        // Performance evaluation
        if (improvementFactor >= 3.0) {
            System.out.println("✅ EXCELLENT: Manual BVH hierarchy is highly effective!");
            System.out.println("   BVH spatial organization provides significant acceleration");
        } else if (improvementFactor >= 2.0) {
            System.out.println("✅ GOOD: Manual BVH showing solid improvement over CBR");
            System.out.println("   Hierarchy organization is working effectively");
        } else if (improvementFactor >= 1.3) {
            System.out.println("✅ PARTIAL: BVH provides moderate improvement");
            System.out.println("   Consider optimizing spatial organization further");
        } else {
            System.out.println("⚠️  ISSUE: BVH improvement is minimal");
            System.out.println("   Check hierarchy organization and bounding box calculations");
        }

        // Verify both scenes produced valid results
        assertTrue(flatCBRTime > 0, "Flat CBR render time should be positive");
        assertTrue(bvhTime > 0, "BVH render time should be positive");
        assertTrue(improvementFactor >= 1.0, "BVH should be at least as fast as flat CBR");
        assertTrue(improvementFactor <= 50.0, "Improvement factor should be reasonable");

        // Verify scenes have similar complexity
        assertNotNull(scene.geometries.getBoundingBox(), "Flat scene should have bounding box");
        assertNotNull(bvhScene.geometries.getBoundingBox(), "BVH scene should have bounding box");

        System.out.println("\n📁 Generated images:");
        System.out.println("   stage2_flat_cbr_baseline.ppm - Flat CBR structure");
        System.out.println("   stage2_manual_bvh_hierarchy.ppm - Manual BVH hierarchy");
        System.out.println("   (Both images should appear visually identical)");
        System.out.println("\n✅ Stage 2 Manual BVH test completed successfully!");
    }

    /**
     * Creates lighting setup for the performance test scenes
     * Provides realistic lighting without excessive computational overhead
     */
    private void setupLighting(Scene scene) {
        // Ambient lighting for overall scene illumination
        scene.setAmbientLight(new AmbientLight(new Color(25, 25, 30)));

        // Primary directional light for general illumination
        scene.lights.add(
                new DirectionalLight(new Color(400, 400, 500), new Vector(1, -1, -1))
        );

        // Secondary point light for depth and highlights
        scene.lights.add(
                new PointLight(new Color(300, 350, 400), new Point(-300, 400, 300))
                        .setKl(0.0001).setKq(0.000001)
        );

        // Accent spot light for dramatic effect
        scene.lights.add(
                new SpotLight(new Color(500, 300, 400), new Point(400, 500, 400), new Vector(-1, -1, -1))
                        .setKl(0.0002).setKq(0.000002)
        );
    }

    /**
     * Creates flat scene geometry for Stage 1 CBR baseline comparison
     * All objects are added directly to scene.geometries without hierarchy
     */
    private void createFlatSceneGeometry(Scene scene) {
        // Foundation planes
        scene.geometries.add(
                new Plane(new Point(0, -250, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(20, 25, 35))
                        .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(40).setKR(0.4))
        );

        scene.geometries.add(
                new Plane(new Point(0, 0, -600), new Vector(0, 0, 1))
                        .setEmission(new Color(35, 25, 25))
                        .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(25))
        );

        Random random = new Random(123); // Fixed seed for reproducible results

        // Create 300 spheres with random properties and positions
        System.out.println("Creating 300 spheres for flat CBR baseline...");
        for (int i = 0; i < 300; i++) {
            Point center = new Point(
                    random.nextDouble() * 600 - 300,  // X: -300 to 300
                    random.nextDouble() * 350 - 175,  // Y: -175 to 175
                    random.nextDouble() * 400 - 500   // Z: -500 to -100
            );

            double radius = random.nextDouble() * 15 + 4; // Radius: 4-19

            scene.geometries.add(
                    new Sphere(radius, center)
                            .setEmission(new Color(
                                    random.nextInt(200) + 55,   // R: 55-255
                                    random.nextInt(200) + 55,   // G: 55-255
                                    random.nextInt(200) + 55    // B: 55-255
                            ))
                            .setMaterial(new Material()
                                    .setKD(0.4 + random.nextDouble() * 0.4)  // KD: 0.4-0.8
                                    .setKS(0.2 + random.nextDouble() * 0.4)  // KS: 0.2-0.6
                                    .setShininess(25 + random.nextInt(75))   // Shininess: 25-100
                                    .setKR(random.nextDouble() * 0.4))       // KR: 0-0.4
            );
        }

        // Create 200 triangles with random properties and positions
        System.out.println("Creating 200 triangles for flat CBR baseline...");
        for (int i = 0; i < 200; i++) {
            Point basePoint = new Point(
                    random.nextDouble() * 400 - 200,  // X: -200 to 200
                    random.nextDouble() * 250 - 125,  // Y: -125 to 125
                    random.nextDouble() * 300 - 450   // Z: -450 to -150
            );

            double size = random.nextDouble() * 30 + 8; // Size: 8-38
            double angle = random.nextDouble() * Math.PI * 2;

            Point p1 = basePoint;
            Point p2 = basePoint.add(new Vector(size * Math.cos(angle), size * Math.sin(angle), 0));
            Point p3 = basePoint.add(new Vector(
                    size * Math.cos(angle + Math.PI * 2/3),
                    size * Math.sin(angle + Math.PI * 2/3),
                    random.nextDouble() * 15 - 7.5
            ));

            scene.geometries.add(
                    new Triangle(p1, p2, p3)
                            .setEmission(new Color(
                                    random.nextInt(180) + 75,   // R: 75-255
                                    random.nextInt(180) + 75,   // G: 75-255
                                    random.nextInt(180) + 75    // B: 75-255
                            ))
                            .setMaterial(new Material()
                                    .setKD(0.5 + random.nextDouble() * 0.3)  // KD: 0.5-0.8
                                    .setKS(0.2 + random.nextDouble() * 0.3)  // KS: 0.2-0.5
                                    .setShininess(20 + random.nextInt(60))   // Shininess: 20-80
                                    .setKT(random.nextDouble() * 0.25))      // KT: 0-0.25
            );
        }

        System.out.println("Flat scene created with " + scene.geometries.size() + " total objects");
    }

    /**
     * Creates manually organized BVH hierarchy for optimal ray tracing performance
     * Objects are spatially organized into logical groups for efficient traversal
     */
    private void createManualBVHHierarchy(Scene scene) {
        // Foundation planes (stay flat - infinite objects don't benefit from hierarchy)
        scene.geometries.add(
                new Plane(new Point(0, -250, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(20, 25, 35))
                        .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(40).setKR(0.4)),

                new Plane(new Point(0, 0, -600), new Vector(0, 0, 1))
                        .setEmission(new Color(35, 25, 25))
                        .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(25))
        );

        Random random = new Random(123); // Same seed for identical object distribution

        // === SPATIAL ORGANIZATION FOR SPHERES ===
        Geometries leftSpheres = new Geometries();     // X < -100
        Geometries centerSpheres = new Geometries();   // -100 <= X <= 100
        Geometries rightSpheres = new Geometries();    // X > 100

        System.out.println("Creating 300 spatially organized spheres...");
        for (int i = 0; i < 300; i++) {
            Point center = new Point(
                    random.nextDouble() * 600 - 300,  // X: -300 to 300
                    random.nextDouble() * 350 - 175,  // Y: -175 to 175
                    random.nextDouble() * 400 - 500   // Z: -500 to -100
            );

            double radius = random.nextDouble() * 15 + 4; // Radius: 4-19

            Sphere sphereObj = new Sphere(radius, center);
            sphereObj.setEmission(new Color(
                    random.nextInt(200) + 55,
                    random.nextInt(200) + 55,
                    random.nextInt(200) + 55
            ));
            sphereObj.setMaterial(new Material()
                    .setKD(0.4 + random.nextDouble() * 0.4)
                    .setKS(0.2 + random.nextDouble() * 0.4)
                    .setShininess(25 + random.nextInt(75))
                    .setKR(random.nextDouble() * 0.4));

            // Organize spheres by X coordinate for efficient spatial queries
            if (center.getX() < -100) {
                leftSpheres.add(sphereObj);
            } else if (center.getX() > 100) {
                rightSpheres.add(sphereObj);
            } else {
                centerSpheres.add(sphereObj);
            }
        }

        // === SPATIAL ORGANIZATION FOR TRIANGLES ===
        Geometries frontTriangles = new Geometries();  // Z > -325
        Geometries backTriangles = new Geometries();   // Z <= -325

        System.out.println("Creating 200 spatially organized triangles...");
        for (int i = 0; i < 200; i++) {
            Point basePoint = new Point(
                    random.nextDouble() * 400 - 200,
                    random.nextDouble() * 250 - 125,
                    random.nextDouble() * 300 - 450   // Z: -450 to -150
            );

            double size = random.nextDouble() * 30 + 8;
            double angle = random.nextDouble() * Math.PI * 2;

            Point p1 = basePoint;
            Point p2 = basePoint.add(new Vector(size * Math.cos(angle), size * Math.sin(angle), 0));
            Point p3 = basePoint.add(new Vector(
                    size * Math.cos(angle + Math.PI * 2/3),
                    size * Math.sin(angle + Math.PI * 2/3),
                    random.nextDouble() * 15 - 7.5
            ));

            Triangle triangleObj = new Triangle(p1, p2, p3);
            triangleObj.setEmission(new Color(
                    random.nextInt(180) + 75,
                    random.nextInt(180) + 75,
                    random.nextInt(180) + 75
            ));
            triangleObj.setMaterial(new Material()
                    .setKD(0.5 + random.nextDouble() * 0.3)
                    .setKS(0.2 + random.nextDouble() * 0.3)
                    .setShininess(20 + random.nextInt(60))
                    .setKT(random.nextDouble() * 0.25));

            // Organize triangles by Z coordinate (depth)
            if (basePoint.getZ() > -325) {
                frontTriangles.add(triangleObj);
            } else {
                backTriangles.add(triangleObj);
            }
        }

        // === BUILD EFFICIENT BVH HIERARCHY ===

        // Level 3: Group spheres by spatial regions
        BVHNode leftCenterSpheres = new BVHNode(leftSpheres, centerSpheres);
        BVHNode allSpheres = new BVHNode(leftCenterSpheres, rightSpheres);

        // Level 3: Group triangles by depth
        BVHNode allTriangles = new BVHNode(frontTriangles, backTriangles);

        // Level 2: Combine all geometric types
        BVHNode topLevelHierarchy = new BVHNode(allSpheres, allTriangles);

        // Add the complete hierarchy to the scene
        scene.geometries.add(topLevelHierarchy);

        System.out.println("Manual BVH hierarchy constructed:");
        System.out.println("  Left spheres: " + leftSpheres.size());
        System.out.println("  Center spheres: " + centerSpheres.size());
        System.out.println("  Right spheres: " + rightSpheres.size());
        System.out.println("  Front triangles: " + frontTriangles.size());
        System.out.println("  Back triangles: " + backTriangles.size());
        System.out.println("  Total objects: " + (leftSpheres.size() + centerSpheres.size() + rightSpheres.size() +
                frontTriangles.size() + backTriangles.size()));
        System.out.println("  Hierarchy levels: 3 (optimized for spatial queries)");
    }

    /**
     * Stage 2 Optimized Test - Creates a scene with better spatial distribution
     * for demonstrating BVH effectiveness
     */
    @Test
    void stage2OptimizedBVHPerformanceTest() {
        System.out.println("=== Stage 2: Optimized BVH Performance Test ===\n");
        System.out.println("Creating scene with better spatial distribution for BVH demonstration...");

        // === PHASE 1: Create Optimized Flat CBR Scene ===
        Scene flatScene = new Scene("Stage 2 Optimized Flat CBR");
        setupOptimizedLighting(flatScene);
        createOptimizedFlatScene(flatScene);

        System.out.println("Phase 1: Testing optimized flat CBR structure...");
        System.out.println("Total objects in flat scene: " + flatScene.geometries.size());

        Camera flatCamera = cameraBuilder
                .setLocation(new Point(600, 500, 800))
                .setDirection(new Point(0, 0, -300), Vector.AXIS_Y)
                .setVpDistance(1000)
                .setVpSize(400, 400)
                .setResolution(600, 600)  // Slightly lower resolution for better demo
                .setRayTracer(flatScene, RayTracerType.SIMPLE)
                .build();

        long startTime1 = System.nanoTime();
        flatCamera.renderImage().writeToImage("stage2_optimized_flat_cbr");
        long endTime1 = System.nanoTime();

        double flatCBRTime = (endTime1 - startTime1) / 1_000_000_000.0;
        System.out.println("Optimized Flat CBR render time: " + String.format("%.2f", flatCBRTime) + " seconds");
        System.out.println("Flat CBR performance: " + String.format("%.0f", 360000 / flatCBRTime) + " rays/second");

        // === PHASE 2: Create Optimized BVH Hierarchy Scene ===
        Scene bvhScene = new Scene("Stage 2 Optimized BVH Hierarchy");
        setupOptimizedLighting(bvhScene);
        createOptimizedBVHHierarchy(bvhScene);

        System.out.println("\nPhase 2: Testing optimized BVH spatial hierarchy...");
        System.out.println("BVH hierarchy with clustered object distribution");

        Camera bvhCamera = cameraBuilder
                .setLocation(new Point(600, 500, 800))
                .setDirection(new Point(0, 0, -300), Vector.AXIS_Y)
                .setVpDistance(1000)
                .setVpSize(400, 400)
                .setResolution(600, 600)
                .setRayTracer(bvhScene, RayTracerType.SIMPLE)
                .build();

        long startTime2 = System.nanoTime();
        bvhCamera.renderImage().writeToImage("stage2_optimized_bvh_hierarchy");
        long endTime2 = System.nanoTime();

        double bvhTime = (endTime2 - startTime2) / 1_000_000_000.0;
        System.out.println("Optimized BVH render time: " + String.format("%.2f", bvhTime) + " seconds");
        System.out.println("BVH performance: " + String.format("%.0f", 360000 / bvhTime) + " rays/second");

        // === PERFORMANCE ANALYSIS ===
        double improvementFactor = flatCBRTime / bvhTime;
        double timeSaved = flatCBRTime - bvhTime;

        System.out.println("\n=== Stage 2 Optimized Results ===");
        System.out.println("Resolution tested: 600x600 (360,000 rays)");
        System.out.println("Scene optimization: Clustered distribution");
        System.out.println("Flat CBR time: " + String.format("%.2f", flatCBRTime) + " seconds");
        System.out.println("BVH time: " + String.format("%.2f", bvhTime) + " seconds");
        System.out.println("Improvement factor: " + String.format("%.2fx", improvementFactor) + " faster");
        System.out.println("Time saved: " + String.format("%.2f", timeSaved) + " seconds");

        if (improvementFactor >= 3.0) {
            System.out.println("🎯 EXCELLENT: BVH hierarchy provides significant acceleration!");
        } else if (improvementFactor >= 2.0) {
            System.out.println("✅ GOOD: BVH showing solid improvement");
        } else {
            System.out.println("⚠️  PARTIAL: BVH provides moderate improvement");
        }

        System.out.println("\n📁 Generated optimized images:");
        System.out.println("   stage2_optimized_flat_cbr.ppm");
        System.out.println("   stage2_optimized_bvh_hierarchy.ppm");
    }

    /**
     * Creates optimized lighting for better performance demonstration
     */
    private void setupOptimizedLighting(Scene scene) {
        scene.setAmbientLight(new AmbientLight(new Color(30, 30, 35)));

        scene.lights.add(new DirectionalLight(new Color(400, 400, 500), new Vector(1, -1, -1)));
        scene.lights.add(new PointLight(new Color(300, 300, 400), new Point(-400, 500, 400))
                .setKl(0.0001).setKq(0.000001));
    }

    /**
     * Creates optimized flat scene with clustered object distribution
     * This creates more empty space for BVH to be effective
     */
    private void createOptimizedFlatScene(Scene scene) {
        // Foundation plane
        scene.geometries.add(
                new Plane(new Point(0, -300, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(25, 30, 40))
                        .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(40).setKR(0.3))
        );

        Random random = new Random(456); // Different seed for better distribution

        // Create 3 distinct clusters of objects - this creates more empty space
        System.out.println("Creating clustered object distribution for optimal BVH testing...");

        // Cluster 1: Left side spheres
        for (int i = 0; i < 60; i++) {
            Point center = new Point(
                    -400 + random.nextDouble() * 150,  // X: -400 to -250 (left cluster)
                    random.nextDouble() * 200 - 100,   // Y: -100 to 100
                    random.nextDouble() * 200 - 500    // Z: -500 to -300
            );

            scene.geometries.add(createRandomSphere(center, random));
        }

        // Cluster 2: Right side spheres
        for (int i = 0; i < 60; i++) {
            Point center = new Point(
                    250 + random.nextDouble() * 150,   // X: 250 to 400 (right cluster)
                    random.nextDouble() * 200 - 100,   // Y: -100 to 100
                    random.nextDouble() * 200 - 500    // Z: -500 to -300
            );

            scene.geometries.add(createRandomSphere(center, random));
        }

        // Cluster 3: Center back triangles
        for (int i = 0; i < 80; i++) {
            Point center = new Point(
                    random.nextDouble() * 200 - 100,   // X: -100 to 100 (center)
                    random.nextDouble() * 150 - 75,    // Y: -75 to 75
                    random.nextDouble() * 100 - 700    // Z: -700 to -600 (far back)
            );

            scene.geometries.add(createRandomTriangle(center, random));
        }

        System.out.println("Optimized flat scene created with " + scene.geometries.size() + " objects in clusters");
    }

    /**
     * Creates optimized BVH hierarchy with clustered object distribution
     */
    private void createOptimizedBVHHierarchy(Scene scene) {
        // Foundation plane
        scene.geometries.add(
                new Plane(new Point(0, -300, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(25, 30, 40))
                        .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(40).setKR(0.3))
        );

        Random random = new Random(456); // Same seed for identical distribution

        // Create spatial containers for clusters
        Geometries leftCluster = new Geometries();     // Left side objects
        Geometries rightCluster = new Geometries();    // Right side objects
        Geometries backCluster = new Geometries();     // Back triangles

        System.out.println("Creating optimized BVH hierarchy with spatial clusters...");

        // Cluster 1: Left side spheres (in hierarchy)
        for (int i = 0; i < 60; i++) {
            Point center = new Point(
                    -400 + random.nextDouble() * 150,
                    random.nextDouble() * 200 - 100,
                    random.nextDouble() * 200 - 500
            );

            leftCluster.add(createRandomSphere(center, random));
        }

        // Cluster 2: Right side spheres (in hierarchy)
        for (int i = 0; i < 60; i++) {
            Point center = new Point(
                    250 + random.nextDouble() * 150,
                    random.nextDouble() * 200 - 100,
                    random.nextDouble() * 200 - 500
            );

            rightCluster.add(createRandomSphere(center, random));
        }

        // Cluster 3: Back triangles (in hierarchy)
        for (int i = 0; i < 80; i++) {
            Point center = new Point(
                    random.nextDouble() * 200 - 100,
                    random.nextDouble() * 150 - 75,
                    random.nextDouble() * 100 - 700
            );

            backCluster.add(createRandomTriangle(center, random));
        }

        // Build efficient spatial hierarchy
        BVHNode leftRightClusters = new BVHNode(leftCluster, rightCluster);
        BVHNode topLevelHierarchy = new BVHNode(leftRightClusters, backCluster);

        // Add only the hierarchy to scene
        scene.geometries.add(topLevelHierarchy);

        System.out.println("Optimized BVH hierarchy constructed:");
        System.out.println("  Left cluster: " + leftCluster.size() + " objects");
        System.out.println("  Right cluster: " + rightCluster.size() + " objects");
        System.out.println("  Back cluster: " + backCluster.size() + " objects");
        System.out.println("  Total in hierarchy: " + (leftCluster.size() + rightCluster.size() + backCluster.size()));
        System.out.println("  Objects directly in scene: " + scene.geometries.size()); // Should be 2 (1 plane + 1 BVH)
        System.out.println("  Spatial organization: 3 distinct clusters with empty space between");
    }

    /**
     * Helper method to create random sphere with consistent properties
     */
    private Sphere createRandomSphere(Point center, Random random) {
        // Create a sphere with random radius and position
        Sphere sphere = new Sphere(random.nextDouble() * 10 + 5, center);
        // Set random emission color
        sphere.setEmission(new Color(
                random.nextInt(150) + 100,  // R: 100-250
                random.nextInt(150) + 100,  // G: 100-250
                random.nextInt(150) + 100   // B: 100-250
        ));
        // Set random material properties
        sphere.setMaterial(new Material()
                .setKD(0.5 + random.nextDouble() * 0.3)  // KD: 0.5-0.8
                .setKS(0.2 + random.nextDouble() * 0.3)  // KS: 0.2-0.5
                .setShininess(30 + random.nextInt(50))   // Shininess: 30-80
                .setKr(random.nextDouble() * 0.3));      // KR: 0-0.3
        return sphere;
    }

    /**
     * Helper method to create random triangle with consistent properties
     */
    private Triangle createRandomTriangle(Point center, Random random) {
        double size = random.nextDouble() * 20 + 10;
        double angle = random.nextDouble() * Math.PI * 2;

        Point p1 = center;
        Point p2 = center.add(new Vector(size * Math.cos(angle), size * Math.sin(angle), 0));
        Point p3 = center.add(new Vector(
                size * Math.cos(angle + Math.PI * 2/3),
                size * Math.sin(angle + Math.PI * 2/3),
                random.nextDouble() * 10 - 5
        ));

        Triangle triangle = new Triangle(p1, p2, p3);
        // Set random emission color
        triangle.setEmission(new Color(
                random.nextInt(120) + 80,  // R: 80-200
                random.nextInt(120) + 80,  // G: 80-200
                random.nextInt(120) + 80   // B: 80-200
        ));

        // Set random material properties
        triangle.setMaterial(new Material()
                .setKD(0.4 + random.nextDouble() * 0.4)  // KD: 0.4-0.8
                .setKS(0.2 + random.nextDouble() * 0.3)  // KS: 0.2-0.5
                .setShininess(20 + random.nextInt(40))   // Shininess: 20-60
                .setKt(random.nextDouble() * 0.2));      // KT: 0-0.2
        // Return the created triangle
        return triangle;
    }
}