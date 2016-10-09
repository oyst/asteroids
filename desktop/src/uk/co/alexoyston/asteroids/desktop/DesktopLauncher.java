package uk.co.alexoyston.asteroids.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import uk.co.alexoyston.asteroids.Asteroids;

public class DesktopLauncher {
	private static final String TITLE = "Asteroids";

	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = TITLE;
		config.width = Asteroids.WORLD_WIDTH;
		config.height = Asteroids.WORLD_HEIGHT;
		new LwjglApplication(new Asteroids(), config);
	}
}
