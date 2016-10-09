package uk.co.alexoyston.asteroids.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * The main menu screen. Requests closure on setting nextScreen.
 */
public class MainMenu extends BaseScreen {
	private final BitmapFont font = new BitmapFont();
	private final SpriteBatch batch = new SpriteBatch();
	private final GlyphLayout layout = new GlyphLayout();
	
	private final static String START_GAME = "Press <SPACE> to start";

	public MainMenu(Game game) {
		super(game);
		batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void update(float delta) {
		if (Gdx.input.isKeyPressed(Keys.SPACE)) {
			nextScreen = State.GAME_LOOP;
		}
	}

	@Override
	public void draw(float delta) {
		float width;
		float height;

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

		layout.setText(font, START_GAME);
		width = layout.width;
		height = layout.height;
		font.draw(batch, START_GAME, (Gdx.graphics.getWidth() - width) / 2, (Gdx.graphics.getHeight() + height) / 2);

		batch.end();
	}

	@Override
	public void dispose() {
		font.dispose();
		batch.dispose();
	}

}