package uk.co.alexoyston.asteroids.simple_rl.state;

import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.DeepCopyState;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

@DeepCopyState
public abstract class PolarState implements ObjectInstance, Comparable<PolarState> {

	public float dist = 0f;
	public float angle = 0f;
	public float vx = 0f;
	public float vy = 0f;

	public int diameter;

	protected String name;

	protected static final List<Object> keys = Arrays.<Object>asList(
			VAR_DIST,
			VAR_ANGLE,
			VAR_VELOCITY_X,
			VAR_VELOCITY_Y
	);

	public PolarState() {
	}

	public PolarState(String name, int diameter, float dist, float angle, float vx, float vy) {
		this.name = name;
		this.dist = dist;
		this.angle = angle;
		this.vx = vx;
		this.vy = vy;

		this.diameter = (short)diameter;
	}

	@Override
	public Object get(Object variableKey) {
		if(variableKey.equals(VAR_DIST))
			return dist;

		else if(variableKey.equals(VAR_ANGLE))
			return angle;

		else if(variableKey.equals(VAR_VELOCITY_X))
			return vx;

		else if(variableKey.equals(VAR_VELOCITY_Y))
			return vy;

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

	@Override
	public PolarState copyWithName(String objectName) {
		PolarState obj = this.copy();
		obj.name = objectName;
		return obj;
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public String toString() {
		return OOStateUtilities.objectInstanceToString(this);
	}

	@DeepCopyState
	public static class Asteroid extends PolarState {
		public Asteroid(String name, int diameter, float dist, float angle, float vx, float vy) {
			super(name, diameter, dist, angle, vx, vy);
		}

		@Override
		public String className() {
			return CLASS_ASTEROID;
		}

		@Override
		public PolarState.Asteroid copy() {
			return new Asteroid(name, diameter, dist, angle, vx, vy);
		}
	}

	@DeepCopyState
	public static class Saucer extends PolarState{
		public Saucer(String name, int diameter, float dist, float angle, float vx, float vy) {
			super(name, diameter, dist, angle, vx, vy);
		}

		@Override
		public String className() {
			return CLASS_SAUCER;
		}

		@Override
		public PolarState.Saucer copy() {
			return new Saucer(name, diameter, dist, angle, vx, vy);
		}
	}

	@DeepCopyState
	public static class Bullet extends PolarState{
		public Bullet(String name, int diameter, float dist, float angle, float vx, float vy) {
			super(name, diameter, dist, angle, vx, vy);
		}

		@Override
		public String className() {
			return CLASS_BULLET;
		}

		@Override
		public PolarState.Bullet copy() {
			return new Bullet(name, diameter, dist, angle, vx, vy);
		}
	}
}
