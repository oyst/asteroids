package uk.co.alexoyston.asteroids.simple_rl;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;

import uk.co.alexoyston.asteroids.simulation.Simulation;

public class AsteroidsEnvironment implements Environment {

	private Simulation sim;
	
	public AsteroidsEnvironment() {
		this.sim = new Simulation(200, 200);
		this.sim.addPlayer(100f, 100f);
	}
	
	@Override
	public State currentObservation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnvironmentOutcome executeAction(Action a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double lastReward() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isInTerminalState() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetEnvironment() {
		// TODO Auto-generated method stub
		
	}

}
