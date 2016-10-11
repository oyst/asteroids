package uk.co.alexoyston.asteroids.simulation;

public class Bullet extends Entity {

	public Bullet() {
		int width = 2;
		int height = 2;

		float[] vertices = new float[] { 
			0, 0,
			width, 0,
			width, height,
			0, height
		};

		setVertices(vertices);

		maxAge = 1;

		center.set(width/2, height/2);
	}

	@Override
	public boolean collides(Entity other) {
		if (other instanceof Asteroid) {
			return super.collides(other);
		}
		return false;
	}

	@Override
	public void collision(Entity other) {
		alive = false;
	}
}
