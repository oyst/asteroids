package uk.co.alexoyston.asteroids.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.InputAdapter;

import uk.co.alexoyston.asteroids.Renderer;
import uk.co.alexoyston.asteroids.simulation.PhysicsParams;
import uk.co.alexoyston.asteroids.simulation.Simulation;

public class GameLoop extends BaseScreen {

	private Simulation simulation;
	private Renderer renderer;
	private InputAdapter playInputAdapter;
	private InputAdapter pauseInputAdapter;
	private InputAdapter gameOverInputAdapter;

	private final BitmapFont font = new BitmapFont();
	private final SpriteBatch batch = new SpriteBatch();
	private final GlyphLayout layout = new GlyphLayout();

	private final int KEY_PAUSE = Keys.ESCAPE;
	private final int KEY_RESUME = Keys.SPACE;
	private final int KEY_EXIT = Keys.ESCAPE;

	private final static String RESUME_GAME = "Press <SPACE> to resume";
	private final static String EXIT_GAME = "Press <ESC> to exit";
	private final static String GAME_OVER = "Game over";

	private boolean gameOver = false;

	public GameLoop(Game game) {
		super(game);
		
		PhysicsParams params = new PhysicsParams();
		params.worldWidth = Gdx.graphics.getWidth();
		params.worldHeight = Gdx.graphics.getHeight();
		
		simulation = new Simulation(params);
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

		gameOverInputAdapter = new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				switch (keycode) {
				case KEY_EXIT:
					nextState = State.MAIN_MENU;
					break;
				}
				return false;
			}
		};

		batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		Gdx.input.setInputProcessor(playInputAdapter);

		simulation.addPlayer(
			simulation.bounds.x + simulation.bounds.width / 2,
			simulation.bounds.y + simulation.bounds.height / 2
		);
	}

	/**
	 * Update the simulation
	 * 
	 * @param delta
	 *            The time in ms since the last update
	 */
	@Override
	public void update(float delta) {
		if (gameOver) {
			simulation.update(delta);
			return;
		}

		if (Gdx.input.isKeyPressed(Keys.UP))
			simulation.playerFwd(0);
		if (Gdx.input.isKeyPressed(Keys.LEFT))
			simulation.playerRotLeft(0);
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			simulation.playerRotRight(0);
		if (Gdx.input.isKeyJustPressed(Keys.SPACE))
			simulation.playerShoot(0);

		simulation.update(delta);

		if (simulation.players.isEmpty())
			gameOver();
	}

	/**
	 * Draw the simulations state
	 * 
	 * @param delta
	 *            The time in ms since the last update
	 */
	@Override
	public void draw(float delta) {
		renderer.clear();
		renderer.renderEntities(simulation, delta);
		renderer.renderDebug(simulation, delta);
		
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

		} else if (gameOver) {
			float width;
			float height;

			batch.begin();

			layout.setText(font, GAME_OVER);
			width = layout.width;
			height = layout.height;
			font.draw(batch, GAME_OVER, (Gdx.graphics.getWidth() - width) / 2, Gdx.graphics.getHeight() / 2 + 1.5f * height);

			layout.setText(font, EXIT_GAME);
			width = layout.width;
			height = layout.height;
			font.draw(batch, EXIT_GAME, (Gdx.graphics.getWidth() - width) / 2, Gdx.graphics.getHeight() / 2 - 1.5f * height);

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

	public void gameOver() {
		gameOver = true;
		Gdx.input.setInputProcessor(gameOverInputAdapter);
	}

	@Override
	public void resume() {
		super.resume();
		simulation.resume();
		Gdx.input.setInputProcessor(playInputAdapter);
	}

}