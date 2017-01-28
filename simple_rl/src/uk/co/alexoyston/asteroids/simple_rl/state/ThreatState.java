package uk.co.alexoyston.asteroids.simple_rl.state;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.CLASS_BULLET;
import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.VAR_VELOCITY_X;
import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.VAR_VELOCITY_Y;
import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.VAR_X;
import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.VAR_Y;

import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.DeepCopyState;

@DeepCopyState
public abstract class ThreatState implements ObjectInstance {

	public float x = 0f;
	public float y = 0f;
	public float vx = 0f;
	public float vy = 0f;
	
	protected String name;
	
	protected static final List<Object> keys = Arrays.<Object>asList(
			VAR_X,
			VAR_Y,
			VAR_VELOCITY_X,
			VAR_VELOCITY_Y
	);
	
	public ThreatState() {
	}

	public ThreatState(String name, float x, float y) {
		this(name, x, y, 0f, 0f);
	}
	
	public ThreatState(String name, float x, float y, float vx, float vy) {
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.name = name;
	}

	@Override
	public List<Object> variableKeys() {
		return keys;
	}

	@Override
	public Object get(Object variableKey) {
		if(variableKey.equals(VAR_X)){
			return x;
		}
		else if(variableKey.equals(VAR_Y)){
			return y;
		}
		else if(variableKey.equals(VAR_VELOCITY_X)){
			return vx;
		}
		else if(variableKey.equals(VAR_VELOCITY_Y)){
			return vy;
		}
		throw new UnknownKeyException(variableKey);
	}

	@Override
	public abstract ThreatState copy();

	@Override
	public abstract String className();
	
	@Override
	public String name() {
		return name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public ObjectInstance copyWithName(String objectName) {
		ThreatState threat = this.copy();
		threat.name = objectName;
		return threat;
	}
	
	@Override
	public String toString() {
		return OOStateUtilities.objectInstanceToString(this);
	}
	
	@DeepCopyState
	public static class Bullet extends ThreatState{
		public Bullet(String name, float x, float y, float vx, float vy) {
			super(name, x, y, vx, vy);
		}

		@Override
		public String className() {
			return CLASS_BULLET;
		}
		
		@Override
		public Bullet copy() {
			return new Bullet(name, x, y, vx, vy);
		}
	}
}
