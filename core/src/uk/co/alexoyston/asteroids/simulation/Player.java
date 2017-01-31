package uk.co.alexoyston.asteroids.simulation;

import com.badlogic.gdx.math.Vector2;

public class Player extends Entity implements BulletShooter {

	public int activeShots = 0;
	protected final float shotSpeed;
	protected final int maxActiveShots;

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
		spawnProtectDuration = params.playerSpawnProtectDuration;
		asteroidScore = params.playerAsteroidHitScore;
		saucerScore = params.playerSaucerHitScore;
		smallSaucerScore = params.playerSmallSaucerHitScore;
		drag = params.playerDrag;
		maxActiveShots = params.playerMaxActiveShots;

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
		velocity.setZero();
		acceleration.setZero();
		activeShots = 0;
		rotation = 0;
		rotationSpeed = 0;
		spawnProtectRemaining = spawnProtectDuration;
	}

	public void shoot() {
		if (activeShots >= maxActiveShots)
			return;

		activeShots++;

		Bullet bullet = new Bullet(this);

		Vector2 shotVelocity = new Vector2(shotSpeed, 0).setAngleRad((float)Math.PI/2 - rotation);
		bullet.velocity.set(velocity).add(shotVelocity);

		Vector2 radius = new Vector2(bounds.width/2, bounds.height/2).setAngleRad((float)Math.PI/2 - rotation);
		bullet.location.set(location).add(center).add(radius);

		entityListener.requestEntity(bullet);
	}

	@Override
	public void onBulletHit(Bullet bullet, Entity target) {
		activeShots--;
	}

	@Override
	public void onBulletDecay(Bullet bullet) {
		activeShots--;
	}

	@Override
	public void update(float delta) {
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
