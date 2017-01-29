package uk.co.alexoyston.asteroids.simulation;

import com.badlogic.gdx.math.Vector2;

public class Player extends Entity {

	private float deltaSinceShot = 0;
	protected float weaponTemp = 0;
	protected final float shotSpeed;
	protected final float shotHeat;
	protected final float reloadTime;
	protected final float coolDown;
	protected final float maxWeaponTemp;

	protected final int asteroidScore;
	protected final int saucerScore;
	protected final int smallSaucerScore;

	protected final Vector2 spawnLocation = new Vector2(0, 0);
	private final float spawnProtectDuration;
	private float spawnProtectRemaining = 0f;
	protected int remainingLives = 0;

	private int score = 0;

	public Player(PhysicsParams params) {
		shotSpeed = params.playerShotSpeed;
		shotHeat = params.playerShotHeat;
		reloadTime = params.playerReloadTime;
		coolDown = params.playerWeaponCoolDownRate;
		spawnProtectDuration = params.playerSpawnProtectDuration;
		maxWeaponTemp = params.playerMaxWeaponHeat;
		asteroidScore = params.playerAsteroidHitScore;
		saucerScore = params.playerSaucerHitScore;
		smallSaucerScore = params.playerSmallSaucerHitScore;
		drag = params.playerDrag;

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
		if (weaponTemp > maxWeaponTemp - shotHeat)
			return;

		if (deltaSinceShot < reloadTime)
			return;
		deltaSinceShot = 0;

		weaponTemp += shotHeat;

		Bullet bullet = new Bullet(this);

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

	public void addHitScore(Entity hit) {
		if (hit instanceof Asteroid)
			score += asteroidScore;
		if (hit instanceof Saucer)
			score += saucerScore;
		if (hit instanceof SmallSaucer)
			score += smallSaucerScore;
	}

	public int getScore() {
		return score;
	}
}
