package uk.co.alexoyston.asteroids.simple_rl;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import uk.co.alexoyston.asteroids.simple_rl.state.AsteroidsState;

class AsteroidsReward implements RewardFunction {

	public AsteroidsReward(){
	}

	@Override
	public double reward(State s, Action a, State sprime) {
		AsteroidsState as = (AsteroidsState)s;
		AsteroidsState asprime = (AsteroidsState)sprime;
		
		if (as.agent.lives > asprime.agent.lives)
			return -100;
		
		if (as.agent.score < asprime.agent.score)
			return 1000;
		
		return 1;
	}


}