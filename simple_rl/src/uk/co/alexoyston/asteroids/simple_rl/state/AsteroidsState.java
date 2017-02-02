package uk.co.alexoyston.asteroids.simple_rl.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;

import burlap.mdp.core.oo.state.MutableOOState;
import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.OOVariableKey;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.oo.state.exceptions.UnknownClassException;
import burlap.mdp.core.oo.state.exceptions.UnknownObjectException;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.StateUtilities;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.ShallowCopyState;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

// Copy-on-write
@ShallowCopyState
public class AsteroidsState implements MutableOOState {

	public AgentState agent;
	public List<EnemyState.Asteroid> asteroids;
	public List<EnemyState.Bullet> bullets;
	public List<EnemyState.Saucer> saucers;

	private HashSet<Object> touchSet;

	public AsteroidsState() {
	}

	public AsteroidsState(AgentState agent) {
		this.agent = agent;
		this.asteroids = new ArrayList<EnemyState.Asteroid>();
		this.bullets = new ArrayList<EnemyState.Bullet>();
		this.saucers = new ArrayList<EnemyState.Saucer>();
	}

	public AsteroidsState(AgentState agent, List<EnemyState.Asteroid> asteroids, List<EnemyState.Bullet> bullets, List<EnemyState.Saucer> saucers) {
		this.agent = agent;
		this.asteroids = asteroids;
		this.bullets = bullets;
		this.saucers = saucers;
	}

	@Override
	public MutableOOState addObject(ObjectInstance o) {
		if (o instanceof AgentState) {
			agent = (AgentState)o;
		}
		else if (o instanceof EnemyState.Asteroid) {
			touchAsteroids().add((EnemyState.Asteroid)o);
		}
		else if (o instanceof EnemyState.Bullet) {
			touchBullets().add((EnemyState.Bullet)o);
		}
		else if (o instanceof EnemyState.Saucer) {
			touchSaucers().add((EnemyState.Saucer)o);
		}
		else {
			throw new UnknownClassException(o.className());
		}
		return this;
	}

	@Override
	public MutableOOState removeObject(String oname) {
		int index = -1;

		if (agent.name().equals(oname)) {
			// Cannot remove agent
			agent = new AgentState();
			return this;
		}

		index = OOStateUtilities.objectIndexWithName(asteroids, oname);
		if (index != -1) {
			touchAsteroids().remove(index);
			return this;
		}

		index = OOStateUtilities.objectIndexWithName(bullets, oname);
		if (index != -1) {
			touchBullets().remove(index);
			return this;
		}

		index = OOStateUtilities.objectIndexWithName(saucers, oname);
		if (index != -1) {
			touchSaucers().remove(index);
			return this;
		}

		throw new UnknownObjectException(oname);
	}

	@Override
	public MutableOOState renameObject(String objectName, String newName) {
		int index = -1;

		if (agent.name().equals(objectName)) {
			throw new RuntimeException("Agent name must be " + objectName);
		}

		index = OOStateUtilities.objectIndexWithName(asteroids, objectName);
		if (index != -1) {
			EnemyState.Asteroid obj = asteroids.get(index);
			touchAsteroids().remove(index);
			asteroids.add(index, (EnemyState.Asteroid)obj.copyWithName(newName));
			return this;
		}

		index = OOStateUtilities.objectIndexWithName(saucers, objectName);
		if (index != -1) {
			EnemyState.Saucer obj = saucers.get(index);
			touchSaucers().remove(index);
			saucers.add(index, (EnemyState.Saucer)obj.copyWithName(newName));
			return this;
		}

		index = OOStateUtilities.objectIndexWithName(bullets, objectName);
		if (index != -1) {
			EnemyState.Bullet obj = bullets.get(index);
			touchBullets().remove(index);
			bullets.add(index, (EnemyState.Bullet)obj.copyWithName(newName));
			return this;
		}

		throw new UnknownObjectException(objectName);

	}

