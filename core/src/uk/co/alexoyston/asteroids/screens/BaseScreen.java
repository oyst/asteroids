package uk.co.alexoyston.asteroids.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/**
 * Common class for a game screen.
 */
public abstract class BaseScreen implements Screen {
	protected Game game;
	protected State nextState = State.UNSET;
	protected boolean paused;

	public BaseScreen(Game game) {
		this.game = game;
	}

	public abstract void update(float delta);

	public abstract void draw(float delta);

	public final boolean finished() {
		return (nextState != State.UNSET);
	}

	public State nextScreen() {
		return nextState;
	}

	@Override
	public final void render(float delta) {
		update(delta);
		draw(delta);
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
		paused = true;
	}

	@Override
	public void resume() {
		paused = false;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void resize(int width, int height) {
	}

}