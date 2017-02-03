package uk.co.alexoyston.asteroids.simple_rl.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;

import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.OOVariableKey;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.oo.state.exceptions.UnknownClassException;
import burlap.mdp.core.oo.state.exceptions.UnknownObjectException;
import burlap.mdp.core.state.StateUtilities;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.ShallowCopyState;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

@ShallowCopyState
public class AsteroidsState implements OOState {

	private static final float nullDistance = 10000f;
	private static final int closestAsteroidsCount = 3;
	private static final int closestSaucersCount = 1;
	private static final int closestBulletsCount = 5;

	private static final PolarState.Asteroid nullAsteroid = new PolarState.Asteroid("asteroidNull", nullDistance, 0f, 0f, 0f);
	private static final PolarState.Saucer nullSaucer = new PolarState.Saucer("saucerNull", nullDistance, 0f, 0f, 0f);
	private static final PolarState.Bullet nullBullet = new PolarState.Bullet("bulletNull", nullDistance, 0f, 0f, 0f);

	public AgentState agent;
	public List<ObjectInstance> asteroids = null;// = new PolarState.Asteroid[closestAsteroidsCount];
	public List<ObjectInstance> saucers = null;// = new PolarState.Saucer[closestSaucersCount];
	public List<ObjectInstance> bullets = null;// = new PolarState.Bullet[closestBulletsCount];

	public List<ObjectInstance> objects = null;

	public AsteroidsState() {
	}

	public AsteroidsState(AgentState agent) {
		this.agent = agent;
	}

	public AsteroidsState(AgentState agent, List<PolarState.Asteroid> asteroids, List<PolarState.Bullet> bullets, List<PolarState.Saucer> saucers) {
		this.agent = agent;
		this.asteroids = asteroids;
		this.saucers = saucers;
		this.bullets = bullets;

		this.asteroids = getClosest(asteroids, closestAsteroidsCount, nullAsteroid);
		this.saucers = getClosest(saucers, closestSaucersCount, nullSaucer);
		this.bullets = getClosest(bullets, closestBulletsCount, nullBullet);
	}

	private <T extends PolarState> List<ObjectInstance> getClosest(List<T> array, int desiredSize, T fillerObj) {
		List<ObjectInstance> out = new ArrayList<ObjectInstance>(desiredSize);

		if (desiredSize == 0)
			return out;

		if (desiredSize < array.size()) {
			out.addAll(array);
			for (int i = array.size(); i < desiredSize; i++)
				out.add(fillerObj);
			return out;
		}

		T min = array.get(start);
		T max = null;
		for (int start = 0; start < desiredSize; start++) {
			T curr;
			for (T elem : array) {
				if (elem.compareTo(min) < 0 && (max == null || elem.compareTo(max) > 0))
		// 			min = curr;
		// 	}
		// 	out.add(min);
		// }

		return out;
	}

	@Override
	public int numObjects() {
		return 1 + closestAsteroidsCount + closestSaucersCount + closestBulletsCount;
	}

	@Override
	public ObjectInstance object(String oname) {
		int index;

		if (agent.name().equals(oname))
			return agent;

		index = OOStateUtilities.objectIndexWithName(asteroids, oname);
		if (index != -1)
			return asteroids.get(index);

		index = OOStateUtilities.objectIndexWithName(saucers, oname);
		if (index != -1)
			return saucers.get(index);

		index = OOStateUtilities.objectIndexWithName(bullets, oname);
		if (index != -1)
			return bullets.get(index);

		throw new UnknownObjectException(oname);
	}

	@Override
	public List<ObjectInstance> objects() {
		if (objects == null) {
			objects = new ArrayList<ObjectInstance>(numObjects());
			objects.add(agent);
			objects.addAll(asteroids);
			objects.addAll(saucers);
			objects.addAll(bullets);
		}
		return objects;
	}

	@Override
	public List<ObjectInstance> objectsOfClass(String oclass) {
		List<ObjectInstance> objs = new ArrayList<ObjectInstance>();

		if(oclass.equals(CLASS_AGENT))
			return Arrays.<ObjectInstance>asList(agent);
		else if(oclass.equals(CLASS_ASTEROID))
			return asteroids;
		else if(oclass.equals(CLASS_SAUCER))
			return saucers;
		else if(oclass.equals(CLASS_BULLET))
			return bullets;

		throw new UnknownClassException(oclass);
	}

	@Override
	public Object get(Object variableKey) {
		return OOStateUtilities.get(this, variableKey);
	}

	@Override
	public List<Object> variableKeys() {
		return OOStateUtilities.flatStateKeys(this);
	}

	@Override
	public AsteroidsState copy() {
		return new AsteroidsState(agent, asteroids, bullets, saucers);
	}

	@Override
	public String toString() {
		return OOStateUtilities.ooStateToString(this);
	}
}