	@Override
	public int numObjects() {
		// One agent and some asteroids
		return 1 + asteroids.size() + bullets.size() + saucers.size();
	}

	@Override
	public ObjectInstance object(String oname) {
		int index = -1;

		if (agent.name().equals(oname)) {
			return agent;
		}

		index = OOStateUtilities.objectIndexWithName(asteroids, oname);
		if (index != -1) {
			return asteroids.get(index);
		}

		index = OOStateUtilities.objectIndexWithName(saucers, oname);
		if (index != -1) {
			return saucers.get(index);
		}

		index = OOStateUtilities.objectIndexWithName(bullets, oname);
		if (index != -1) {
			return bullets.get(index);
		}

		throw new UnknownObjectException(oname);
	}

	@Override
	public List<ObjectInstance> objects() {
		List<ObjectInstance> obs = new ArrayList<ObjectInstance>(numObjects());
		obs.add(agent);
		obs.addAll(asteroids);
		obs.addAll(saucers);
		obs.addAll(bullets);
		return obs;
	}

	@Override
	public List<ObjectInstance> objectsOfClass(String oclass) {
		if(oclass.equals(CLASS_AGENT)){
			return Arrays.<ObjectInstance>asList(agent);
		}
		else if(oclass.equals(CLASS_ASTEROID)){
			return new ArrayList<ObjectInstance>(asteroids);
		}
		else if(oclass.equals(CLASS_SAUCER)){
			return new ArrayList<ObjectInstance>(saucers);
		}
		else if(oclass.equals(CLASS_BULLET)){
			return new ArrayList<ObjectInstance>(bullets);
		}
		throw new UnknownClassException(oclass);
	}

	@Override
	public MutableState set(Object variableKey, Object value) {
		//TODO: Research
		OOVariableKey key = OOStateUtilities.generateKey(variableKey);
		Number num = StateUtilities.stringOrNumber(value);
		int index = -1;

		if (agent.name().equals(key.obName)) {
			if (key.obVarKey.equals(VAR_X)) {
				touchAgent().x = num.floatValue();
			}
			else if (key.obVarKey.equals(VAR_Y)) {
				touchAgent().y = num.floatValue();
			}
			else if (key.obVarKey.equals(VAR_WIDTH)) {
				touchAgent().width = num.floatValue();
			}
			else if (key.obVarKey.equals(VAR_HEIGHT)) {
				touchAgent().height = num.floatValue();
			}
			else if (key.obVarKey.equals(VAR_VELOCITY_X)) {
				touchAgent().vx = num.floatValue();
			}
			else if (key.obVarKey.equals(VAR_VELOCITY_Y)) {
				touchAgent().vy = num.floatValue();
			}
			else {
				throw new UnknownKeyException(key.obVarKey);
			}
			return this;
		}

		index = OOStateUtilities.objectIndexWithName(asteroids, key.obName);
		if (index != -1) {
			if (key.obVarKey.equals(VAR_X)) {
				touchAsteroid(index).x = num.floatValue();
			}
			else if (key.obVarKey.equals(VAR_Y)) {
				touchAsteroid(index).y = num.floatValue();
			}
			else if (key.obVarKey.equals(VAR_WIDTH)) {
				touchAsteroid(index).width = num.floatValue();
			}
			else if (key.obVarKey.equals(VAR_HEIGHT)) {
				touchAsteroid(index).height = num.floatValue();
			}
			else if (key.obVarKey.equals(VAR_VELOCITY_X)) {
				touchAsteroid(index).vx = num.floatValue();
			}
			else if (key.obVarKey.equals(VAR_VELOCITY_Y)) {
				touchAsteroid(index).vy = num.floatValue();
			}
			else {
				throw new UnknownKeyException(key.obVarKey);
			}
			return this;
		}

		index = OOStateUtilities.objectIndexWithName(saucers, key.obName);
		if (index != -1) {
			if (key.obVarKey.equals(VAR_X)) {
				touchSaucer(index).x = num.floatValue();
			}
			else if (key.obVarKey.equals(VAR_Y)) {
				touchSaucer(index).y = num.floatValue();
			}
			else if (key.obVarKey.equals(VAR_WIDTH)) {
				touchSaucer(index).width = num.floatValue();
			}
			else if (key.obVarKey.equals(VAR_HEIGHT)) {
				touchSaucer(index).height = num.floatValue();
			}
			else if (key.obVarKey.equals(VAR_VELOCITY_X)) {
				touchSaucer(index).vx = num.floatValue();
			}
			else if (key.obVarKey.equals(VAR_VELOCITY_Y)) {
				touchSaucer(index).vy = num.floatValue();
			}
			else {
				throw new UnknownKeyException(key.obVarKey);
			}
			return this;
		}

		index = OOStateUtilities.objectIndexWithName(bullets, key.obName);
		if (index != -1) {
			if (key.obVarKey.equals(VAR_X)) {
				touchBullet(index).x = num.floatValue();
			}
			else if (key.obVarKey.equals(VAR_Y)) {
				touchBullet(index).y = num.floatValue();
			}
			else if (key.obVarKey.equals(VAR_VELOCITY_X)) {
				touchBullet(index).vx = num.floatValue();
			}
			else if (key.obVarKey.equals(VAR_VELOCITY_Y)) {
				touchBullet(index).vy = num.floatValue();
			}
			else {
				throw new UnknownKeyException(key.obVarKey);
			}
			return this;
		}

		throw new UnknownKeyException(key.obName);
	}

