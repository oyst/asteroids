package uk.co.alexoyston.asteroids.simulation;

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

		velocity.x = (float) Math.cos(angle) * speed;
		velocity.y = (float) Math.sin(angle) * speed;
	}

	public void shoot(float targetX, float targetY) {
		long now = System.currentTimeMillis();
		if (now - lastShot < freqShot)
			return;

		lastShot = now;

		Bullet bullet = new Bullet();
		bullet.location.set(location.x + center.x, location.y + center.y);
		bullet.velocity.set(targetX - location.x, targetY - location.y);
		bullet.velocity.nor().scl(shotSpeed).add(velocity);

		entityListener.requestEntity(bullet);
	}

	@Override
	public void collision(Entity other) {
	}

	@Override
	public boolean collides(Entity other) {
		return false;
	}

}
