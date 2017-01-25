package uk.co.alexoyston.asteroids.simple_rl;

import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.StateUtilities;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.DeepCopyState;

@DeepCopyState
public class AsteroidsState implements MutableState{

	public int x;
	public int y;

	private final static List<Object> keys = Arrays.<Object>asList();

	public AsteroidsState() {
	}

	public AsteroidsState(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public MutableState set(Object variableKey, Object value) {
//		if(variableKey.equals(VAR_X)){
//			this.x = StateUtilities.stringOrNumber(value).intValue();
//		}
//		else{
//			throw new UnknownKeyException(variableKey);
//		}
		return this;
	}

	public List<Object> variableKeys() {
		return keys;
	}

	@Override
	public Object get(Object variableKey) {
//		if(variableKey.equals(VAR_X)){
//			return x;
//		}
		throw new UnknownKeyException(variableKey);
	}

	@Override
	public AsteroidsState copy() {
		return new AsteroidsState(x, y);
	}

	@Override
	public String toString() {
		return StateUtilities.stateToString(this);
	}
}