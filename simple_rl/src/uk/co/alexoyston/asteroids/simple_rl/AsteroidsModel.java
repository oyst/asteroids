package uk.co.alexoyston.asteroids.simple_rl;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.ACTION_FORWARD;
import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.ACTION_ROTATE_LEFT;
import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.ACTION_ROTATE_RIGHT;
import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.ACTION_SHOOT;

import java.util.List;

import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;

import uk.co.alexoyston.asteroids.simple_rl.state.AgentState;
import uk.co.alexoyston.asteroids.simple_rl.state.AsteroidsState;
import uk.co.alexoyston.asteroids.simple_rl.state.EnemyState;

import uk.co.alexoyston.asteroids.simulation.PhysicsParams;

class AsteroidsModel implements FullStateModel{

	PhysicsParams phys;

	public AsteroidsModel(PhysicsParams phys) {
		this.phys = phys;
	}

	@Override
	public State sample(State s, Action a) {
		s = s.copy();
		AsteroidsState state = (AsteroidsState)s;

		AgentState agent = state.agent;

		if (a.actionName().equals(ACTION_FORWARD)) {
			double ax = Math.sin(agent.rot) * phys.playerThrustPower;
			double ay = Math.cos(agent.rot) * phys.playerThrustPower;

			agent.x += agent.vx * phys.updateDelta + ax * phys.updateDelta * phys.updateDelta * 0.5f;
			agent.y += agent.vy * phys.updateDelta + ay * phys.updateDelta * phys.updateDelta * 0.5f;

			agent.vx += ax * phys.updateDelta;
			agent.vy += ay * phys.updateDelta;

			agent.vx -= agent.vx * phys.playerDrag * phys.updateDelta;
			agent.vy -= agent.vy * phys.playerDrag * phys.updateDelta;

		} else if (a.actionName().equals(ACTION_ROTATE_RIGHT)) {
			agent.rot += phys.playerRotationPower * phys.updateDelta;
			agent.rot %= (Math.PI) * 2;

		} else if (a.actionName().equals(ACTION_ROTATE_LEFT)) {
			agent.rot += -phys.playerRotationPower * phys.updateDelta;
			agent.rot %= (Math.PI) * 2;

		} else if (a.actionName().equals(ACTION_SHOOT)) {

		}

		// Bounds check
		if (agent.x > phys.worldWidth)
			agent.x = -agent.width;
		else if (agent.x + agent.width < 0)
			agent.x = phys.worldWidth + agent.width;

		if (agent.y > phys.worldHeight)
			agent.y = -agent.height;
		else if (agent.y + agent.height < 0)
			agent.y = phys.worldHeight + agent.height;


		for (EnemyState.Asteroid asteroid : state.asteroids) {
			asteroid.x += asteroid.vx * phys.updateDelta;
			asteroid.y += asteroid.vy * phys.updateDelta;

			// Bounds check
			if (asteroid.x > phys.worldWidth)
				asteroid.x = -asteroid.width;
			else if (asteroid.x + asteroid.width < 0)
				asteroid.x = phys.worldWidth + asteroid.width;

			if (asteroid.y > phys.worldHeight)
				asteroid.y = -asteroid.height;
			else if (asteroid.y + asteroid.height < 0)
				asteroid.y = phys.worldHeight + asteroid.height;
		}

		for (EnemyState.Saucer saucer : state.saucers) {
			saucer.x += saucer.vx * phys.updateDelta;
			saucer.y += saucer.vy * phys.updateDelta;

			// Bounds check
			if (saucer.x > phys.worldWidth)
				saucer.x = -saucer.width;
			else if (saucer.x + saucer.width < 0)
				saucer.x = phys.worldWidth + saucer.width;

			if (saucer.y > phys.worldHeight)
				saucer.y = -saucer.height;
			else if (saucer.y + saucer.height < 0)
				saucer.y = phys.worldHeight + saucer.height;
		}

		for (EnemyState.Bullet bullet : state.bullets) {
			bullet.x += bullet.vx * phys.updateDelta;
			bullet.y += bullet.vy * phys.updateDelta;

			// Bounds check
			if (bullet.x > phys.worldWidth)
				bullet.x = -bullet.width;
			else if (bullet.x + bullet.width < 0)
				bullet.x = phys.worldWidth + bullet.width;

			if (bullet.y > phys.worldHeight)
				bullet.y = -bullet.height;
			else if (bullet.y + bullet.height < 0)
				bullet.y = phys.worldHeight + bullet.height;
		}

		return state;
	}

	@Override
	public List<StateTransitionProb> stateTransitions(State s, Action a) {
		return FullStateModel.Helper.deterministicTransition(this, s, a);
	}
}
