package uk.co.alexoyston.asteroids.simulation;

public class Bullet extends Entity {

	private Entity owner;

	public Bullet(Entity owner) {
		int width = 2;
		int height = 2;

		float[] vertices = new float[] { 
			0, 0,
			width, 0,
			width, height,
			0, height
		};

		setVertices(vertices);

		this.owner = owner;

		maxAge = 1;

		center.set(width/2, height/2);
	}

	@Override
	public boolean collides(Entity other) {
		if (other instanceof Bullet)
			return false;
		return super.collides(other);
	}

	@Override
	public void collision(Entity other) {
		alive = false;
		if (owner instanceof Player) {
			((Player)owner).addHitScore(other);
		}
	}
}
