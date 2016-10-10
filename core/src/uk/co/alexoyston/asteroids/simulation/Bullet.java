package uk.co.alexoyston.asteroids.simulation;

import com.badlogic.gdx.math.Polygon;

public class Bullet extends Entity {

	public Bullet() {
		int width = 2;
		int height = 2;

		float[] vertices = new float[] { 
			0, 0,
			width, 0,
			width, height,
			height, 0
		};
		polygon = new Polygon();
		polygon.setVertices(vertices);
		
		maxAge = 1;
	}
}
