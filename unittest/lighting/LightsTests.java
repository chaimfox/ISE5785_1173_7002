package lighting;

import static java.awt.Color.BLUE;

import org.junit.jupiter.api.Test;

import geometries.*;
import primitives.*;
import renderer.*;
import scene.Scene;

/**
 * Test rendering a basic image
 * @author Dan Zilberstein
 */
class LightsTests {
   /** Default constructor to satisfy JavaDoc generator */
   LightsTests() { /* to satisfy JavaDoc generator */ }

   /** First scene for some of the tests */
   private final Scene          scene1                  = new Scene("Test scene");
   /** Second scene for some of the tests */
   private final Scene          scene2                  = new Scene("Test scene")
      .setAmbientLight(new AmbientLight(new Color(38, 38, 38)));

   /** First camera builder for some of the tests */
   private final Camera.Builder camera1                 = Camera.getBuilder()                                          //
      .setRayTracer(scene1, RayTracerType.SIMPLE)                                                                      //
      .setLocation(new Point(0, 0, 1000))                                                                              //
      .setDirection(Point.ZERO, Vector.AXIS_Y)                                                                         //
      .setVpSize(150, 150).setVpDistance(1000);

   /** Second camera builder for some of the tests */
   private final Camera.Builder camera2                 = Camera.getBuilder()                                          //
      .setRayTracer(scene2, RayTracerType.SIMPLE)                                                                      //
      .setLocation(new Point(0, 0, 1000))                                                                              //
      .setDirection(Point.ZERO, Vector.AXIS_Y)                                                                         //
      .setVpSize(200, 200).setVpDistance(1000);

   /** Shininess value for most of the geometries in the tests */
   private static final int     SHININESS               = 301;
   /** Diffusion attenuation factor for some of the geometries in the tests */
   private static final double  KD                      = 0.5;
   /** Diffusion attenuation factor for some of the geometries in the tests */
   private static final Double3 KD3                     = new Double3(0.2, 0.6, 0.4);

   /** Specular attenuation factor for some of the geometries in the tests */
   private static final double  KS                      = 0.5;
   /** Specular attenuation factor for some of the geometries in the tests */
   private static final Double3 KS3                     = new Double3(0.2, 0.4, 0.3);

   /** Material for some of the geometries in the tests */
   private final Material       material                = new Material().setKD(KD3).setKS(KS3).setShininess(SHININESS);
   /** Light color for tests with triangles */
   private final Color          trianglesLightColor     = new Color(800, 500, 250);
   /** Light color for tests with sphere */
   private final Color          sphereLightColor        = new Color(800, 500, 0);
   /** Color of the sphere */
   private final Color          sphereColor             = new Color(BLUE).reduce(2);

   /** Center of the sphere */
   private final Point          sphereCenter            = new Point(0, 0, -50);
   /** Radius of the sphere */
   private static final double  SPHERE_RADIUS           = 50d;

   /** The triangles' vertices for the tests with triangles */
   private final Point[]        vertices                =
      {
        // the shared left-bottom:
        new Point(-110, -110, -150),
        // the shared right-top:
        new Point(95, 100, -150),
        // the right-bottom
        new Point(110, -110, -150),
        // the left-top
        new Point(-75, 78, 100)
      };
   /** Position of the light in tests with sphere */
   private final Point          sphereLightPosition     = new Point(-50, -50, 25);
   /** Light direction (directional and spot) in tests with sphere */
   private final Vector         sphereLightDirection    = new Vector(1, 1, -0.5);
   /** Position of the light in tests with triangles */
   private final Point          trianglesLightPosition  = new Point(30, 10, -100);
   /** Light direction (directional and spot) in tests with triangles */
   private final Vector         trianglesLightDirection = new Vector(-2, -2, -2);

   /** The sphere in appropriate tests */
   private final Geometry       sphere                  = new Sphere(sphereCenter, SPHERE_RADIUS)
      .setEmission(sphereColor).setMaterial(new Material().setKD(KD).setKS(KS).setShininess(SHININESS));
   /** The first triangle in appropriate tests */
   private final Geometry       triangle1               = new Triangle(vertices[0], vertices[1], vertices[2])
      .setMaterial(material);
   /** The first triangle in appropriate tests */
   private final Geometry       triangle2               = new Triangle(vertices[0], vertices[1], vertices[3])
      .setMaterial(material);

   /** Produce a picture of a sphere lighted by a directional light */
   @Test
   void sphereDirectional() {
      scene1.geometries.add(sphere);
      scene1.lights.add(new DirectionalLight(sphereLightColor, sphereLightDirection));

      camera1 //
         .setResolution(500, 500) //
         .build() //
         .renderImageNoThreads() //
         .writeToImage("lightSphereDirectional");
   }

