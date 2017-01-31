package uk.co.alexoyston.asteroids.simple_rl.state;

import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.DeepCopyState;

import uk.co.alexoyston.asteroids.simulation.PhysicsParams;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

@DeepCopyState
public class AgentState implements ObjectInstance {

	public float x = 0f;
	public float y = 0f;
	public float rot = 0f;
	public float vx = 0f;
	public float vy = 0f;
	public float width = 0f;
	public float height = 0f;
	public int activeShots = 0;

	private static final List<Object> keys = Arrays.<Object>asList(
		VAR_X,
		VAR_Y,
		VAR_WIDTH,
		VAR_HEIGHT,
		VAR_VELOCITY_X,
		VAR_VELOCITY_Y,
		VAR_ROTATION,
		VAR_ACTIVE_SHOTS
	);

	public AgentState() {
	}

	public AgentState(float x, float y, float width, float height, float rot) {
		this(x, y, width, height, rot, 0f, 0f, 0);
	}

	public AgentState(float x, float y, float width, float height, float rot, float vx, float vy) {
		this(x, y, width, height, rot, vx, vy, 0);
	}

	public AgentState(float x, float y, float width, float height, float rot, float vx, float vy, int activeShots) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.rot = rot;
		this.vx = vx;
		this.vy = vy;
		this.activeShots = activeShots;
	}

	@Override
	public List<Object> variableKeys() {
		return keys;
	}

	@Override
	public Object get(Object variableKey) {
		if (variableKey.equals(VAR_X)) {
			return x;
		} else if (variableKey.equals(VAR_Y)) {
			return y;
		} else if (variableKey.equals(VAR_WIDTH)) {
			return width;
		} else if (variableKey.equals(VAR_HEIGHT)) {
			return height;
		} else if (variableKey.equals(VAR_VELOCITY_X)) {
			return vx;
		} else if (variableKey.equals(VAR_VELOCITY_Y)) {
			return vy;
		} else if (variableKey.equals(VAR_ROTATION)) {
			return rot;
		} else if (variableKey.equals(VAR_ACTIVE_SHOTS)) {
			return activeShots;
		}
		throw new UnknownKeyException(variableKey);
	}

	@Override
	public AgentState copy() {
		return new AgentState(x, y, width, height, rot, vx, vy, activeShots);
	}

	@Override
	public String className() {
		return CLASS_AGENT;
	}

	@Override
	public String name() {
		return CLASS_AGENT;
	}

	@Override
	public ObjectInstance copyWithName(String objectName) {
		if (!objectName.equals(CLASS_AGENT))
			throw new RuntimeException("Agent number must be " + CLASS_AGENT);

		return copy();
	}

	@Override
	public String toString() {
		return OOStateUtilities.objectInstanceToString(this);
	}
}
