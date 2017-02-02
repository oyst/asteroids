package uk.co.alexoyston.asteroids.simple_rl.state;

import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.oo.state.OOStateUtilities;

import uk.co.alexoyston.asteroids.simulation.Entity;

public abstract class EntityState implements ObjectInstance {

	public float x = 0f;
	public float y = 0f;
	public float width = 0f;
	public float height = 0f;
	public float vx = 0f;
	public float vy = 0f;
	public float rotation = 0f;

	protected String name;

	public EntityState() {
	}

	public EntityState(String name) {
		this(name, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
	}

	public EntityState(String name, Entity entity) {
		this(
			name,
			entity.location.x,
			entity.location.y,
			entity.bounds.width,
			entity.bounds.height,
			entity.velocity.x,
			entity.velocity.y,
			entity.rotation
		);
	}

	public EntityState(String name, float x, float y, float width, float height, float vx, float vy, float rotation) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.vx = vx;
		this.vy = vy;
		this.rotation = rotation;
	}

	@Override
	public EntityState copyWithName(String objectName) {
		EntityState obj = this.copy();
		obj.name = objectName;
		return obj;
	}

	@Override
	public abstract EntityState copy();

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public String toString() {
		return OOStateUtilities.objectInstanceToString(this);
	}
}
