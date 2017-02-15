package uk.co.alexoyston.asteroids.simulation;

public class SmallSaucer extends Saucer {

	public SmallSaucer(PhysicsParams params) {
		speed = params.smallSaucerSpeed;
		turnFreq = params.smallSaucerTurnFreq;
		reloadTime = params.smallSaucerReloadTime;
		shotSpeed = params.smallSaucerShotSpeed;
		shotAccuracy = params.smallSaucerShotAccuracy;

		int width = 25;
		int height = 15;

		float a = width/5;
		float b = width/3;
		float c = height/3;

		float[] vertices = new float[] {
			width, c,
			0, c,
			a, 0,
			width - a, 0,
			width, c,
			width - a, height - c,
			a, height - c,
			0, c,
			a, height - c,
			b, height,
			width - b, height,
			width - a, height - c
		};

		setVertices(vertices);

		velocity.x = speed;

		center.set(width/2, height/2);
	}
}