	@Override
	public Object get(Object variableKey) {
		return OOStateUtilities.get(this,  variableKey);
	}

	@Override
	public List<Object> variableKeys() {
		return OOStateUtilities.flatStateKeys(this);
	}

	@Override
	public AsteroidsState copy() {
		AsteroidsState state = new AsteroidsState(agent, asteroids, bullets, saucers);
		state.resetTouchSet();
		return state;
	}

	public void resetTouchSet() {
		touchSet = new HashSet<Object>();
	}

	public AgentState touchAgent() {
		if (touchSet.contains(agent))
			return agent;
		agent = agent.copy();
		touchSet.add(agent);
		return agent;
	}

	public List<EnemyState.Asteroid> touchAsteroids() {
		if (touchSet.contains(asteroids))
			return asteroids;
		asteroids = new ArrayList<EnemyState.Asteroid>(asteroids);
		touchSet.add(asteroids);
		return asteroids;
	}

	public EnemyState.Asteroid touchAsteroid(int index) {
		EnemyState.Asteroid asteroid = asteroids.get(index);
		if (touchSet.contains(asteroid))
			return asteroid;
		touchAsteroids();
		asteroid = asteroid.copy();
		asteroids.set(index, asteroid);
		touchSet.add(asteroid);
		return asteroid;
	}

	public List<EnemyState.Saucer> touchSaucers() {
		if (touchSet.contains(saucers))
			return saucers;
		saucers = new ArrayList<EnemyState.Saucer>(saucers);
		touchSet.add(saucers);
		return saucers;
	}

	public EnemyState.Saucer touchSaucer(int index) {
		EnemyState.Saucer saucer = saucers.get(index);
		if (touchSet.contains(saucer))
			return saucer;
		touchSaucers();
		saucer = saucer.copy();
		saucers.set(index, saucer);
		touchSet.add(saucer);
		return saucer;
	}

	public List<EnemyState.Bullet> touchBullets() {
		if (touchSet.contains(bullets))
			return bullets;
		bullets = new ArrayList<EnemyState.Bullet>(bullets);
		touchSet.add(bullets);
		return bullets;
	}

	public EnemyState.Bullet touchBullet(int index) {
		EnemyState.Bullet bullet = bullets.get(index);
		if (touchSet.contains(bullet))
			return bullet;
		touchBullets();
		bullet = bullet.copy();
		bullets.set(index, bullet);
		touchSet.add(bullet);
		return bullet;
	}

	@Override
	public String toString() {
		return OOStateUtilities.ooStateToString(this);
	}
}
