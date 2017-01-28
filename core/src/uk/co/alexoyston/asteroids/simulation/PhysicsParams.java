package uk.co.alexoyston.asteroids.simulation;

public class PhysicsParams {

	public float updateDelta = 0.01f;
	public int worldWidth = 1000;
	public int worldHeight = 1000;
	
	public PhysicsParams copy(){
		PhysicsParams params = new PhysicsParams();
		params.updateDelta = updateDelta;
		return params;
	}
}
