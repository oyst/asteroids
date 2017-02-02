package uk.co.alexoyston.asteroids.simple_rl.state;

import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.DeepCopyState;

import uk.co.alexoyston.asteroids.simulation.Entity;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

@DeepCopyState
public class AgentState extends EntityState {

	public int activeShots = 0;

	private static final List<Object> keys = Arrays.<Object>asList(
		VAR_ACTIVE_SHOTS
	);

	public AgentState() {
		this.name = CLASS_AGENT;
	}

	public AgentState(int activeShots) {
		this.activeShots = activeShots;
		this.name = CLASS_AGENT;
	}

	public AgentState(float x, float y, float width, float height, float vx, float vy, float rotation, int activeShots) {
		super(CLASS_AGENT, x, y, width, height, vx, vy, rotation);
		this.activeShots = activeShots;
	}

	public AgentState(Entity entity, int activeShots) {
		super(CLASS_AGENT, entity);
		this.activeShots = activeShots;
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
		return new AgentState(x, y, width, height, vx, vy, rotation, activeShots);
	}

	@Override
	public String className() {
		return name;
	}

	@Override
	public AgentState copyWithName(String objectName) {
		if (!objectName.equals(CLASS_AGENT))
			throw new RuntimeException("Agent number must be " + CLASS_AGENT);

		return copy();
	}
}
