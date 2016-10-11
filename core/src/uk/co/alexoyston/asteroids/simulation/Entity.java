package uk.co.alexoyston.asteroids.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ShortArray;

public abstract class Entity {
	private static final EntityListener nullListener = new EntityListener() {
		@Override
		public void requestEntity(Entity entity) {
		}
	};

	public float maxSpeed = 1000;
	public float maxRotationSpeed = 4.5f;
	public float maxAge = Float.MAX_VALUE;
	public float drag = 0;

	// Entity state
	public final Vector2 location = new Vector2(); // Bottom-left coordinate
	public final Vector2 center = new Vector2(); // Centre of rotation relative to the location
	public float rotation = 0; // Current rotation in rad
	public final Vector2 velocity = new Vector2(); // Current velocity in px/s
	public final Vector2 acceleration = new Vector2();
	
	public final Rectangle bounds = new Rectangle();

	public float rotationSpeed = 0; // Clockwise rotation in rad/s
	public float age = 0; // Age in s

	public Color color = Color.WHITE;	
	public boolean alive = true;

	protected final Polygon polygon = new Polygon();
	private final EarClippingTriangulator triangulator = new EarClippingTriangulator();
	private float[][] triangles;
	// pointIndexes contains indexes of polygons points
	// Each index points to the X coordinate, with the Y coordninate just being the index + 1
	// Every 3 indexes represent the 3 X coordinates needed to make up a triangle
	private ShortArray pointIndexes;

	protected EntityListener entityListener = Entity.nullListener;

	public float[] getVertices() {
		return polygon.getTransformedVertices();
	}

	public void setVertices(float[] vertices) {
		polygon.setVertices(vertices);
		updateVertices();
		
		pointIndexes = triangulator.computeTriangles(vertices);
		triangles = new float[pointIndexes.size * 2][6];
		// Multiply by 2 to skip over Y coords
		for (int i = 0; i < pointIndexes.size; i++)
			pointIndexes.mul(i, (short) 2);
	}

	public void updateVertices() {
		polygon.setPosition(location.x, location.y);
		polygon.setRotation((float)Math.toDegrees(-rotation));
		polygon.setOrigin(center.x, center.y);

		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		float maxY = Float.MIN_VALUE;
		boolean x_coord = true;
		for (float f : getVertices()) {
			if (x_coord) {
				if (f < minX)
					minX = f;
				if (f > maxX)
					maxX = f;
			} else {
				if (f < minY)
					minY = f;
				if (f > maxY)
					maxY = f;
			}
			x_coord = !x_coord;
		}
		bounds.x = minX;
		bounds.y = minY;
		bounds.width = maxX - minX;
		bounds.height = maxY - minY;
	}

	/**
	 * A personal update for any non-standard updates covered by the Simulation
	 * 
	 * @param delta
	 * 			Seconds since last update
	 */
	public void update(float delta) {
	}

	/**
	 * Check for collision between this Entity and another
	 *
	 * @param other
	 * 			The Entity to check collision against
	 *
	 * @return true if the two collide, false otherwise
	 */
	public boolean collides(Entity other) {

		// Bounding box collision detection
		if (!Intersector.overlaps(bounds, other.bounds))
			return false;

		// Triangle collision detection
		for (float[] triangle : getTriangles()) {
			for (float[] otherTriangle : other.getTriangles()) {
				if (Intersector.overlapConvexPolygons(triangle, otherTriangle, null))
					return true;
			}
		}

		return false;
	}

	/**
	 * Called when a collision occurs between this Entity and another
	 *
	 * @param other
	 * 			The entity collided with
	 */
	public abstract void collision(Entity other);

	/**
	 * Return an array of float[6]'s, each containing the coordinates to make up a single polygon triangle
	 * The float[6] is ordered as [x1, y1, x2, y2, x3, y3]
	 * 
	 * @return array of coordinates
	 */
	public float[][] getTriangles() {
		float[] points = getVertices();

		for (int i = 0; i < pointIndexes.size / 3; i++) {
			int index = pointIndexes.get(i * 3);
			triangles[i][0] = points[index];
			triangles[i][1] = points[index + 1];

			index = pointIndexes.get(i * 3 + 1);
			triangles[i][2] = points[index];
			triangles[i][3] = points[index + 1];

			index = pointIndexes.get(i * 3 + 2);
			triangles[i][4] = points[index];
			triangles[i][5] = points[index + 1];
		}
		return triangles;
	}

	public final void registerListener(EntityListener listener) {
		entityListener = listener;
	}
}
