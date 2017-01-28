package uk.co.alexoyston.asteroids.simulation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public class Simulation implements Disposable, EntityListener {

	private static final String TAG = Simulation.class.getName();
	public LinkedList<Entity> entities = new LinkedList<Entity>();
	private LinkedList<Entity> waitingEntities = new LinkedList<Entity>();
	public ArrayList<Player> players = new ArrayList<Player>();

	private boolean paused = false;

	private int level = 0;

	private int asteroidStartNum = 4;
	private int asteroidStartSize = 2;
	private float asteroidMinSpeed = 15;
	private float asteroidMaxSpeed = 25;

	private int saucersOnField = 0;
	private int saucersMax = 0;
	private float saucersProbSmall = 0.3f;
	private float saucersFreq = 0.5f;
	private float saucersSpeed = 25;

	public final Rectangle bounds;
	
	private PhysicsParams params;

	public Simulation(PhysicsParams params) {
		this.params = params;
		// TODO: Having the x,y of bounds == 0, 0 causes a problem with the
		// bounds checks
		// Find out why and if there is a way to fix it
		bounds = new Rectangle(1, 1, this.params.worldWidth - 2, this.params.worldHeight - 2);

		level = 0;

		nextLevel();
	}

	public void nextLevel() {
		level++;
		for (int i = 0; i < (asteroidStartNum + level); i++) {
			Asteroid asteroid = new Asteroid(asteroidStartSize);
			float x, y, vx, vy;

			// Start in a random direction
			double angle = Math.random() * 2 * Math.PI;
			float sinAngle = (float) Math.sin(angle);
			float cosAngle = (float) Math.cos(angle);

			// Random bounded velocity
			vx = sinAngle * (asteroidMinSpeed + (float) Math.random() * (asteroidMaxSpeed - asteroidMinSpeed));
			vy = cosAngle * (asteroidMinSpeed + (float) Math.random() * (asteroidMaxSpeed - asteroidMinSpeed));

			// We want to come from either the top/bottom or left/right
			// The stochasticity of the velocity should even out the left:right
			// and top:bottom pairs
			if (Math.random() > 0.5) {
				// From the top/bottom so y is fixed
				x = bounds.getX() + (float) Math.random() * bounds.getWidth();
				y = bounds.getHeight();
			} else {
				// From the left/right so x is fixed
				x = bounds.getWidth();
				y = bounds.getY() + (float) Math.random() * bounds.getHeight();
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
		//Gdx.app.log(TAG, "Player " + id + " added");
		Player player = new Player();
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

		entities.addAll(waitingEntities);
		waitingEntities.clear();

		ListIterator<Entity> i;
		ListIterator<Entity> j;

		// Add saucers
		if (Math.random() < saucersFreq * delta && saucersOnField < saucersMax) {
			Entity saucer;
			if (Math.random() < saucersProbSmall)
				saucer = new SmallSaucer();
			else
				saucer = new Saucer();
			saucer.location.y = bounds.y + (float)Math.random() * bounds.height;
			saucer.location.x = (Math.random() < 0.5) ? bounds.x - saucer.bounds.width : bounds.width;
			saucer.velocity.x = saucersSpeed;
			requestEntity(saucer);
			saucersOnField++;
		}

		// AI
		i = entities.listIterator();
		while (i.hasNext()) {
			Entity entity = i.next();
			if (entity instanceof SmallSaucer) {
				SmallSaucer saucer = (SmallSaucer)entity;
				if (!players.isEmpty()) {
					int index = (int) (Math.random() * players.size());
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
				entity.alive = false;

			if (!entity.alive) {
				i.remove();
				if (entity instanceof Player)
					players.remove(entity);
				if (entity instanceof Saucer || entity instanceof SmallSaucer)
					saucersOnField--;
				continue;
			}

			// Update location, center and velocity
			float ax_d = entity.acceleration.x * delta;
			float ay_d = entity.acceleration.y * delta;

			entity.location.x += entity.velocity.x * delta + ax_d * delta * 0.5f;
			entity.location.y += entity.velocity.y * delta + ay_d * delta * 0.5f;

			entity.velocity.x += ax_d;
			entity.velocity.y += ay_d;

			entity.rotation += entity.rotationSpeed * delta;
			entity.rotation %= (Math.PI) * 2;

			// Reset acceleration to 0 after each update
			entity.acceleration.set(0, 0);
			entity.rotationSpeed = 0;

			entity.velocity.x -= entity.velocity.x * entity.drag * delta;
			entity.velocity.y -= entity.velocity.y * entity.drag * delta;

			// Bounds check
			float offsetX = 0;
			float offsetY = 0;
			if (entity.bounds.x > bounds.x + bounds.width)
				offsetX = -(bounds.width + entity.bounds.width);
			else if (entity.bounds.x + entity.bounds.width < bounds.x)
				offsetX = (bounds.width + entity.bounds.width);

			if (entity.bounds.y > bounds.y + bounds.height)
				offsetY = -(bounds.height + entity.bounds.height);
			else if (entity.bounds.y + entity.bounds.height < bounds.y)
				offsetY = (bounds.height + entity.bounds.height);

			//TODO: Refactor this
			// Remove the saucers if they go out of bounds
			if ((entity instanceof Saucer || entity instanceof SmallSaucer) &&
				(offsetX != 0 || offsetY != 0)) {
				i.remove();
				saucersOnField--;
			}

			entity.location.add(offsetX, offsetY);

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
		//Gdx.app.log(TAG, "Paused");
		paused = true;
	}

	public void resume() {
		//Gdx.app.log(TAG, "Resumed");
		paused = false;
	}

	@Override
	public void dispose() {
	}

	public void playerFwd(int playerId) {
		Player player = players.get(playerId);
		player.acceleration.x = (float) Math.sin(player.rotation) * 120f;
		player.acceleration.y = (float) Math.cos(player.rotation) * 120f;
	}

	public void playerRotLeft(int playerId) {
		Player player = players.get(playerId);
		player.rotationSpeed = -3f;
	}

	public void playerRotRight(int playerId) {
		Player player = players.get(playerId);
		player.rotationSpeed = 3f;
	}

	public void playerShoot(int playerId) {
		 Player player = players.get(playerId);
		 player.shoot();
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
}