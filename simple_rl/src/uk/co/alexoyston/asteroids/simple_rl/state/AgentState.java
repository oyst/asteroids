package uk.co.alexoyston.asteroids.simple_rl.state;

import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.DeepCopyState;

import uk.co.alexoyston.asteroids.simulation.Entity;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

@DeepCopyState
public class AgentState implements ObjectInstance {

	public int remainingShots = 0;
	public int remainingWarps = 0;

	protected final String name = CLASS_AGENT;

	public short diameter = 0;
	public float rotation = 0f;

	public static final List<Object> keys = Arrays.<Object>asList(
		VAR_CAN_SHOOT
		// VAR_CAN_WARP
	);

	public AgentState() {
	}

	public AgentState(int diameter, int remainingShots, int remainingWarps, float rotation) {
		this.remainingShots = remainingShots;
		this.remainingWarps = remainingWarps;
		this.rotation = rotation;
		this.diameter = (short)diameter;
	}

	@Override
	public List<Object> variableKeys() {
		return keys;
	}

	@Override
	public Object get(Object variableKey) {
		if (variableKey.equals(VAR_CAN_SHOOT)) {
			return (remainingShots == 0) ? 0 : 1;
		}
		else if (variableKey.equals(VAR_CAN_WARP)) {
			return (remainingWarps == 0) ? 0 : 1;
		}
		throw new UnknownKeyException(variableKey);
	}

	@Override
	public AgentState copy() {
		return new AgentState(diameter, remainingShots, remainingWarps, rotation);
	}

	@Override
	public String className() {
		return name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public AgentState copyWithName(String objectName) {
		if (!objectName.equals(name))
			throw new RuntimeException("Agent number must be " + name);

		return copy();
	}

	@Override
	public String toString() {
		return OOStateUtilities.objectInstanceToString(this);
	}
}
