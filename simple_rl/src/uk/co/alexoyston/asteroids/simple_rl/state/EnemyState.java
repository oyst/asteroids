package uk.co.alexoyston.asteroids.simple_rl.state;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.DeepCopyState;
import burlap.mdp.core.oo.state.OOStateUtilities;

@DeepCopyState
public abstract class EnemyState implements ObjectInstance {

	public float width = 0f;
	public float height = 0f;
	public float rot = 0f;
	public float x = 0f;
	public float y = 0f;
	public float vx = 0f;
	public float vy = 0f;

	protected String name;

	protected static final List<Object> keys = Arrays.<Object>asList(
			VAR_X,
			VAR_Y,
			VAR_VELOCITY_X,
			VAR_VELOCITY_Y,
			VAR_WIDTH,
			VAR_HEIGHT,
			VAR_ROTATION
	);

	public EnemyState() {
	}

	public EnemyState(String name, float x, float y, float width, float height, float rot) {
		this(name, x, y, 0f, 0f, width, height, rot);
	}

	public EnemyState(String name, float x, float y, float vx, float vy, float width, float height, float rot) {
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.width = width;
		this.height = height;
		this.rot = rot;
		this.name = name;
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
		else if(variableKey.equals(VAR_WIDTH)){
			return width;
		}
		else if(variableKey.equals(VAR_HEIGHT)){
			return height;
		}
		else if(variableKey.equals(VAR_ROTATION)){
			return rot;
		}

		throw new UnknownKeyException(variableKey);
	}

	@Override
	public abstract EnemyState copy();

	@Override
	public ObjectInstance copyWithName(String objectName) {
		EnemyState enemy = this.copy();
		enemy.name = objectName;
		return enemy;
	}

	@Override
	public List<Object> variableKeys() {
		return keys;
	}

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
	public String toString() {
		return OOStateUtilities.objectInstanceToString(this);
	}

	@DeepCopyState
	public static class Asteroid extends EnemyState{
		public Asteroid(String name, float x, float y, float vx, float vy, float width, float height, float rot) {
			super(name, x, y, vx, vy, width, height, rot);
		}

		@Override
		public String className() {
			return CLASS_ASTEROID;
		}

		@Override
		public EnemyState.Asteroid copy() {
			return new Asteroid(name, x, y, vx, vy, width, height, rot);
		}
	}

	@DeepCopyState
	public static class Saucer extends EnemyState{
		public Saucer(String name, float x, float y, float vx, float vy, float width, float height, float rot) {
			super(name, x, y, vx, vy, width, height, rot);
		}

		@Override
		public String className() {
			return CLASS_SAUCER;
		}

		@Override
		public EnemyState.Saucer copy() {
			return new Saucer(name, x, y, vx, vy, width, height, rot);
		}
	}

	@DeepCopyState
	public static class Bullet extends EnemyState{
		public Bullet(String name, float x, float y, float vx, float vy, float width, float height, float rot) {
			super(name, x, y, vx, vy, width, height, rot);
		}

		@Override
		public String className() {
			return CLASS_BULLET;
		}

		@Override
		public EnemyState.Bullet copy() {
			return new Bullet(name, x, y, vx, vy, width, height, rot);
		}
	}
}
