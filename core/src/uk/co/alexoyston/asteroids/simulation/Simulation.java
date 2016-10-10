package uk.co.alexoyston.asteroids.simulation;

import java.util.ArrayList;
import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public class Simulation implements Disposable {

	private static final String TAG = Simulation.class.getName();
	public LinkedList<Entity> entities = new LinkedList<Entity>();
	public ArrayList<Player> players = new ArrayList<Player>();

	private boolean paused = false;

	public final Rectangle bounds;

	public Simulation() {
		// TODO: Having the x,y of bounds == 0, 0 causes a problem with the
		// bounds checks
		// Find out why and if there is a way to fix it
		bounds = new Rectangle(1, 1, Gdx.graphics.getWidth() - 2, Gdx.graphics.getHeight() - 2);
		addPlayer();
	}

	/**
	 * Add a new controllable player to the simulation
	 * 
	 * @return The ID of the player to be used when requesting movement
	 */
	public int addPlayer() {
		int id = players.size();
		Gdx.app.log(TAG, "Player " + id + " added");
		Player player = new Player();
		player.location.set(50, 50);
		player.center.set(60, 60);
		entities.add(player);
		players.add(player);
		return id;
	}

	public void update(float delta) {
		if (paused)
			return;

		for (Entity entity : entities) {
			// Update location, center and velocity
			float ax_d = entity.acceleration.x * delta;
			float ay_d = entity.acceleration.y * delta;
			float x_offset = entity.velocity.x * delta + ax_d * delta * 0.5f;
			float y_offset = entity.velocity.y * delta + ay_d * delta * 0.5f;

			entity.location.x += x_offset;
			entity.location.y += y_offset;

			entity.center.x += x_offset;
			entity.center.y += y_offset;

			entity.velocity.x += ax_d;
			entity.velocity.y += ay_d;

			entity.rotation += entity.rotationSpeed * delta;
			entity.rotation %= (Math.PI) * 2;

			// Update age
			entity.age += delta;
			if (entity.age > entity.maxAge)
				entity.alive = false;

			// Reset acceleration to 0 after each update
			entity.acceleration.set(0, 0);
			entity.rotationSpeed = 0;

			entity.velocity.x -= entity.velocity.x * entity.drag * delta;
			entity.velocity.y -= entity.velocity.y * entity.drag * delta;

			// Bounds check
			float boundsOffsetX = 0;
			float boundsOffsetY = 0;

			if (entity.bounds.x > bounds.x + bounds.width)
				boundsOffsetX = -(bounds.width + entity.bounds.width);
			else if (entity.bounds.x + entity.bounds.width < bounds.x) {
				boundsOffsetX = (bounds.width + entity.bounds.width);
			}
			System.out.println(entity.bounds);
			System.out.println(bounds);

			if (entity.bounds.y > bounds.y + bounds.height)
				boundsOffsetY = -(bounds.height + entity.bounds.height);
			else if (entity.bounds.y + entity.bounds.height < bounds.y)
				boundsOffsetY = (bounds.height + entity.bounds.height);

			entity.location.x += boundsOffsetX;
			entity.location.y += boundsOffsetY;

			entity.center.x += boundsOffsetX;
			entity.center.y += boundsOffsetY;

			entity.updateVertices();
		}

	}

	public void pause() {
		Gdx.app.log(TAG, "Paused");
		paused = true;
	}

	public void resume() {
		Gdx.app.log(TAG, "Resumed");
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

}