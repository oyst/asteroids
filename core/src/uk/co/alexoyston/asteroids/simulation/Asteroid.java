package uk.co.alexoyston.asteroids.simulation;

import java.util.ArrayList;

public class Asteroid extends Entity {
	private int numSplits;
	private float splitBoost = 7;

	private static final ArrayList<float[]> asteroidShapes = new ArrayList<float[]>();
	
	static {
		asteroidShapes.add(new float[] {
			6.0f, 15.0f,
			9.0f, 15.0f,
			12.0f, 15.0f,
			15.0f, 11.25f,
			12.0f, 7.5f,
			15.0f, 3.75f,
			12.0f, 0.0f,
			8.4f, 1.8f,
			3.75f, 0.3f,
			0.0f, 6.0f,
			0.0f, 12.0f,
			3.0f, 15.0f,
		});

		asteroidShapes.add(new float[] {
			15.0f, 10.8f,
			15.0f, 9.0f,
			8.4f, 6.3f,
			15.0f, 3.6f,
			12.0f, 0.0f,
			8.4f, 1.8f,
			3.45f, 0.3f,
			0.0f, 6.0f,
			0.0f, 11.1f,
			5.4f, 11.1f,
			4.5f, 14.1f,
			9.0f, 14.1f,
		});
	}

	public Asteroid(int numSplits) {
		this.numSplits = numSplits;
		
		int index = (int) (Math.random() * asteroidShapes.size());
	
		float[] vertices = asteroidShapes.get(index).clone();
		
		for (int i = 0; i < vertices.length; i++) 
			vertices[i] *= (this.numSplits + 1);
		
		setVertices(vertices);
	}

	public void split() {
		numSplits--;
		alive = false;

		if (numSplits < 0)
			return;

		for (int i = 0; i < 2; i++) {
			Asteroid child = new Asteroid(numSplits);

			// Get an extra boost in a random direction
			double angle = Math.random() * 2 * Math.PI;
			float sinAngle = (float) Math.sin(angle);
			float cosAngle = (float) Math.cos(angle);
			float vx = (velocity.x + splitBoost) * sinAngle;
			float vy = (velocity.y + splitBoost) * cosAngle;

			child.location.set(location);
			child.center.set(location.x + child.bounds.width/2, location.y + child.bounds.height/2);
			child.velocity.set(vx, vy);

			entityListener.requestEntity(child);
		}
	}

	@Override
	public boolean collides(Entity other) {
		if (other instanceof Player || other instanceof Bullet) {
			return super.collides(other);
		}
		return false;
	}

	@Override
	public void collision(Entity other) {
		split();
	}
}
