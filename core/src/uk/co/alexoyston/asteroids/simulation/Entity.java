package uk.co.alexoyston.asteroids.simulation;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
	private static final float maxSpeed = 1;
	private static final float maxRotationSpeed = 0.0045f;
	protected long maxAge = Long.MAX_VALUE;

	// Entity state
	protected Vector2 location = new Vector2(); // Top-left coordinate
	protected Vector2 center = new Vector2(); // Centre of rotation
	protected float rotation = 0; // Current rotation in rad
	protected Vector2 velocity = new Vector2(); // Current velocity in px/ms

	protected float minX;
	protected float minY;
	protected float maxX;
	protected float maxY;

	protected float rotationSpeed = 0; // Clockwise rotation in rad/ms
	protected long age = 0; // Age in ms

	protected float cosRotation = (float) Math.cos(rotation);
	protected float sinRotation = (float) Math.sin(rotation);

	protected Color color = Color.WHITE;

	protected Polygon polygon = null;

	protected boolean alive = true;

	Entity() {
	}

	public boolean alive() {
		return alive;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public float getRotationalSpeed() {
		return rotationSpeed;
	}

	public float getRotation() {
		return rotation;
	}

	public Vector2 getLocation() {
		return location;
	}

	public float getX() {
		return location.x;
	}

	public float getY() {
		return location.y;
	}

	public Vector2 getCenter() {
		return center;
	}

	public float getCenterX() {
		return center.x;
	}

	public float getCenterY() {
		return center.y;
	}

	public long getAge() {
		return age;
	}

	public float[] getPolyVertices() {
		return polygon.getTransformedVertices();
	}

	public Color getColor() {
		return color;
	}

	public float getMinX() {
		return minX;
	}

	public float getMaxX() {
		return maxX;
	}

	public float getMinY() {
		return minY;
	}

	public float getMaxY() {
		return maxY;
	}

	public long maxAge() {
		return maxAge;
	}

	public float maxSpeed() {
		return maxSpeed;
	}

	public float maxRotation() {
		return maxRotationSpeed;
	}

	public void setLocation(Vector2 location) {
		this.location = location;
	}

	public void setLocation(float x, float y) {
		setLocation(new Vector2(x, y));
	}

	public void setX(float x) {
		setLocation(new Vector2(x, getY()));
	}

	public void setY(float y) {
		setLocation(new Vector2(getX(), y));
	}

	public void setCenter(Vector2 center) {
		this.center = center;
	}

	public void setCenter(float x, float y) {
		setCenter(new Vector2(x, y));
	}

	public void setCenterX(float x) {
		this.center.x = x;
	}

	public void setCenterY(float y) {
		this.center.y = y;
	}

	public void setVelocity(Vector2 velocity) {
		setVelocity(velocity.x, velocity.y);
	}

	public void setVelocity(float x, float y) {
		velocity.x = Math.min(Math.max(x, -maxSpeed), maxSpeed);
		velocity.y = Math.min(Math.max(y, -maxSpeed), maxSpeed);
	}

	public void setRotation(float radians) {
		rotation = (float) (radians % (2 * Math.PI));
		cosRotation = (float) Math.cos(rotation);
		sinRotation = (float) Math.sin(rotation);
	}

	public void setRotationSpeed(float rotSpeed) {
		rotationSpeed = Math.min(Math.max(rotSpeed, -maxRotationSpeed), maxRotationSpeed);
	}

	public void translate(float x, float y) {
		setLocation(location.x + x, location.y + y);
		setCenter(center.x + x, center.y + y);
	}

	public void rotate(float rotation) {
		setRotation(this.rotation + rotation);
	}

	public void update(float delta) {
		translate(velocity.x * delta, velocity.y * delta);
		rotate(rotationSpeed * delta);

		minX = Float.MAX_VALUE;
		minY = Float.MAX_VALUE;
		maxX = Float.MIN_VALUE;
		maxY = Float.MIN_VALUE;
		boolean x_coord = true;
		for (float f : getPolyVertices()) {
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

		polygon.setPosition(location.x, location.y);
		polygon.setRotation(rotation);
		polygon.setOrigin(location.x - center.x, location.y - center.y);

		// Update age
		age += delta;
		if (age > maxAge)
			alive = false;
	}

	public abstract void collide(Entity other);

	public abstract void input(Input input, int delta);
}
