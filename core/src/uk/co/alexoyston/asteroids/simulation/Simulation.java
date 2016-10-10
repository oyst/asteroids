package uk.co.alexoyston.asteroids.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;

public class Simulation implements Disposable {

	private static final String TAG = Simulation.class.getName();
	
	public Simulation() {
	}
	
	public void update(float delta) {
	}

	public void pause() {
		Gdx.app.log(TAG, "Paused");
	}
	
	public void resume() {
		Gdx.app.log(TAG, "Resumed");
	}

	@Override
	public void dispose() {
	}
	
}