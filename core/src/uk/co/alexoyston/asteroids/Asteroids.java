package uk.co.alexoyston.asteroids;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class Asteroids extends ApplicationAdapter {
	public static final int WORLD_WIDTH = 800;
	public static final int WORLD_HEIGHT = 480;

	@Override
	public void create() {
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
}
