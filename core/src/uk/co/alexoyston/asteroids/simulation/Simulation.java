package uk.co.alexoyston.asteroids.simulation;

import java.util.ArrayList;
import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;

public class Simulation implements Disposable {

	private static final String TAG = Simulation.class.getName();
	public LinkedList<Entity> entities = new LinkedList<Entity>();
	public ArrayList<Player> players = new ArrayList<Player>();

	private boolean paused = false;

	public Simulation() {
		addPlayer();
	}

	/**
	 * Add a new controlable player to the simulation
	 * 
	 * @return The ID of the player to be used when requesting movement
	 */
	public int addPlayer() {
		int id = players.size();
		Gdx.app.log(TAG, "Player " + id + " added");
		Player player = new Player();
		entities.add(player);
		players.add(player);
		return id;
	}

	public void update(float delta) {
		if (paused)
			return;

		for (Entity entity : entities) {
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
			
			// Update age
			entity.age += delta;
			if (entity.age > entity.maxAge)
				entity.alive = false;
			
			// Reset acceleration to 0 after each update
			entity.acceleration.set(0, 0);
			
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
		player.acceleration.y = 100f;
	}

}