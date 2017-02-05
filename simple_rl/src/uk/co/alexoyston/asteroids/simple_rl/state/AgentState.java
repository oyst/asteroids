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

	public int activeShots = 0;

	protected final String name = CLASS_AGENT;

	public short radius;
	public float rotation;

	private static final List<Object> keys = Arrays.<Object>asList(
		VAR_ACTIVE_SHOTS
	);

	public AgentState() {
	}

	public AgentState(int radius, float rotation, int activeShots) {
		this.activeShots = activeShots;

		this.radius = (short)radius;
		this.rotation = rotation;
	}

	@Override
	public List<Object> variableKeys() {
		return keys;
	}

	@Override
	public Object get(Object variableKey) {
		if (variableKey.equals(VAR_ACTIVE_SHOTS)) {
			return activeShots;
		}
		throw new UnknownKeyException(variableKey);
	}

	@Override
	public AgentState copy() {
		return new AgentState(radius, rotation, activeShots);
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
