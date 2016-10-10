package uk.co.alexoyston.asteroids.simulation;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;

public class Simulation implements Disposable {

	private static final String TAG = Simulation.class.getName();
	public LinkedList<Entity> entities = new LinkedList<Entity>();

	private boolean paused = false;

	public Simulation() {
		entities.add(new Player());
	}

	public void update(float delta) {
		if (paused)
			return;
		
		for (Entity entity : entities) 
			entity.update(delta);
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

}