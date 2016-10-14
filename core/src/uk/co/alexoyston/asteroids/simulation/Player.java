package uk.co.alexoyston.asteroids.simulation;

import com.badlogic.gdx.math.Vector2;

public class Player extends Entity {

	private float deltaSinceShot = 0;
	protected float weaponTemp = 0;
	protected final float shotSpeed = 600f;
	protected final float shotHeat = 30;
	protected final float reloadTime = 0.3f;
	protected final float coolDown = 20;

	protected final Vector2 spawnLocation = new Vector2(0, 0);
	private final float spawnProtectDuration = 1f;
	private float spawnProtectRemaining = 0f;
	protected int remainingLives = 3;

	public Player() {
		float width = 20;
		float height = 20;

		float[] vertices = new float[] { 
			width / 2, height / 2,
			0, 0,
			width / 2, height,
			width, 0
		};
		
		setVertices(vertices);

		center.set(width/2, height/2);

		drag = 0.3f;
	}

	public void respawn() {
		if (remainingLives <= 0) {
			alive = false;
			return;
		}
		remainingLives--;
		location.set(spawnLocation);
		weaponTemp = 0;
		deltaSinceShot = 0;
		velocity.setZero();
		acceleration.setZero();
		rotation = 0;
		rotationSpeed = 0;
		spawnProtectRemaining = spawnProtectDuration;
	}

	public void shoot() {
		if (weaponTemp > 100 - shotHeat)
			return;

		if (deltaSinceShot < reloadTime)
			return;
		deltaSinceShot = 0;

		weaponTemp += shotHeat;

		Bullet bullet = new Bullet();

		Vector2 shotVelocity = new Vector2(shotSpeed, 0).setAngleRad((float)Math.PI/2 - rotation);
		bullet.velocity.set(velocity).add(shotVelocity);

		Vector2 radius = new Vector2(bounds.width/2, bounds.height/2).setAngleRad((float)Math.PI/2 - rotation);
		bullet.location.set(location).add(center).add(radius);

		entityListener.requestEntity(bullet);
	}

	@Override
	public void update(float delta) {
		weaponTemp -= coolDown * delta;
		deltaSinceShot += delta;
		spawnProtectRemaining = Math.max(spawnProtectRemaining - delta, 0);
	}

	@Override
	public boolean collides(Entity other) {
		if (spawnProtectRemaining > 0)
			return false;
		if (other instanceof Player)
			return false;
		return super.collides(other);
	}

	@Override
	public void collision(Entity other) {
		respawn();
	}
}
