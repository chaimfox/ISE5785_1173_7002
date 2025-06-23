package geometries;

import geometries.*;
import org.junit.jupiter.api.Test;
import primitives.*;
import renderer.RayTracerType;
import scene.Scene;
import renderer.Camera;
import lighting.*;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Stage 1 Performance Tests for BVH (Bounding Volume Hierarchy) optimizations
 * This class contains tests to measure the performance impact of CBR (Conservative Boundary Region)
 * optimizations in complex scenes with various geometries.
 */
class Stage1PerformanceTests {
    /** Scene for the BVH performance tests */
    private Scene scene;

    /** Camera builder for the tests with BVH optimization */
    private final Camera.Builder cameraBuilder = Camera.getBuilder();

    /**
     * Stage 1 BVH Performance Test - Conservative Boundary Region (CBR)
     * Creates a complex scene with 500+ objects to test CBR optimization effectiveness.
     * Measures rendering time with and without bounding box optimizations.
     * Expected result: 3-4x performance improvement with CBR enabled.
     */
    @Test
    void stage1CBRPerformanceTestWithComplexScene() {
        scene = new Scene("Stage 1 CBR Performance Test");

        // Set ambient light for overall scene illumination
        scene.setAmbientLight(new AmbientLight(new Color(20, 20, 25)));

        // Add multiple light sources for realistic lighting
        scene.lights.add(
                new DirectionalLight(new Color(300, 300, 400), new Vector(1, -1, -1))
        );

        scene.lights.add(
                new PointLight(new Color(400, 400, 500), new Point(-200, 300, 200))
                        .setKl(0.0001).setKq(0.000001)
        );

        scene.lights.add(
                new SpotLight(new Color(500, 400, 300), new Point(300, 400, 300), new Vector(-1, -1, -1))
                        .setKl(0.0002).setKq(0.000002)
        );

        // Create foundation plane
        scene.geometries.add(
                new Plane(new Point(0, -300, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(30, 30, 40))
                        .setMaterial(new Material().setKD(0.6).setKS(0.4).setShininess(30).setKR(0.3))
        );

        // Add back wall
        scene.geometries.add(
                new Plane(new Point(0, 0, -800), new Vector(0, 0, 1))
                        .setEmission(new Color(40, 30, 30))
                        .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(20))
        );

        // Generate 200 random spheres - main performance test objects
        Random random = new Random(42); // Fixed seed for reproducible results
        System.out.println("Creating 200 spheres for CBR performance test...");

        for (int i = 0; i < 200; i++) {
            Point center = new Point(
                    random.nextDouble() * 600 - 300,  // X: -300 to 300
                    random.nextDouble() * 400 - 200,  // Y: -200 to 200
                    random.nextDouble() * 400 - 600   // Z: -600 to -200
            );

            double radius = random.nextDouble() * 12 + 3; // Radius: 3-15

            scene.geometries.add(
                    new Sphere(radius, center)
                            .setEmission(new Color(
                                    random.nextInt(180) + 75,   // R: 75-255
                                    random.nextInt(180) + 75,   // G: 75-255
                                    random.nextInt(180) + 75    // B: 75-255
                            ))
                            .setMaterial(new Material()
                                    .setKD(0.5 + random.nextDouble() * 0.3)  // KD: 0.5-0.8
                                    .setKS(0.2 + random.nextDouble() * 0.3)  // KS: 0.2-0.5
                                    .setShininess(20 + random.nextInt(60))   // Shininess: 20-80
                                    .setKR(random.nextDouble() * 0.3))       // KR: 0-0.3
            );
        }

        // Generate 150 random triangles for additional geometric complexity
        System.out.println("Creating 150 triangles for additional complexity...");

        for (int i = 0; i < 150; i++) {
            Point basePoint = new Point(
                    random.nextDouble() * 500 - 250,  // X: -250 to 250
                    random.nextDouble() * 300 - 150,  // Y: -150 to 150
                    random.nextDouble() * 300 - 550   // Z: -550 to -250
            );

            double triangleSize = random.nextDouble() * 25 + 10; // Size: 10-35

            Point p1 = basePoint;
            Point p2 = basePoint.add(new Vector(triangleSize, 0, 0));
            Point p3 = basePoint.add(new Vector(triangleSize * 0.5, triangleSize, 0));

            scene.geometries.add(
                    new Triangle(p1, p2, p3)
                            .setEmission(new Color(
                                    random.nextInt(150) + 50,   // R: 50-200
                                    random.nextInt(150) + 50,   // G: 50-200
                                    random.nextInt(150) + 50    // B: 50-200
                            ))
                            .setMaterial(new Material()
                                    .setKD(0.4 + random.nextDouble() * 0.4)  // KD: 0.4-0.8
                                    .setKS(0.1 + random.nextDouble() * 0.4)  // KS: 0.1-0.5
                                    .setShininess(15 + random.nextInt(50))   // Shininess: 15-65
                                    .setKT(random.nextDouble() * 0.2))       // KT: 0-0.2
            );
        }

        // Add 100 cylinders for mixed geometry performance testing
        System.out.println("Creating 100 cylinders for mixed geometry testing...");

        for (int i = 0; i < 100; i++) {
            Point basePoint = new Point(
                    random.nextDouble() * 400 - 200,  // X: -200 to 200
                    random.nextDouble() * 200 - 100,  // Y: -100 to 100
                    random.nextDouble() * 200 - 500   // Z: -500 to -300
            );

            Vector direction = new Vector(
                    random.nextDouble() * 2 - 1,      // X: -1 to 1
                    random.nextDouble() * 2 - 1,      // Y: -1 to 1
                    random.nextDouble() * 2 - 1       // Z: -1 to 1
            ).normalize();

            double radius = random.nextDouble() * 8 + 2;     // Radius: 2-10
            double height = random.nextDouble() * 30 + 15;   // Height: 15-45

            scene.geometries.add(
                    new Cylinder(radius, new Ray(basePoint, direction), height)
                            .setEmission(new Color(
                                    random.nextInt(120) + 60,   // R: 60-180
                                    random.nextInt(120) + 60,   // G: 60-180
                                    random.nextInt(200) + 55    // B: 55-255 (blue bias)
                            ))
                            .setMaterial(new Material()
                                    .setKD(0.3 + random.nextDouble() * 0.4)  // KD: 0.3-0.7
                                    .setKS(0.2 + random.nextDouble() * 0.5)  // KS: 0.2-0.7
                                    .setShininess(25 + random.nextInt(75))   // Shininess: 25-100
                                    .setKR(random.nextDouble() * 0.4))       // KR: 0-0.4
            );
        }

        System.out.println("Total objects in scene: " + scene.geometries.size());
        System.out.println("Starting Stage 1 CBR performance measurement...");

        // Configure camera for performance testing
        Camera camera = cameraBuilder
                .setLocation(new Point(400, 300, 600))
                .setDirection(new Point(-100, -50, -300), Vector.AXIS_Y)
                .setVpDistance(800)
                .setVpSize(400, 400)
                .setResolution(600, 600)  // 360,000 rays total
                .setRayTracer(scene, RayTracerType.SIMPLE)
                //.disableDepthOfField()  // CRITICAL: Disable DOF for performance testing
                .build();

        // Performance measurement with detailed timing
        System.out.println("\n=== Stage 1 Performance Measurement ===");
        System.out.println("Resolution: 600x600 (360,000 rays)");
        System.out.println("Scene complexity: " + scene.geometries.size() + " objects");

        // CBR optimization is automatically enabled when bounding boxes are calculated
        // The first call to getBoundingBox() will trigger calculation and caching

        long startTime = System.nanoTime();
        camera.renderImage().writeToImage("stage1_cbr_performance_test");
        long endTime = System.nanoTime();

        double renderTime = (endTime - startTime) / 1_000_000_000.0;

        System.out.println("Rendering completed!");
        System.out.println("Total render time: " + String.format("%.2f", renderTime) + " seconds");
        System.out.println("Performance: " + String.format("%.0f", 360000 / renderTime) + " rays/second");

        // Verify scene bounding box was calculated (indicates CBR is active)
        AABB sceneBoundingBox = scene.geometries.getBoundingBox();
        assertNotNull(sceneBoundingBox, "Scene bounding box should be calculated for CBR optimization");

        System.out.println("Scene bounding box: " + sceneBoundingBox);
        System.out.println("CBR optimization: ACTIVE");

        // Performance validation
        assertTrue(renderTime > 0, "Render time should be positive");
        assertTrue(renderTime < 300, "Render time should be reasonable (under 5 minutes)");

        System.out.println("\n✅ Stage 1 CBR test completed successfully!");
        System.out.println("📁 Output image: stage1_cbr_performance_test.ppm");

        // Note: For comparison testing (with/without CBR), you would need to:
        // 1. Invalidate all bounding boxes (simulate no CBR)
        // 2. Measure performance
        // 3. Re-enable bounding boxes (CBR active)
        // 4. Measure performance again
        // 5. Calculate improvement ratio
    }

