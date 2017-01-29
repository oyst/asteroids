package uk.co.alexoyston.asteroids.simulation;

import com.badlogic.gdx.math.Vector2;

public class Saucer extends Entity {

	private float speed;
	private float angle = (float) Math.PI / 6;
	private float turnFreq;
	private float deltaSinceTurn = 0;

	private float reloadTime;
	private float deltaSinceShot = 0;
	private float shotSpeed;

	public Saucer(PhysicsParams params) {
		speed = params.saucerSpeed;
		turnFreq = params.saucerTurnFreq;
		reloadTime = params.saucerReloadTime;
		shotSpeed = params.saucerShotSpeed;

		int width = 40;
		int height = 20;

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
		deltaSinceShot += delta;
		deltaSinceTurn += delta;

		shoot();

		if (deltaSinceTurn < turnFreq)
			return;

		deltaSinceTurn = 0;

		float r = (float)Math.random();
		if (r < 0.33f)
			angle = (float)Math.PI / 6;
		else if (r < 0.66f)
			angle = -(float)Math.PI / 6;
		else
			angle = 0;

		velocity.setAngleRad(angle).setLength(speed);
	}

	public void shoot() {
		if (deltaSinceShot < reloadTime)
			return;

		deltaSinceShot = 0;

		Bullet bullet = new Bullet(this);

		bullet.velocity.setToRandomDirection().scl(shotSpeed);

		Vector2 radius = new Vector2(bounds.width/2, bounds.height/2).setAngleRad(bullet.velocity.angleRad());
		bullet.location.set(location).add(center).add(radius);

		entityListener.requestEntity(bullet);
	}

	@Override
	public void collision(Entity other) {
		alive = false;
	}

	@Override
	public boolean collides(Entity other) {
		return super.collides(other);
	}
}
