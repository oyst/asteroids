package uk.co.alexoyston.asteroids.simulation;

public class Player extends Entity {

	public Player() {
		float width = 20;
		float height = 20;

		float[] vertices = new float[] { 
			width / 2, height / 2,
			0, 0,
			width / 2, height,
			width, 0
		};

		setVertices(vertices);

		drag = 0.3f;
	}
}
