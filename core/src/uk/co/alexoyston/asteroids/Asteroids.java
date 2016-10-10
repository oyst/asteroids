package uk.co.alexoyston.asteroids;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;

import uk.co.alexoyston.asteroids.screens.BaseScreen;
import uk.co.alexoyston.asteroids.screens.GameLoop;
import uk.co.alexoyston.asteroids.screens.MainMenu;
import uk.co.alexoyston.asteroids.screens.State;

public class Asteroids extends Game {
	public static final int WORLD_WIDTH = 800;
	public static final int WORLD_HEIGHT = 480;
	private static final FPSLogger fps = new FPSLogger();
	private static final String TAG = Asteroids.class.getName();
	
	@Override
	public void create() {
		setScreen(new MainMenu(this));
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		BaseScreen currentScreen = getScreen();

		currentScreen.render(Gdx.graphics.getDeltaTime());

		if (currentScreen.nextScreen() != State.UNSET) {
			// dispose the resources of the old screen
			currentScreen.dispose();

			switch (currentScreen.nextScreen()) {
			case MAIN_MENU:
				setScreen(new MainMenu(this));
				break;

			case GAME_LOOP:
				setScreen(new GameLoop(this));
				break;

			case EXIT:
				Gdx.app.log(TAG, "Closing");
				Gdx.app.exit();
				break;

			default:
				Gdx.app.error(TAG, "Request to change to an unknown state: " + currentScreen.nextScreen());
				Gdx.app.exit();
				break;
			}
		}

		fps.log();
	}

	@Override
	public BaseScreen getScreen() {
		return (BaseScreen) super.getScreen();
	}
}
