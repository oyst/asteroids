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
	private static final int closestSaucersCount = 0;
	private static final int closestBulletsCount = 3;

	private static final PolarState.Asteroid nullAsteroid = new PolarState.Asteroid("asteroidNull", nullDistance, 0f, 0f, 0f);
	private static final PolarState.Saucer nullSaucer = new PolarState.Saucer("saucerNull", nullDistance, 0f, 0f, 0f);
	private static final PolarState.Bullet nullBullet = new PolarState.Bullet("bulletNull", nullDistance, 0f, 0f, 0f);

	private HashSet<Object> touchSet;

	public AgentState agent;
	public PolarState.Asteroid[] asteroids = new PolarState.Asteroid[closestAsteroidsCount];
	public PolarState.Saucer[] saucers = new PolarState.Saucer[closestSaucersCount];
	public PolarState.Bullet[] bullets = new PolarState.Bullet[closestBulletsCount];

	public AsteroidsState() {
	}

	public AsteroidsState(AgentState agent) {
		this.agent = agent;
		for (int i = 0; i < asteroids.length; i++) asteroids[i] = nullAsteroid;
		for (int i = 0; i < saucers.length; i++) saucers[i] = nullSaucer;
		for (int i = 0; i < bullets.length; i++) bullets[i] = nullBullet;
	}

	public AsteroidsState(AgentState agent, PolarState.Asteroid[] asteroids, PolarState.Bullet[] bullets, PolarState.Saucer[] saucers) {
		this.agent = agent;
		this.asteroids = asteroids;
		this.saucers = saucers;
		this.bullets = bullets;
		touchSet = new HashSet<Object>();
	}

	public AsteroidsState(AgentState agent, List<PolarState.Asteroid> asteroids, List<PolarState.Bullet> bullets, List<PolarState.Saucer> saucers) {
		this.agent = agent;
		selectSort(asteroids, this.asteroids, nullAsteroid);
		System.out.println(asteroids.size() != 0 ? asteroids.get(0).dist : "n");
		selectSort(saucers, this.saucers, nullSaucer);
		selectSort(bullets, this.bullets, nullBullet);
		touchSet = new HashSet<Object>();
	}

	private static <T extends PolarState> void selectSort(List<T> src, T[] dst, T fillerState) {
		T min;
		T dst_max = null;
		for (int i = 0; i < dst.length; i++) {
			min = fillerState;
			for (T elem : src) {
				if ((elem.compareTo(min) < 0) && (dst_max == null || elem.compareTo(dst_max) > 0))
					min = elem;
			}
			dst_max = min;
			dst[i] = dst_max;
		}
	}

	@Override
	public int numObjects() {
		return 1 + asteroids.length + bullets.length + saucers.length;
	}

	@Override
	public ObjectInstance object(String oname) {
		int index;

		if (agent.name().equals(oname))
			return agent;

		index = objectIndexWithName(asteroids, oname);
		if (index != -1)
			return asteroids[index];

		index = objectIndexWithName(saucers, oname);
		if (index != -1)
			return saucers[index];

		index = objectIndexWithName(bullets, oname);
		if (index != -1)
			return bullets[index];

		throw new UnknownObjectException(oname);
	}

	@Override
	public List<ObjectInstance> objects() {
		List<ObjectInstance> objs = new ArrayList<ObjectInstance>(numObjects());
		objs.add(agent);
		objs.addAll(Arrays.asList(asteroids));
		objs.addAll(Arrays.asList(saucers));
		objs.addAll(Arrays.asList(bullets));
		return objs;
	}

	@Override
	public List<ObjectInstance> objectsOfClass(String oclass) {
		List<ObjectInstance> objs = new ArrayList<ObjectInstance>();

		if(oclass.equals(CLASS_AGENT)){
			return Arrays.<ObjectInstance>asList(agent);
		}
		else if(oclass.equals(CLASS_ASTEROID)){
			objs.addAll(Arrays.asList(asteroids));
			return objs;
		}
		else if(oclass.equals(CLASS_SAUCER)){
			objs.addAll(Arrays.asList(saucers));
			return objs;
		}
		else if(oclass.equals(CLASS_BULLET)){
			objs.addAll(Arrays.asList(bullets));
			return objs;
		}
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
		AsteroidsState state = new AsteroidsState(agent, asteroids, bullets, saucers);
		state.resetTouchSet();
		return state;
	}

	public void resetTouchSet() {
		touchSet = new HashSet<Object>();
	}

	@Override
	public String toString() {
		return OOStateUtilities.ooStateToString(this);
	}

	public <T extends ObjectInstance> int objectIndexWithName(T[] objects, String name) {
		for (int i = 0; i < objects.length; i++)
			if (objects[i].name().equals(name)) return i;
		return -1;
	}
}
