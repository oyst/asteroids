package uk.co.alexoyston.asteroids.simulation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
	public float maxSpeed = 1000;
	public float maxRotationSpeed = 4.5f;
	public float maxAge = Float.MAX_VALUE;

	// Entity state
	public Vector2 location = new Vector2(); // Top-left coordinate
	public Vector2 center = new Vector2(); // Centre of rotation
	public float rotation = 0; // Current rotation in rad
	public Vector2 velocity = new Vector2(); // Current velocity in px/s
	public Vector2 acceleration = new Vector2();
	
	public Rectangle bounds = new Rectangle();

	public float rotationSpeed = 0; // Clockwise rotation in rad/s
	public long age = 0; // Age in s

	public Color color = Color.WHITE;	
	public boolean alive = true;

	protected Polygon polygon = null;
	
	public float[] getVertices() {
		return polygon.getTransformedVertices();
	}

	public void updateVertices() {
		polygon.setPosition(location.x, location.y);
		polygon.setRotation(rotation);
		polygon.setOrigin(location.x - center.x, location.y - center.y);

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
}
