package uk.co.alexoyston.asteroids.simulation;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Polygon;

public class Player extends Entity {

	public Player() {
		float width = 20;
		float height = 20;
		
		float[] vertices = new float[] {
		    width/2, height/2,
		    0, height,
		    width/2, 0,
		    width, height
		};
		polygon = new Polygon();
		polygon.setVertices(vertices);
	}

	@Override
	public void collide(Entity other) {
	}

	@Override
	public void input(Input input, int delta) {
	}

}
