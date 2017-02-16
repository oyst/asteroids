package uk.co.alexoyston.asteroids.simulation;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public class Player extends Entity implements BulletShooter {

	protected int activeShots = 0;
	protected final float shotSpeed;
	protected final int maxActiveShots;

	protected int remainingWarps;

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
		remainingWarps = params.playerWarpCount;

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
		super.update(delta);
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
			score += asteroidScore / ((Asteroid)hit).size();
		else if (hit instanceof SmallSaucer)
			score += smallSaucerScore;
		else if (hit instanceof Saucer)
			score += saucerScore;
	}

	public int getScore() {
		return score;
	}

	public void warp(Rectangle worldBounds) {
		if (remainingWarps == 0)
			return;
		remainingWarps--;

		Random rand = new Random();
		location.x = worldBounds.x + (worldBounds.width - worldBounds.x) * rand.nextFloat();
		location.y = worldBounds.y + (worldBounds.height - worldBounds.y) * rand.nextFloat();
	}

	public int remainingShots() {
		return maxActiveShots - activeShots;
	}

	public int remainingWarps() {
		return remainingWarps;
	}
}
