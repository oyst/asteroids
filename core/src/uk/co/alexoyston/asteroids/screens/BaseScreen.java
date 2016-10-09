package uk.co.alexoyston.asteroids.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/**
 * Common class for a game screen.
 */
public abstract class BaseScreen implements Screen {
	protected Game game;
	protected State nextScreen = State.UNSET;

	public BaseScreen(Game game) {
		this.game = game;
	}

	public abstract void update(float delta);

	public abstract void draw(float delta);

	public final boolean finished() {
		return (nextScreen != State.UNSET);
	}

	public State nextScreen() {
		return nextScreen;
	}

	@Override
	public void render(float delta) {
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
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void resize(int width, int height) {
	}

}