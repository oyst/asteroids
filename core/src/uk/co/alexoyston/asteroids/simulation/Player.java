package uk.co.alexoyston.asteroids.simulation;

public class Player extends Entity {

	private long prevShot = 0;
	protected float weaponTemp = 0;
	protected final float shotSpeed = 600f;
	protected final float shotHeat = 30;
	protected final float reloadTime = 0.03f;

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

	public Entity shoot() {
		if (weaponTemp > 100 - shotHeat)
			return null;

		long now = System.currentTimeMillis();
		if (now - prevShot < reloadTime)
			return null;
		prevShot = now;

		weaponTemp += shotHeat;

		Bullet bullet = new Bullet();
		bullet.location.x = center.x - bullet.bounds.width / 2;
		bullet.location.y = center.y - bullet.bounds.height / 2;
		bullet.center.x = center.x;
		bullet.center.y = center.y;

		float sinRotation = (float) Math.sin(rotation);
		float cosRotation = (float) Math.cos(rotation);
		bullet.velocity.set(sinRotation * shotSpeed, cosRotation * shotSpeed);

		return bullet;
	}
}
