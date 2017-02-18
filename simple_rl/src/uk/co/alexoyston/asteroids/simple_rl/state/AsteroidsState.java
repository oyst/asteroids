package uk.co.alexoyston.asteroids.simple_rl.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Collections;

import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.oo.state.exceptions.UnknownClassException;
import burlap.mdp.core.oo.state.exceptions.UnknownObjectException;
import burlap.mdp.core.state.annotations.ShallowCopyState;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

@ShallowCopyState
public class AsteroidsState implements OOState {

	public static final int closestAsteroidsCount = 2;
	public static final int closestSaucersCount = 0;
	public static final int closestBulletsCount = 0;

	private static final PolarState nullObj = new PolarState("obj");

	public AgentState agent;
	public List<ObjectInstance> objs = new ArrayList<ObjectInstance>();

	public AsteroidsState() {
	}

	public AsteroidsState(AgentState agent) {
		this(agent, null, null, null);
	}

	public AsteroidsState(AgentState agent, List<ObjectInstance> objs) {
		this.agent = agent;
		this.objs = objs;
	}

	public AsteroidsState(AgentState agent, List<PolarState> asteroids, List<PolarState> bullets, List<PolarState> saucers) {
		this.agent = agent;

		getClosest(objs, asteroids, closestAsteroidsCount, nullObj);
		getClosest(objs, saucers, closestSaucersCount, nullObj);
		getClosest(objs, bullets, closestBulletsCount, nullObj);
	}

	private void getClosest(List<ObjectInstance> out, List<PolarState> array, int desiredSize, PolarState fillerObj) {
		if (desiredSize == 0)
			return;

		if (array == null)
			array = new ArrayList<PolarState>(0);

		if (desiredSize > array.size()) {
			out.addAll(array);
			for (int i = array.size(); i < desiredSize; i++)
				out.add(fillerObj);
			return;
		}

		Collections.sort(array);
		for (int i = 0; i < desiredSize; i++)
			out.add(array.get(i));
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

		index = OOStateUtilities.objectIndexWithName(objs, oname);
		if (index != -1)
			return objs.get(index);

		throw new UnknownObjectException(oname);
	}

	@Override
	public List<ObjectInstance> objects() {
		return objs;
	}

	@Override
	public List<ObjectInstance> objectsOfClass(String oclass) {

		if(oclass.equals(CLASS_AGENT))
			return Arrays.<ObjectInstance>asList(agent);

		else if(oclass.equals(CLASS_OBJECT))
			return objs;

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
		return new AsteroidsState(agent, objs);
	}

	@Override
	public String toString() {
		return OOStateUtilities.ooStateToString(this);
	}
}
