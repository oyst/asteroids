package uk.co.alexoyston.asteroids.simple_rl;

import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;

class AsteroidsTerminal implements TerminalFunction {

	public AsteroidsTerminal(){
	}

	@Override
	public boolean isTerminal(State s) {
		return false;
	}
}