   /** Produce a picture of a sphere lighted by a point light */
   @Test
   void spherePoint() {
      scene1.geometries.add(sphere);
      scene1.lights.add(new PointLight(sphereLightColor, sphereLightPosition) //
         .setKl(0.001).setKq(0.0002));

      camera1 //
         .setResolution(500, 500) //
         .build() //
         .renderImageNoThreads() //
         .writeToImage("lightSpherePoint");
   }

   /** Produce a picture of a sphere lighted by a spotlight */
   @Test
   void sphereSpot() {
      scene1.geometries.add(sphere);
      scene1.lights.add(new SpotLight(sphereLightColor, sphereLightPosition, sphereLightDirection) //
         .setKl(0.001).setKq(0.0001));

      camera1 //
         .setResolution(500, 500) //
         .build() //
         .renderImageNoThreads() //
         .writeToImage("lightSphereSpot");
   }

   /** Produce a picture of two triangles lighted by a directional light */
   @Test
   void trianglesDirectional() {
      scene2.geometries.add(triangle1, triangle2);
      scene2.lights.add(new DirectionalLight(trianglesLightColor, trianglesLightDirection));

      camera2.setResolution(500, 500) //
         .build() //
         .renderImageNoThreads() //
         .writeToImage("lightTrianglesDirectional");
   }

   /** Produce a picture of two triangles lighted by a point light */
   @Test
   void trianglesPoint() {
      scene2.geometries.add(triangle1, triangle2);
      scene2.lights.add(new PointLight(trianglesLightColor, trianglesLightPosition) //
         .setKl(0.001).setKq(0.0002));

      camera2.setResolution(500, 500) //
         .build() //
         .renderImageNoThreads() //
         .writeToImage("lightTrianglesPoint");
   }

   /** Produce a picture of two triangles lighted by a spotlight */
   @Test
   void trianglesSpot() {
      scene2.geometries.add(triangle1, triangle2);
      scene2.lights.add(new SpotLight(trianglesLightColor, trianglesLightPosition, trianglesLightDirection) //
         .setKl(0.001).setKq(0.0001));

      camera2.setResolution(500, 500) //
         .build() //
         .renderImageNoThreads() //
         .writeToImage("lightTrianglesSpot");
   }

   /** Produce a picture of a sphere lighted by 3 different light sources together */
   @Test
   void lightCombined1() {
      scene1.geometries.add(sphere);
      scene1.lights.add(new DirectionalLight(new Color(300, 150, 150), new Vector(1, 1, -1)));
      scene1.lights.add(new PointLight(new Color(200, 300, 200), new Point(50, 50, 25)) //
              .setKl(0.001).setKq(0.0002));
      scene1.lights.add(new SpotLight(new Color(250, 250, 400), new Point(-60, -60, 40), new Vector(1, 1, -1)) //
              .setKl(0.001).setKq(0.0001));

      camera1 //
              .setResolution(500, 500) //
              .build() //
              .renderImageNoThreads() //
              .writeToImage("lightCombined1");
   }

   /** Produce a picture of two triangles lighted by 3 different light sources together */
   @Test
   void lightCombined2() {
      scene2.geometries.add(triangle1, triangle2);
      scene2.lights.add(new DirectionalLight(new Color(400, 300, 100), new Vector(-1, -1, -1)));
      scene2.lights.add(new PointLight(new Color(300, 200, 300), new Point(-30, 60, -80)) //
              .setKl(0.0015).setKq(0.0003));
      scene2.lights.add(new SpotLight(new Color(500, 300, 200), new Point(50, -40, 30), new Vector(-2, 2, -1)) //
              .setKl(0.001).setKq(0.0001));

      camera2 //
              .setResolution(500, 500) //
              .build() //
              .renderImageNoThreads() //
              .writeToImage("lightCombined2");
   }



   /** Produce a picture of a sphere lighted by a narrow spotlight */
   /**
   @Test
   void sphereSpotSharp() {
      scene1.geometries.add(sphere);
      scene1.lights
         .add(new SpotLight(sphereLightColor, sphereLightPosition, new Vector(1, 1, -0.5)) //
            .setKl(0.001).setKq(0.00004).setNarrowBeam(10));

      camera1.setResolution(500, 500) //
         .build() //
         .renderImage() //
         .writeToImage("lightSphereSpotSharp");
   }

   /** Produce a picture of two triangles lighted by a narrow spotlight */
/**
   @Test
   void trianglesSpotSharp() {
      scene2.geometries.add(triangle1, triangle2);
      scene2.lights.add(new SpotLight(trianglesLightColor, trianglesLightPosition, trianglesLightDirection) //
         .setKl(0.001).setKq(0.00004).setNarrowBeam(10));

      camera2.setResolution(500, 500) //
         .build() //
         .renderImage() //
         .writeToImage("lightTrianglesSpotSharp");
   }
*/
}
