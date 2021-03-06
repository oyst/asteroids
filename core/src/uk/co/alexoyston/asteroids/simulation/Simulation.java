package uk.co.alexoyston.asteroids.simulation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public class Simulation implements Disposable, EntityListener {

	private static final String TAG = Simulation.class.getName();
	public LinkedList<Entity> entities = new LinkedList<Entity>();
	private LinkedList<Entity> waitingEntities = new LinkedList<Entity>();
	public ArrayList<Player> players = new ArrayList<Player>();

	public static final Random rand = new Random();
	protected static long seed;

	private boolean paused = false;

	private int level = 0;

	private final int asteroidStartNum;
	private final int asteroidStartSize;
	private final float asteroidMinSpeed;
	private final float asteroidMaxSpeed;

	private int saucersOnField = 0;
	private final int saucersMax;
	private float saucersProbSmall;
	private float saucersFreq;

	private final float updateLimiter;

	public final Rectangle bounds;

	private final PhysicsParams params;

	public Simulation(PhysicsParams params) {
		this(params, System.currentTimeMillis());
	}

	public Simulation(PhysicsParams params, long seed) {
		this.seed = seed;
		rand.setSeed(seed);

		this.params = params;

		// TODO: Having the x,y of bounds == 0, 0 causes a problem with the
		// bounds checks
		// Find out why and if there is a way to fix it
		bounds = new Rectangle(1, 1, this.params.worldWidth - 2, this.params.worldHeight - 2);

		asteroidStartNum = this.params.asteroidStartCount;
		asteroidStartSize = this.params.asteroidStartSize;
		asteroidMinSpeed = this.params.asteroidMinSpeed;
		asteroidMaxSpeed = this.params.asteroidMaxSpeed;

		saucersMax = this.params.saucersMax;

		updateLimiter = this.params.updateDelta;

		level = 0;

		nextLevel();
	}

	public void nextLevel() {
		level++;

		saucersFreq = params.saucersFreqInitial + (params.saucersFreqIncrease * level);
		saucersFreq = Math.min(1, saucersFreq);

		saucersProbSmall = params.saucersProbSmallInitial + (params.saucersProbSmallIncrease * level);
		saucersProbSmall = Math.min(1, saucersProbSmall);

		for (int i = 0; i < (asteroidStartNum - 1 + level); i++) {
			Asteroid asteroid = new Asteroid(params);
			float x, y, vx, vy;

			// Start in a random direction
			double angle = rand.nextDouble() * 2 * Math.PI;
			float sinAngle = (float) Math.sin(angle);
			float cosAngle = (float) Math.cos(angle);

			// Random bounded velocity
			vx = sinAngle * (asteroidMinSpeed + rand.nextFloat() * (asteroidMaxSpeed - asteroidMinSpeed));
			vy = cosAngle * (asteroidMinSpeed + rand.nextFloat() * (asteroidMaxSpeed - asteroidMinSpeed));

			// We want to come from either the top/bottom or left/right
			// The stochasticity of the velocity should even out the left:right
			// and top:bottom pairs
			if (rand.nextFloat() > 0.5) {
				// From the top/bottom so y is fixed
				x = bounds.getX() + rand.nextFloat() * bounds.getWidth();
				y = bounds.getHeight();
			} else {
				// From the left/right so x is fixed
				x = bounds.getWidth();
				y = bounds.getY() + rand.nextFloat() * bounds.getHeight();
			}

			asteroid.location.set(x, y);
			asteroid.velocity.set(vx, vy);
			requestEntity(asteroid);
		}
	}

	/**
	 * Add a new controllable player to the simulation
	 *
	 * @return The ID of the player to be used when requesting movement
	 */
	public int addPlayer(float x, float y) {
		int id = players.size();
		Player player = new Player(params);
		player.location.set(x - player.bounds.width / 2, y - player.bounds.height / 2);
		player.spawnLocation.set(player.location);
		players.add(player);
		requestEntity(player);
		return id;
	}

	public void update(float delta) {
		//TODO: Refactor the whole update loop
		// Maybe separate lists for each entity type??

		if (paused)
			return;

		if (delta < updateLimiter) {
			try {
			    Thread.sleep((int) (1000 * updateLimiter - delta));
			} catch(InterruptedException e) {
			    Thread.currentThread().interrupt();
			}
			delta = updateLimiter;
		}

		entities.addAll(waitingEntities);
		waitingEntities.clear();

		ListIterator<Entity> i;
		ListIterator<Entity> j;

		// Add saucers
		if (rand.nextFloat() < saucersFreq * delta && saucersOnField < saucersMax) {
			Entity saucer;
			if (rand.nextFloat() < saucersProbSmall)
				saucer = new SmallSaucer(params);
			else
				saucer = new Saucer(params);
			saucer.location.y = bounds.y + rand.nextFloat() * bounds.height;
			saucer.location.x = (rand.nextFloat() < 0.5) ? bounds.x - saucer.bounds.width : bounds.width;
			requestEntity(saucer);
			saucersOnField++;
		}

		// AI
		i = entities.listIterator();
		while (i.hasNext()) {
			Entity entity = i.next();
			if (entity instanceof Saucer) {
				Saucer saucer = (Saucer)entity;
				if (!players.isEmpty()) {
					int index = rand.nextInt(players.size());
					Player player = players.get(index);
					saucer.shoot(player.location.x, player.location.y);
				}
			}
		}

		i = entities.listIterator();
		while (i.hasNext()) {
			Entity entity = i.next();

			// Update age
			entity.age += delta;
			if (entity.age > entity.maxAge)
				entity.decay();

			if (!entity.alive) {
				i.remove();
				if (entity instanceof Player)
					players.remove(entity);
				if (entity instanceof Saucer)
					saucersOnField--;
				continue;
			}

			entity.checkBounds(bounds);
			entity.update(delta);

			entity.updateVertices();
		}

		// Collision detection
		// This needs to be its own iteration to give every entity
		// the chance to update first
		i = entities.listIterator();
		while (i.hasNext()) {
			j = entities.listIterator(i.nextIndex());
			Entity entity = i.next();
			while (j.hasNext()) {
				Entity other = j.next();
				if (entity.collides(other))
					entity.collision(other);
				if (other.collides(entity))
					other.collision(entity);
			}
		}

		if (fieldClear())
			nextLevel();
	}

	public boolean fieldClear() {
		// Check if the field is clear
		ListIterator<Entity> i = entities.listIterator();
		while (i.hasNext()) {
			Entity entity = i.next();
			if (entity instanceof Player || entity instanceof Bullet)
				continue;
			return false;
		}
		return true;
	}

	public void pause() {
		paused = true;
	}

	public void resume() {
		paused = false;
	}

	@Override
	public void dispose() {
	}

	public void playerFwd(int playerId) {
		Player player = players.get(playerId);
		player.acceleration.x = (float) Math.sin(player.rotation) * params.playerThrustPower;
		player.acceleration.y = (float) Math.cos(player.rotation) * params.playerThrustPower;
	}

	public void playerRotLeft(int playerId) {
		Player player = players.get(playerId);
		player.rotationSpeed = -params.playerRotationPower;
	}

	public void playerRotRight(int playerId) {
		Player player = players.get(playerId);
		player.rotationSpeed = params.playerRotationPower;
	}

	public void playerShoot(int playerId) {
		 Player player = players.get(playerId);
		 player.shoot();
	}

	public void playerWarp(int playerId) {
			Player player = players.get(playerId);
			player.warp(bounds);
	}

	public int getScore(int playerId) {
		Player player = players.get(playerId);
		return player.getScore();
	}

	@Override
	public void requestEntity(Entity entity) {
		waitingEntities.add(entity);
		entity.registerListener(this);
	}

	public long getSeed() {
		return seed;
	}
}
