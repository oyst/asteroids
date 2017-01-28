package uk.co.alexoyston.asteroids.simple_rl;

import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import uk.co.alexoyston.asteroids.simple_rl.state.AsteroidsState;
import uk.co.alexoyston.asteroids.simulation.PhysicsParams;

class AsteroidsTerminal implements TerminalFunction {

	private PhysicsParams phys;
	
	public AsteroidsTerminal(PhysicsParams phys) {
		this.phys = phys;
	}
	
	@Override
	public boolean isTerminal(State s) {
		return ((AsteroidsState)s).agent.alive <= 0;
	}
}