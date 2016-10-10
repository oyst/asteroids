package uk.co.alexoyston.asteroids.simulation;

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
		setVertices(vertices);
		
		maxAge = 1;
	}
}
