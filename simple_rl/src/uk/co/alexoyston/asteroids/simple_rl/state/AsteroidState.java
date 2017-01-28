package uk.co.alexoyston.asteroids.simple_rl.state;

import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.DeepCopyState;
import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

@DeepCopyState
public class AsteroidState implements ObjectInstance {

	public float x = 0f;
	public float y = 0f;
	public float width = 0f;
	public float height = 0f;
	public float rot = 0f;
	public float vx = 0f;
	public float vy = 0f;
	
	private String name;
	
	private static final List<Object> keys = Arrays.<Object>asList(VAR_X, VAR_Y, VAR_WIDTH, VAR_HEIGHT, VAR_VELOCITY_X, VAR_VELOCITY_Y);
	
	public AsteroidState() {
	}

	public AsteroidState(String name, float x, float y, float width, float height, float rot) {
		this(name, x, y, width, height, rot, 0f, 0f);
	}
	
	public AsteroidState(String name, float x, float y, float width, float height, float rot, float vx, float vy) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.rot = rot;
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
		else if(variableKey.equals(VAR_WIDTH)){
			return width;
		}
		else if(variableKey.equals(VAR_HEIGHT)){
			return height;
		}
		else if(variableKey.equals(VAR_VELOCITY_X)){
			return vx;
		}
		else if(variableKey.equals(VAR_VELOCITY_Y)){
			return vy;
		}
		else if(variableKey.equals(VAR_ROTATION)){
			return rot;
		}
		throw new UnknownKeyException(variableKey);
	}


	@Override
	public AsteroidState copy() {
		return new AsteroidState(name, x, y, width, height, rot, vx, vy);
	}


	@Override
	public String className() {
		return CLASS_ASTEROID;
	}

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
		AsteroidState asteroid = this.copy();
		asteroid.name = objectName;
		return asteroid;
	}
	
	@Override
	public String toString() {
		return OOStateUtilities.objectInstanceToString(this);
	}
}
