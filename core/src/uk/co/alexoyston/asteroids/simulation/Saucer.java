package uk.co.alexoyston.asteroids.simulation;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public class Saucer extends Entity implements BulletShooter {

	protected float speed;
	protected float angle = (float) Math.PI / 6;
	protected float turnFreq;
	protected float deltaSinceTurn = 0;

	protected float reloadTime;
	protected float deltaSinceShot = 0;
	protected float shotSpeed;
	protected float shotAccuracy;

	protected Saucer(){};

	public Saucer(PhysicsParams params) {
		speed = params.saucerSpeed;
		turnFreq = params.saucerTurnFreq;
		reloadTime = params.saucerReloadTime;
		shotSpeed = params.saucerShotSpeed;
		shotAccuracy = 0;

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

		velocity.x = speed;

		center.set(width/2, height/2);
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		
		deltaSinceShot += delta;
		deltaSinceTurn += delta;

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

	public void shoot(float targetX, float targetY) {
		if (deltaSinceShot < reloadTime)
			return;

		deltaSinceShot = 0;

		Bullet bullet = new Bullet(this);
		float angle = (float)(Math.atan2(targetX - location.x, targetY - location.y));

		Random rand = new Random();
		angle += (1f - shotAccuracy) * (-Math.PI + (2*Math.PI) * rand.nextDouble());

		Vector2 shotVelocity = new Vector2(shotSpeed, 0).setAngleRad((float)Math.PI/2 - angle);
		bullet.velocity.set(shotVelocity);

		Vector2 radius = new Vector2(bounds.width/2, bounds.height/2).setAngleRad((float)Math.PI/2 - angle);
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

	@Override
	public void onBulletHit(Bullet bullet, Entity hitEntity) {
	}

	@Override
	public void onBulletDecay(Bullet bullet) {
	}

	@Override
	public boolean checkBounds(Rectangle worldBounds) {
		boolean inBounds = super.checkBounds(worldBounds);
		if (!inBounds)
			this.decay();

		return inBounds;
	}
}