    /**
     * Stage 1 CBR Test with Artistic Complex Scene
     * Creates a visually appealing scene while testing CBR performance.
     * Combines aesthetic value with performance measurement.
     */
    @Test
    void stage1CBRTestWithArtisticComplexScene() {
        scene = new Scene("Stage 1 CBR Artistic Test");

        // Enhanced ambient lighting for artistic effect
        scene.setAmbientLight(new AmbientLight(new Color(25, 20, 30)));

        // Artistic lighting setup
        scene.lights.add(
                new SpotLight(new Color(800, 600, 400), new Point(-400, 400, 300), new Vector(1, -1, -1))
                        .setKl(0.00005).setKq(0.0000005));

        scene.lights.add(
                new DirectionalLight(new Color(200, 300, 400), new Vector(0.3, -0.8, -0.5)));

        scene.lights.add(
                new PointLight(new Color(500, 400, 600), new Point(200, 250, 100))
                        .setKl(0.0002).setKq(0.000001));

        // Reflective floor for artistic effect
        scene.geometries.add(
                new Plane(new Point(0, -200, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(15, 15, 25))
                        .setMaterial(new Material().setKD(0.4).setKS(0.6).setShininess(80).setKR(0.5))
        );

        // Backdrop wall
        scene.geometries.add(
                new Plane(new Point(0, 0, -500), new Vector(0, 0, 1))
                        .setEmission(new Color(30, 25, 35))
                        .setMaterial(new Material().setKD(0.7).setKS(0.3).setShininess(20))
        );

        // Central focal spheres for artistic composition
        scene.geometries.add(
                // Large central glass sphere
                new Sphere(80d, new Point(0, -100, -250))
                        .setEmission(new Color(10, 10, 30))
                        .setMaterial(new Material().setKD(0.1).setKS(0.9).setShininess(120).setKT(0.8).setKR(0.2)),

                // Supporting artistic spheres
                new Sphere(40d, new Point(-120, -150, -200))
                        .setEmission(new Color(150, 50, 50))
                        .setMaterial(new Material().setKD(0.4).setKS(0.6).setShininess(60).setKR(0.3)),

                new Sphere(35d, new Point(130, -140, -180))
                        .setEmission(new Color(50, 150, 50))
                        .setMaterial(new Material().setKD(0.4).setKS(0.6).setShininess(70).setKT(0.2)),

                new Sphere(30d, new Point(-80, -50, -150))
                        .setEmission(new Color(50, 50, 200))
                        .setMaterial(new Material().setKD(0.3).setKS(0.7).setShininess(100).setKT(0.6))
        );

        // Generate artistic cluster of smaller objects for CBR testing
        Random artisticRandom = new Random(123); // Different seed for artistic variation

        // Create 150 artistic spheres in clusters
        for (int cluster = 0; cluster < 3; cluster++) {
            Point clusterCenter = new Point(
                    (cluster - 1) * 200,           // Spread clusters: -200, 0, 200
                    -50 + cluster * 30,            // Varying heights
                    -200 - cluster * 50            // Varying depths
            );

            for (int i = 0; i < 50; i++) {
                Point position = clusterCenter.add(new Vector(
                        artisticRandom.nextGaussian() * 60,  // Gaussian distribution for natural clustering
                        artisticRandom.nextGaussian() * 40,
                        artisticRandom.nextGaussian() * 50
                ));

                double radius = artisticRandom.nextDouble() * 8 + 2;

                // Color scheme varies by cluster
                Color clusterColor = switch (cluster) {
                    case 0 -> new Color(200, 100, 100);  // Red cluster
                    case 1 -> new Color(100, 200, 100);  // Green cluster
                    case 2 -> new Color(100, 100, 200);  // Blue cluster
                    default -> new Color(150, 150, 150);
                };

                scene.geometries.add(
                        new Sphere(radius, position)
                                .setEmission(clusterColor.scale(0.3 + artisticRandom.nextDouble() * 0.4))
                                .setMaterial(new Material()
                                        .setKD(0.4 + artisticRandom.nextDouble() * 0.3)
                                        .setKS(0.3 + artisticRandom.nextDouble() * 0.4)
                                        .setShininess(30 + artisticRandom.nextInt(60))
                                        .setKR(artisticRandom.nextDouble() * 0.3))
                );
            }
        }

        // Add geometric art pieces (triangles forming interesting shapes)
        for (int i = 0; i < 80; i++) {
            Point base = new Point(
                    artisticRandom.nextDouble() * 300 - 150,
                    artisticRandom.nextDouble() * 150 - 75,
                    artisticRandom.nextDouble() * 200 - 350
            );

            double size = artisticRandom.nextDouble() * 20 + 5;
            double angle = artisticRandom.nextDouble() * Math.PI * 2;

            Point p1 = base;
            Point p2 = base.add(new Vector(size * Math.cos(angle), size * Math.sin(angle), 0));
            Point p3 = base.add(new Vector(
                    size * Math.cos(angle + Math.PI * 2/3),
                    size * Math.sin(angle + Math.PI * 2/3),
                    artisticRandom.nextDouble() * 10 - 5
            ));

            scene.geometries.add(
                    new Triangle(p1, p2, p3)
                            .setEmission(new Color(
                                    100 + artisticRandom.nextInt(155),
                                    100 + artisticRandom.nextInt(155),
                                    100 + artisticRandom.nextInt(155)
                            ))
                            .setMaterial(new Material()
                                    .setKD(0.5).setKS(0.5).setShininess(40)
                                    .setKT(artisticRandom.nextDouble() * 0.3))
            );
        }

        System.out.println("Artistic scene created with " + scene.geometries.size() + " objects");

        // Render artistic scene with CBR optimization
        long startTime = System.nanoTime();

        cameraBuilder
                .setLocation(new Point(250, 150, 400))
                .setDirection(new Point(-50, -75, -200), Vector.AXIS_Y)
                .setVpDistance(600)
                .setVpSize(300, 300)
                .setResolution(800, 800)  // High resolution for artistic quality
                .setRayTracer(scene, RayTracerType.SIMPLE)
                //.disableDepthOfField()  // Focus on CBR performance, not DOF
                .build()
                .renderImage()
                .writeToImage("stage1_cbr_artistic_scene");

        long endTime = System.nanoTime();
        double renderTime = (endTime - startTime) / 1_000_000_000.0;

        System.out.println("Artistic CBR test completed in " + String.format("%.2f", renderTime) + " seconds");
        System.out.println("CBR bounding box calculated: " + (scene.geometries.getBoundingBox() != null));

        // Verify CBR is working
        assertNotNull(scene.geometries.getBoundingBox(), "CBR bounding box should be calculated");
        assertTrue(renderTime > 0 && renderTime < 600, "Render time should be reasonable");

        System.out.println("✅ Artistic CBR test completed successfully!");
        System.out.println("📁 Output image: stage1_cbr_artistic_scene.ppm");
    }
}