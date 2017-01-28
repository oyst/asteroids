package uk.co.alexoyston.asteroids.simple_rl.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

@ShallowCopyState
public class AsteroidsState implements MutableOOState {
	
	public AgentState agent;
	public List<AsteroidState> asteroids;
	
	public AsteroidsState() {
	}

	public AsteroidsState(AgentState agent, List<AsteroidState> asteroids) {
		this.agent = agent;
		this.asteroids = asteroids;
	}

	public AsteroidsState(AgentState agent, AsteroidState... asteroids) {
		this.agent = agent;
		this.asteroids = Arrays.asList(asteroids);
	}
	
	@Override
	public MutableOOState addObject(ObjectInstance o) {
		if (o instanceof AgentState) {
			agent = (AgentState)o;
		}
		else if (o instanceof AsteroidState) {
			touchAsteroids().add((AsteroidState)o);
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
			AsteroidState obj = asteroids.get(index);
			touchAsteroids().remove(index);
			asteroids.add(index, (AsteroidState)obj.copyWithName(newName));
			return this;
		}
		
		throw new UnknownObjectException(objectName);
				
	}

	@Override
	public int numObjects() {
		// One agent and some asteroids
		return 1 + asteroids.size();
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
		
		throw new UnknownObjectException(oname);
	}

	@Override
	public List<ObjectInstance> objects() {
		List<ObjectInstance> obs = new ArrayList<ObjectInstance>(numObjects());
		obs.add(agent);
		obs.addAll(asteroids);
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
			else if (key.obVarKey.equals(VAR_LIVES)) {
				touchAgent().lives = num.intValue();
			}
			else if (key.obVarKey.equals(VAR_SCORE)) {
				touchAgent().score = num.intValue();
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
		return new AsteroidsState(agent, asteroids);
	}

	public List<AsteroidState> touchAsteroids() {
		asteroids = new ArrayList<AsteroidState>(asteroids);
		return asteroids;
	}
	
	public AsteroidState touchAsteroid(int index) {
		AsteroidState asteroid = asteroids.get(index).copy();
		touchAsteroids().remove(index);
		touchAsteroids().add(index, asteroid);
		return asteroid;
	}
	
	public AgentState touchAgent() {
		agent = agent.copy();
		return agent;
	}
	
	@Override
	public String toString() {
		return OOStateUtilities.ooStateToString(this);
	}
}