package uk.co.alexoyston.asteroids.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ShortArray;

public abstract class Entity {
	public float maxSpeed = 1000;
	public float maxRotationSpeed = 4.5f;
	public float maxAge = Float.MAX_VALUE;
	public float drag = 0;

	// Entity state
	public final Vector2 location = new Vector2(); // Top-left coordinate
	public final Vector2 center = new Vector2(); // Centre of rotation
	public float rotation = 0; // Current rotation in rad
	public final Vector2 velocity = new Vector2(); // Current velocity in px/s
	public final Vector2 acceleration = new Vector2();
	
	public final Rectangle bounds = new Rectangle();

	public float rotationSpeed = 0; // Clockwise rotation in rad/s
	public long age = 0; // Age in s

	public Color color = Color.WHITE;	
	public boolean alive = true;

	protected final Polygon polygon = new Polygon();
	private final EarClippingTriangulator triangulator = new EarClippingTriangulator();
	private float[] triangles;	
	// pointIndexes contains indexes of polygons points
	// Each index points to the X coordinate, with the Y coordninate just being the index + 1
	// Every 3 indexes represent the 3 X coordinates needed to make up a triangle
	private ShortArray pointIndexes;
	
	public float[] getVertices() {
		return polygon.getTransformedVertices();
	}

	public void setVertices(float[] vertices) {
		polygon.setVertices(vertices);
		updateVertices();
		
		pointIndexes = triangulator.computeTriangles(vertices);
		triangles = new float[pointIndexes.size * 2];
		// Multiply by 2 to skip over Y coords
		for (int i = 0; i < pointIndexes.size; i++)
			pointIndexes.mul(i, (short) 2);
	}
	
	public void updateVertices() {
		polygon.setPosition(location.x, location.y);
		polygon.setRotation((float)Math.toDegrees(-rotation));
		polygon.setOrigin(center.x - location.x, center.y - location.y);

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
		return false;
	}

	public float[] getTriangles() {
		float[] points = getVertices();

		for (int i = 0; i < pointIndexes.size; i++) {
			int index = pointIndexes.get(i);
			triangles[i*2] = points[index];
			triangles[i*2 + 1] = points[index + 1];
		}

		return triangles;
	}
}
