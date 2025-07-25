package geometries;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

import java.util.List;

import primitives.*;


/**
 * Polygon class represents two-dimensional polygon in 3D Cartesian coordinate
 * system
 * @author Dan
 */
public class Polygon extends Geometry {
   /** List of polygon's vertices */
   protected final List<Point> vertices;
   /** Associated plane in which the polygon lays */
   protected final Plane       plane;
   /** The size of the polygon - the amount of the vertices in the polygon */
   private final int           size;

   /**
    * Polygon constructor based on vertices list. The list must be ordered by edge
    * path. The polygon must be convex.
    * @param  vertices                 list of vertices according to their order by
    *                                  edge path
    * @throws IllegalArgumentException in any case of illegal combination of
    *                                  vertices:
    *                                  <ul>
    *                                  <li>Less than 3 vertices</li>
    *                                  <li>Consequent vertices are in the same
    *                                  point
    *                                  <li>The vertices are not in the same
    *                                  plane</li>
    *                                  <li>The order of vertices is not according
    *                                  to edge path</li>
    *                                  <li>Three consequent vertices lay in the
    *                                  same line (180&#176; angle between two
    *                                  consequent edges)
    *                                  <li>The polygon is concave (not convex)</li>
    *                                  </ul>
    */
   public Polygon(Point... vertices) {
      if (vertices.length < 3)
         throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
      this.vertices = List.of(vertices);
      size          = vertices.length;

      // Generate the plane according to the first three vertices and associate the
      // polygon with this plane.
      // The plane holds the invariant normal (orthogonal unit) vector to the polygon
      plane         = new Plane(vertices[0], vertices[1], vertices[2]);
      if (size == 3) return; // no need for more tests for a Triangle

      Vector  n        = plane.getNormal();
      // Subtracting any subsequent points will throw an IllegalArgumentException
      // because of Zero Vector if they are in the same point
      Vector  edge1    = vertices[vertices.length - 1].subtract(vertices[vertices.length - 2]);
      Vector  edge2    = vertices[0].subtract(vertices[vertices.length - 1]);

      // Cross Product of any subsequent edges will throw an IllegalArgumentException
      // because of Zero Vector if they connect three vertices that lay in the same
      // line.
      // Generate the direction of the polygon according to the angle between last and
      // first edge being less than 180 deg. It is hold by the sign of its dot product
      // with the normal. If all the rest consequent edges will generate the same sign
      // - the polygon is convex ("kamur" in Hebrew).
      boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
      for (var i = 1; i < vertices.length; ++i) {
         // Test that the point is in the same plane as calculated originally
         if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
            throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");
         // Test the consequent edges have
         edge1 = edge2;
         edge2 = vertices[i].subtract(vertices[i - 1]);
         if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
            throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
      }
   }

   @Override
   public Vector getNormal(Point point) { return plane.getNormal(); }

   /**
    * Finds the intersection points between a given ray and the Polygon.
    *
    * @param ray the ray to intersect with the Polygon
    * @return a list of intersection points, or null if there are no intersections
    */
   @Override
   protected List<Intersection> calculateIntersectionsHelper(Ray ray) {

      // Check if the ray intersects the plane of the polygon
      List<Point> planeIntersections = plane.findIntersections(ray);
      if (planeIntersections == null)
         return null;

      // Retrieve the direction vector and head point of the ray
      Vector rayDirection = ray.getDirection();
      Point rayPoint = ray.getHead();

      for(Point p : vertices) {
         if (p.equals(rayPoint))
            return null; // The ray's head is one of the polygon's vertices
      }

      // Loop through all vertices and edges of the polygon
      Boolean positive = null;
      for (int i = 0; i < size; i++) {
         Point p1 = vertices.get(i);
         Point p2 = vertices.get((i + 1) % size);

         Vector edgeVector1 = p1.subtract(rayPoint);
         Vector edgeVector2 = p2.subtract(rayPoint);
         // Vector toIntersection = intersectionPoint.subtract(p1);
         Vector normal = edgeVector1.crossProduct(edgeVector2).normalize();

         double dotProduct = alignZero(normal.dotProduct(rayDirection));
         if (dotProduct == 0)
            return null; // Intersection point is on the edge considered outside the polygon

         if (positive == null) {
            positive = dotProduct > 0;
         } else if (positive != dotProduct > 0)
            return null; // the sing is not the sane for all vertices
      }
      // Return the intersection point with the plane of the polygon
      return List.of(new Intersection(this, planeIntersections.getFirst() , this.getMaterial()));
   }

   /**
    * Get the vertices of the polygon
    *
    * @return the vertices of the polygon
    */
   @Override
   protected AABB calculateBoundingBox() {
      if (vertices.isEmpty()) {
         return null;
      }

      double minX = vertices.get(0).getX();
      double maxX = vertices.get(0).getX();
      double minY = vertices.get(0).getY();
      double maxY = vertices.get(0).getY();
      double minZ = vertices.get(0).getZ();
      double maxZ = vertices.get(0).getZ();

      for (Point vertex : vertices) {
         minX = Math.min(minX, vertex.getX());
         maxX = Math.max(maxX, vertex.getX());
         minY = Math.min(minY, vertex.getY());
         maxY = Math.max(maxY, vertex.getY());
         minZ = Math.min(minZ, vertex.getZ());
         maxZ = Math.max(maxZ, vertex.getZ());
      }

      return new AABB(new Point(minX, minY, minZ), new Point(maxX, maxY, maxZ));
   }

}