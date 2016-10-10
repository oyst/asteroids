package uk.co.alexoyston.asteroids.simulation;

import com.badlogic.gdx.math.Polygon;

public class Player extends Entity {

	public Player() {
		float width = 20;
		float height = 20;
		
		float[] vertices = new float[] {
		    width/2, height/2,
		    0, 0,
		    width/2, height,
		    width, 0
		};
		polygon = new Polygon();
		polygon.setVertices(vertices);
		
		drag = 0.3f;
	}
}
