package uk.co.alexoyston.asteroids.simple_rl;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;

class AsteroidsReward implements RewardFunction {

	public AsteroidsReward(){
	}

	@Override
	public double reward(State s, Action a, State sprime) {
		return -1;
	}


}