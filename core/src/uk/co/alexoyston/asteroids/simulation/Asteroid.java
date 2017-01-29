package uk.co.alexoyston.asteroids.simulation;

import java.util.ArrayList;

public class Asteroid extends Entity {
	private int numSplits;
	private float splitBoost;

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

	public Asteroid(PhysicsParams params) {
		this(params.asteroidStartSize, params.asteroidSplitBoost);
	}

	public Asteroid(int numSplits, float splitBoost) {
		this.splitBoost = splitBoost;
		this.numSplits = numSplits;

		int index = (int) (Math.random() * asteroidShapes.size());

		float[] vertices = new float[asteroidShapes.get(index).length];
		System.arraycopy(asteroidShapes.get(index), 0, vertices, 0, asteroidShapes.get(index).length);

		for (int i = 0; i < vertices.length; i++)
			vertices[i] *= (this.numSplits + 1);

		setVertices(vertices);

		center.set(bounds.width/2, bounds.height/2);
	}

	public void split() {
		numSplits--;
		alive = false;

		if (numSplits < 0)
			return;

		for (int i = 0; i < 2; i++) {
			Asteroid child = new Asteroid(numSplits, splitBoost);

			// Get an extra boost in a random direction
			float angle = (float) (Math.random() * 2 * Math.PI);
			child.location.set(location);
			child.velocity.set(velocity).add(splitBoost, splitBoost).setAngleRad(angle);
			child.rotation = angle;

			entityListener.requestEntity(child);
		}
	}

	@Override
	public boolean collides(Entity other) {
		if (other instanceof Asteroid)
			return false;
		return super.collides(other);
	}

	@Override
	public void collision(Entity other) {
		split();
	}
}
