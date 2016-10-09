package uk.co.alexoyston.asteroids;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;

import uk.co.alexoyston.asteroids.screens.BaseScreen;
import uk.co.alexoyston.asteroids.screens.GameLoop;
import uk.co.alexoyston.asteroids.screens.MainMenu;

public class Asteroids extends Game {
	public static final int WORLD_WIDTH = 800;
	public static final int WORLD_HEIGHT = 480;
	private static final FPSLogger fps = new FPSLogger();

	@Override
	public void create() {
		setScreen(new MainMenu(this));
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		BaseScreen currentScreen = getScreen();

		// update the screen
		currentScreen.render(Gdx.graphics.getDeltaTime());

		if (currentScreen.finished()) {
			// dispose the resources of the current screen
			currentScreen.dispose();

			switch (currentScreen.nextScreen()) {
			case MAIN_MENU:
				setScreen(new MainMenu(this));
				break;

			case GAME_LOOP:
				setScreen(new GameLoop(this));
				break;

			default:
				throw new UnknownError("Unexpected state: " + currentScreen.nextScreen());
			}
		}

		fps.log();
	}

	@Override
	public BaseScreen getScreen() {
		return (BaseScreen) super.getScreen();
	}
}
