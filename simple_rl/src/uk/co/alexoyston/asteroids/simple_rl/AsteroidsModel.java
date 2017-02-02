package uk.co.alexoyston.asteroids.simple_rl;

import java.util.List;

import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;

import uk.co.alexoyston.asteroids.simulation.PhysicsParams;

import uk.co.alexoyston.asteroids.simple_rl.state.AgentState;
import uk.co.alexoyston.asteroids.simple_rl.state.AsteroidsState;
import uk.co.alexoyston.asteroids.simple_rl.state.PolarState;
import uk.co.alexoyston.asteroids.simple_rl.state.EntityState;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

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
			double ax = Math.sin(agent.rotation) * phys.playerThrustPower;
			double ay = Math.cos(agent.rotation) * phys.playerThrustPower;

			agent.vx += ax * phys.updateDelta;
			agent.vy += ay * phys.updateDelta;
			agent.x += ax * phys.updateDelta * phys.updateDelta * 0.5f;
			agent.y += ay * phys.updateDelta * phys.updateDelta * 0.5f;

		} else if (a.actionName().equals(ACTION_ROTATE_RIGHT)) {
			agent.rotation += phys.playerRotationPower * phys.updateDelta;
			agent.rotation %= (Math.PI) * 2;

		} else if (a.actionName().equals(ACTION_ROTATE_LEFT)) {
			agent.rotation += -phys.playerRotationPower * phys.updateDelta;
			agent.rotation %= (Math.PI) * 2;

		} else if (a.actionName().equals(ACTION_SHOOT)) {
			agent.activeShots++;
		}

		agent.vx -= agent.vx * phys.playerDrag * phys.updateDelta;
		agent.vy -= agent.vy * phys.playerDrag * phys.updateDelta;
		agent.x += agent.vx * phys.updateDelta;
		agent.y += agent.vy * phys.updateDelta;

		// Bounds check
		if (agent.x > phys.worldWidth)
			agent.x = -agent.width;
		else if (agent.x + agent.width < 0)
			agent.x = phys.worldWidth + agent.width;

		if (agent.y > phys.worldHeight)
			agent.y = -agent.height;
		else if (agent.y + agent.height < 0)
			agent.y = phys.worldHeight + agent.height;


		for (PolarState.Asteroid asteroid : state.asteroids) {
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

			asteroid.dist = (float) Math.sqrt(Math.pow(asteroid.x - agent.x, 2) + Math.pow(asteroid.y - agent.y, 2));
			asteroid.angle = (float) Math.tan((agent.y + asteroid.y) / (agent.x + asteroid.x));
			asteroid.angle -= agent.rotation;
			asteroid.vx -= agent.vx;
			asteroid.vy -= agent.vy;
		}

		for (PolarState.Saucer saucer : state.saucers) {
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

			saucer.dist = (float) Math.sqrt(Math.pow(saucer.x - agent.x, 2) + Math.pow(saucer.y - agent.y, 2));
			saucer.angle = (float) Math.tan((agent.y + saucer.y) / (agent.x + saucer.x));
			saucer.angle -= agent.rotation;
			saucer.vx -= agent.vx;
			saucer.vy -= agent.vy;
		}

		for (PolarState.Bullet bullet : state.bullets) {
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

			bullet.dist = (float) Math.sqrt(Math.pow(bullet.x - agent.x, 2) + Math.pow(bullet.y - agent.y, 2));
			bullet.angle = (float) Math.tan((agent.y + bullet.y) / (agent.x + bullet.x));
			bullet.angle -= agent.rotation;
			bullet.vx -= agent.vx;
			bullet.vy -= agent.vy;
		}

		return state;
	}

	@Override
	public List<StateTransitionProb> stateTransitions(State s, Action a) {
		return FullStateModel.Helper.deterministicTransition(this, s, a);
	}

	private EntityState updateEntity(EntityState state) {
		// Update absolute location
		state.x += state.vx * phys.updateDelta;
		state.y += state.vy * phys.updateDelta;

		// Bounds check
		if (state.x > phys.worldWidth)
			state.x = -state.width;
		else if (state.x + state.width < 0)
			state.x = phys.worldWidth + state.width;

		if (state.y > phys.worldHeight)
			state.y = -state.height;
		else if (state.y + state.height < 0)
			state.y = phys.worldHeight + state.height;

		return state;
	}

	private PolarState updatePolar(PolarState state, AgentState origin) {
		state.dist = (float) Math.sqrt(Math.pow(state.x - origin.x, 2) + Math.pow(state.y - origin.y, 2));
		state.angle = (float) Math.tan((origin.y + state.y) / (origin.x + state.x));
		state.angle -= origin.rotation;
		state.vx -= origin.vx;
		state.vy -= origin.vy;
		return state;
	}

}
