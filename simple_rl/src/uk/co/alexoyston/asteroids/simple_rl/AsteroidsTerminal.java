package uk.co.alexoyston.asteroids.simple_rl;

import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.core.oo.OODomain;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.oo.state.OOState;

import uk.co.alexoyston.asteroids.simulation.PhysicsParams;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

class AsteroidsTerminal implements TerminalFunction {

	private PhysicsParams phys;

	protected PropositionalFunction killed;

	public AsteroidsTerminal(OODomain domain, PhysicsParams phys) {
		this.phys = phys;
		killed = domain.propFunction(PF_AGENT_KILLED);
	}

	@Override
	public boolean isTerminal(State s) {
		return killed.someGroundingIsTrue((OOState)s);
	}
}
