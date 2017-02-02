package uk.co.alexoyston.asteroids.simple_rl;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.OODomain;
import burlap.mdp.core.oo.propositional.PropositionalFunction;

import uk.co.alexoyston.asteroids.simulation.PhysicsParams;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

class AsteroidsReward implements RewardFunction {

	private PhysicsParams phys;

	private int collisionReward = -100;
	private int defaultReward = 0;
	private int shootReward = -1;

	protected PropositionalFunction killed;
	protected PropositionalFunction shotAsteroid;
	protected PropositionalFunction shotSaucer;

	public AsteroidsReward(OODomain domain, PhysicsParams phys) {
		this.phys = phys;
		killed = domain.propFunction(PF_AGENT_KILLED);
		shotAsteroid = domain.propFunction(PF_SHOT_ASTEROID);
		shotSaucer = domain.propFunction(PF_SHOT_SAUCER);
	}

	@Override
	public double reward(State s, Action a, State sprime) {
		if (shotAsteroid.someGroundingIsTrue((OOState)sprime)) {
			return phys.playerAsteroidHitScore;
		}

		if (shotSaucer.someGroundingIsTrue((OOState)sprime)) {
			return phys.playerSaucerHitScore;
		}

		if (killed.someGroundingIsTrue((OOState)sprime)) {
			return collisionReward;
		}

		if (a.actionName().equals(ACTION_SHOOT))
			return shootReward;

		return defaultReward;
	}
}
