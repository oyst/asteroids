package uk.co.alexoyston.asteroids.simple_rl.state;

import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.DeepCopyState;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

@DeepCopyState
public class PolarState implements ObjectInstance, Comparable<PolarState> {

	public float dist = 0f;
	public float angle = 0f;
	public float vDist = 0f;
	public float vAngle = 0f;
	public byte present = 0;

	public int diameter;

	protected String name;

	public static final List<Object> keys = Arrays.<Object>asList(
			VAR_DIST,
			VAR_ANGLE,
			VAR_VELOCITY_DIST,
			VAR_VELOCITY_ANGLE
	);

	public PolarState() {
	}

	public PolarState(String name) {
		this.name = name;
		this.present = 0;
	}

	public PolarState(String name, int diameter, float dist, float angle, float vDist, float vAngle) {
		this(name, diameter, dist, angle, vDist, vAngle, 1);
	}

	public PolarState(String name, int diameter, float dist, float angle, float vDist, float vAngle, int present) {
		this.name = name;
		this.dist = dist;
		this.angle = angle;
		this.vDist = vDist;
		this.vAngle = vAngle;
		this.present = (byte)present;

		this.diameter = (short)diameter;
	}

	@Override
	public Object get(Object variableKey) {
		if(variableKey.equals(VAR_DIST))
			return dist;

		else if(variableKey.equals(VAR_ANGLE))
			return angle;

		else if(variableKey.equals(VAR_VELOCITY_DIST))
			return vDist;

		else if(variableKey.equals(VAR_VELOCITY_ANGLE))
			return vAngle;

		else if(variableKey.equals(VAR_PRESENT))
			return present;

		throw new UnknownKeyException(variableKey);
	}

	@Override
	public List<Object> variableKeys() {
		return keys;
	}

	@Override
	public int compareTo(PolarState other) {
		if (this.present > other.present) return -1;
		if (this.present < other.present) return 1;

		if (this.dist > other.dist) return 1;
		if (this.dist < other.dist) return -1;
		return 0;
	}

	@Override
	public PolarState copy() {
		return new PolarState(name, diameter, dist, angle, vDist, vAngle, present);
	}

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
	public String className() {
		return CLASS_OBJECT;
	}

	@Override
	public String toString() {
		return OOStateUtilities.objectInstanceToString(this);
	}
}
