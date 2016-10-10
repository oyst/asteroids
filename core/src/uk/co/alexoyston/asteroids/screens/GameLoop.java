package uk.co.alexoyston.asteroids.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.InputAdapter;

import uk.co.alexoyston.asteroids.Renderer;
import uk.co.alexoyston.asteroids.simulation.Simulation;

public class GameLoop extends BaseScreen {

	private Simulation simulation;
	private Renderer renderer;
	private InputAdapter playInputAdapter;
	private InputAdapter pauseInputAdapter;

	private final BitmapFont font = new BitmapFont();
	private final SpriteBatch batch = new SpriteBatch();
	private final GlyphLayout layout = new GlyphLayout();

	private final int KEY_PAUSE = Keys.ESCAPE;
	private final int KEY_RESUME = Keys.SPACE;
	private final int KEY_EXIT = Keys.ESCAPE;

	private final static String RESUME_GAME = "Press <SPACE> to resume";
	private final static String EXIT_GAME = "Press <ESC> to exit";

	public GameLoop(Game game) {
		super(game);

		simulation = new Simulation();
		renderer = new Renderer(simulation);

		playInputAdapter = new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				switch (keycode) {
				case KEY_PAUSE:
					pause();
					break;
				}
				return false;
			}
		};

		pauseInputAdapter = new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				switch (keycode) {
				case KEY_RESUME:
					resume();
					break;
				case KEY_EXIT:
					nextState = State.MAIN_MENU;
					break;
				}
				return false;
			}
		};

		batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		Gdx.input.setInputProcessor(playInputAdapter);
	}

	/**
	 * Update the simulation
	 * 
	 * @param delta
	 *            The time in ms since the last update
	 */
	@Override
	public void update(float delta) {
		simulation.update(delta);
	}

	/**
	 * Draw the simulations state
	 * 
	 * @param delta
	 *            The time in ms since the last update
	 */
	@Override
	public void draw(float delta) {
		renderer.render(simulation, delta);
		if (paused) {
			float width;
			float height;

			batch.begin();

			layout.setText(font, RESUME_GAME);
			width = layout.width;
			height = layout.height;
			font.draw(batch, RESUME_GAME, (Gdx.graphics.getWidth() - width) / 2, Gdx.graphics.getHeight() / 2 + height);

			layout.setText(font, EXIT_GAME);
			width = layout.width;
			height = layout.height;
			font.draw(batch, EXIT_GAME, (Gdx.graphics.getWidth() - width) / 2, Gdx.graphics.getHeight() / 2 - height);

			batch.end();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		simulation.dispose();
		renderer.dispose();
		Gdx.input.setInputProcessor(null);
		font.dispose();
		batch.dispose();
	}

	@Override
	public void pause() {
		super.pause();
		simulation.pause();
		Gdx.input.setInputProcessor(pauseInputAdapter);
	}

	@Override
	public void resume() {
		super.resume();
		simulation.resume();
		Gdx.input.setInputProcessor(playInputAdapter);
	}

}