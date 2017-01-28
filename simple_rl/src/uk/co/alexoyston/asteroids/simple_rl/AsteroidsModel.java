package uk.co.alexoyston.asteroids.simple_rl;

import java.util.List;

import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;
import uk.co.alexoyston.asteroids.simple_rl.state.AsteroidsState;
import uk.co.alexoyston.asteroids.simulation.Simulation;

class AsteroidsModel implements FullStateModel{

	public AsteroidsModel() {
	}

	@Override
	public State sample(State s, Action a) {
		s = s.copy();
		AsteroidsState state = (AsteroidsState)s;
				
		state.agent.x += state.agent.vx * 0.01;
		state.agent.y += state.agent.vy * 0.01;
		
		return state;
	}

	@Override
	public List<StateTransitionProb> stateTransitions(State s, Action a) {
		return FullStateModel.Helper.deterministicTransition(this, s, a);
	}
}