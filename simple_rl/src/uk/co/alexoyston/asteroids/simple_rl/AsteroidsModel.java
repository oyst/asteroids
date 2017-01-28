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
import uk.co.alexoyston.asteroids.simple_rl.state.ThreatState;
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
			double ax = Math.sin(agent.rot) * 120f;
			double ay = Math.cos(agent.rot) * 120f;
			
			agent.x += agent.vx * 0.01 + ax * 0.01 * 0.01 * 0.5f;
			agent.y += agent.vy * 0.01 + ay * 0.01 * 0.01 * 0.5f;

			agent.vx += ax * 0.01;
			agent.vy += ay * 0.01;
			
			agent.vx -= agent.vx * 0.3 * 0.01;
			agent.vy -= agent.vy * 0.3 * 0.01;
			
		} else if (a.actionName().equals(ACTION_ROTATE_RIGHT)) {
			agent.rot += 3 * 0.01;
			agent.rot %= (Math.PI) * 2;
			
		} else if (a.actionName().equals(ACTION_ROTATE_LEFT)) {
			agent.rot += -3 * 0.01;
			agent.rot %= (Math.PI) * 2;
			
		} else if (a.actionName().equals(ACTION_SHOOT)) {
			
		}
		
		// Bounds check
		if (agent.x > 1000)
			agent.x = -agent.width;
		else if (agent.x + agent.width < 0)
			agent.x = 1000 + agent.width;
		
		if (agent.y > 1000)
			agent.y = -agent.height;
		else if (agent.y + agent.height < 0)
			agent.y = 1000 + agent.height;
		
	
		for (EnemyState.Asteroid asteroid : state.asteroids) {
			asteroid.x += asteroid.vx * 0.01;
			asteroid.y += asteroid.vy * 0.01;
			
			// Bounds check
			if (asteroid.x > 1000)
				asteroid.x = -asteroid.width;
			else if (asteroid.x + asteroid.width < 0)
				asteroid.x = 1000 + asteroid.width;
			
			if (asteroid.y > 1000)
				asteroid.y = -asteroid.height;
			else if (asteroid.y + asteroid.height < 0)
				asteroid.y = 1000 + asteroid.height;
		}
		
		for (EnemyState.Saucer saucer : state.saucers) {
			saucer.x += saucer.vx * 0.01;
			saucer.y += saucer.vy * 0.01;
			
			// Bounds check
			if (saucer.x > 1000)
				saucer.x = -saucer.width;
			else if (saucer.x + saucer.width < 0)
				saucer.x = 1000 + saucer.width;
			
			if (saucer.y > 1000)
				saucer.y = -saucer.height;
			else if (saucer.y + saucer.height < 0)
				saucer.y = 1000 + saucer.height;
		}
		
		for (ThreatState.Bullet bullet : state.bullets) {
			bullet.x += bullet.vx * 0.01;
			bullet.y += bullet.vy * 0.01;
			
			// Bounds check
			if (bullet.x > 1000)
				bullet.x = -2;
			else if (bullet.x + 2 < 0)
				bullet.x = 1000 + 2;
			
			if (bullet.y > 1000)
				bullet.y = -2;
			else if (bullet.y + 2 < 0)
				bullet.y = 1000 + 2;
		}
		
		return state;
	}
	
	@Override
	public List<StateTransitionProb> stateTransitions(State s, Action a) {
		return FullStateModel.Helper.deterministicTransition(this, s, a);
	}
}