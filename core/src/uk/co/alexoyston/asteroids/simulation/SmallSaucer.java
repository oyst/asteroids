package uk.co.alexoyston.asteroids.simulation;

import com.badlogic.gdx.math.Vector2;

public class SmallSaucer extends Entity {

	private float speed = 35;
	private float angle = (float) Math.PI / 6;
	private long freqAngleChange = 1000; // Milliseconds
	private long lastAngleChange = 0;

	private long freqShot = 700;
	private long lastShot = 0;
	private float shotSpeed = 250;

	public SmallSaucer() {
		int width = 25;
		int height = 15;

		float a = width/5;
		float b = width/3;
		float c = height/3;

		float[] vertices = new float[] {
			width, c,
			0, c,
			a, 0,
			width - a, 0,
			width, c,
			width - a, height - c,
			a, height - c,
			0, c,
			a, height - c,
			b, height,
			width - b, height,
			width - a, height - c
		};

		setVertices(vertices);

		center.set(width/2, height/2);
	}

	@Override
	public void update(float delta) {
		long now = System.currentTimeMillis();
		if (now - lastAngleChange < freqAngleChange)
			return;

		lastAngleChange = now;

		float r = (float)Math.random();
		if (r < 0.33f)
			angle = (float)Math.PI / 6;
		else if (r < 0.66f)
			angle = -(float)Math.PI / 6;
		else
			angle = 0;

		velocity.setAngleRad(angle).setLength(speed);
	}

	public void shoot(float targetX, float targetY) {
		long now = System.currentTimeMillis();
		if (now - lastShot < freqShot)
			return;

		lastShot = now;

		Bullet bullet = new Bullet();
		float angle = (float)(Math.atan2(targetX - location.x, targetY - location.y));

		Vector2 shotVelocity = new Vector2(shotSpeed, 0).setAngleRad((float)Math.PI/2 - angle);
		bullet.velocity.set(shotVelocity);

		Vector2 radius = new Vector2(bounds.width/2, bounds.height/2).setAngleRad((float)Math.PI/2 - angle);
		bullet.location.set(location).add(center).add(radius);

		entityListener.requestEntity(bullet);
	}

	@Override
	public void collision(Entity other) {
	}

	@Override
	public boolean collides(Entity other) {
		return super.collides(other);
	}

}
