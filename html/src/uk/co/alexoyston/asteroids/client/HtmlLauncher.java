package uk.co.alexoyston.asteroids.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import uk.co.alexoyston.asteroids.Asteroids;

public class HtmlLauncher extends GwtApplication {
	@Override
	public GwtApplicationConfiguration getConfig() {
		return new GwtApplicationConfiguration(Asteroids.WORLD_WIDTH, Asteroids.WORLD_HEIGHT);
	}

	@Override
	public ApplicationListener createApplicationListener() {
		return new Asteroids();
	}
}