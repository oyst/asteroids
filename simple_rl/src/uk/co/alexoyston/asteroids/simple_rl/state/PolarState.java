package uk.co.alexoyston.asteroids.simple_rl.state;

import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.DeepCopyState;

import uk.co.alexoyston.asteroids.simulation.Entity;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

@DeepCopyState
public abstract class PolarState extends EntityState implements Comparable<PolarState> {

	public float dist = 0f;
	public float angle = 0f;
	public float rel_vx = 0f;
	public float rel_vy = 0f;

	protected static final List<Object> keys = Arrays.<Object>asList(
			VAR_DIST,
			VAR_ANGLE,
			VAR_VELOCITY_X,
			VAR_VELOCITY_Y
	);

	public PolarState() {
	}

	public PolarState(String name, float dist, float angle, float vx, float vy) {
		this.name = name;
		this.dist = dist;
		this.angle = angle;
		this.vx = vx;
		this.vy = vy;
	}

	public PolarState(String name, float x, float y, float width, float height, float vx, float vy, float rotation, float dist, float angle, float rel_vx, float rel_vy) {
		super(name, x, y, width, height, vx, vy, rotation);
		this.dist = dist;
		this.angle = angle;
		this.rel_vx = rel_vx;
		this.rel_vy = rel_vy;
	}

	public PolarState(String name, Entity entity, float dist, float angle, float rel_vx, float rel_vy) {
		super(name, entity);
		this.dist = dist;
		this.angle = angle;
		this.rel_vx = rel_vx;
		this.rel_vy = rel_vy;
	}

	@Override
	public Object get(Object variableKey) {
		if(variableKey.equals(VAR_DIST))
			return dist;

		else if(variableKey.equals(VAR_ANGLE))
			return angle;

		else if(variableKey.equals(VAR_VELOCITY_X))
			return rel_vx;

		else if(variableKey.equals(VAR_VELOCITY_Y))
			return rel_vy;

		throw new UnknownKeyException(variableKey);
	}

	@Override
	public List<Object> variableKeys() {
		return keys;
	}

	@Override
	public int compareTo(PolarState other) {
		if (this.dist > other.dist) return 1;
		if (this.dist < other.dist) return -1;
		return 0;
	}

	@Override
	public abstract PolarState copy();

	@DeepCopyState
	public static class Asteroid extends PolarState{
		public Asteroid(String name, float dist, float angle, float vx, float vy) {
			super(name, dist, angle, vx, vy);
		}

		public Asteroid(String name, float x, float y, float width, float height, float vx, float vy, float rotation, float dist, float angle, float rel_vx, float rel_vy) {
			super(name, x, y, width, height, vx, vy, rotation, dist, angle, rel_vx, rel_vy);
		}

		public Asteroid(String name, Entity entity, float dist, float angle, float rel_vx, float rel_vy) {
			super(name, entity, dist, angle, rel_vx, rel_vy);
		}

		@Override
		public String className() {
			return CLASS_ASTEROID;
		}

		@Override
		public PolarState.Asteroid copy() {
			return new Asteroid(name, x, y, width, height, vx, vy, rotation, dist, angle, rel_vx, rel_vy);
		}
	}

	@DeepCopyState
	public static class Saucer extends PolarState{
		public Saucer(String name, float dist, float angle, float vx, float vy) {
			super(name, dist, angle, vx, vy);
		}

		public Saucer(String name, float x, float y, float width, float height, float vx, float vy, float rotation, float dist, float angle, float rel_vx, float rel_vy) {
			super(name, x, y, width, height, vx, vy, rotation, dist, angle, rel_vx, rel_vy);
		}

		public Saucer(String name, Entity entity, float dist, float angle, float rel_vx, float rel_vy) {
			super(name, entity, dist, angle, rel_vx, rel_vy);
		}

		@Override
		public String className() {
			return CLASS_SAUCER;
		}

		@Override
		public PolarState.Saucer copy() {
			return new Saucer(name, x, y, width, height, vx, vy, rotation, dist, angle, rel_vx, rel_vy);
		}
	}

	@DeepCopyState
	public static class Bullet extends PolarState{
		public Bullet(String name, float dist, float angle, float vx, float vy) {
			super(name, dist, angle, vx, vy);
		}

		public Bullet(String name, float x, float y, float width, float height, float vx, float vy, float rotation, float dist, float angle, float rel_vx, float rel_vy) {
			super(name, x, y, width, height, vx, vy, rotation, dist, angle, rel_vx, rel_vy);
		}

		public Bullet(String name, Entity entity, float dist, float angle, float rel_vx, float rel_vy) {
			super(name, entity, dist, angle, rel_vx, rel_vy);
		}

		@Override
		public String className() {
			return CLASS_BULLET;
		}

		@Override
		public PolarState.Bullet copy() {
			return new Bullet(name, x, y, width, height, vx, vy, rotation, dist, angle, rel_vx, rel_vy);
		}
	}
}
