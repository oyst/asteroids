package uk.co.alexoyston.asteroids.simple_rl;

import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import uk.co.alexoyston.asteroids.simple_rl.state.AsteroidsState;

class AsteroidsTerminal implements TerminalFunction {

	@Override
	public boolean isTerminal(State s) {
		return ((AsteroidsState)s).agent.lives <= 0;
	}
}