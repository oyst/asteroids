package uk.co.alexoyston.asteroids.simple_rl;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;

import uk.co.alexoyston.asteroids.simple_rl.state.AgentState;
import uk.co.alexoyston.asteroids.simple_rl.state.AsteroidsState;
import uk.co.alexoyston.asteroids.simple_rl.state.EnemyState;

import uk.co.alexoyston.asteroids.simulation.PhysicsParams;

class AsteroidsReward implements RewardFunction {

	private PhysicsParams phys;

	public AsteroidsReward(PhysicsParams phys) {
		this.phys = phys;
	}

	@Override
	public double reward(State s, Action a, State sprime) {
		AsteroidsState as = (AsteroidsState)s;
		AsteroidsState asprime = (AsteroidsState)sprime;


		AgentState agent = as.agent;
		for (EnemyState.Asteroid asteroid : as.asteroids) {
			if (collides(
					agent.x, agent.y, agent.width, agent.height,
					asteroid.x, asteroid.y, asteroid.width, asteroid.height)) {
				return 0;
			}
		}

		agent = asprime.agent;
		for (EnemyState.Asteroid asteroid : asprime.asteroids) {
			if (collides(
					agent.x, agent.y, agent.width, agent.height,
					asteroid.x, asteroid.y, asteroid.width, asteroid.height)) {
				return -100;
			}
			for (EnemyState.Bullet bullet: asprime.bullets) {
				if (collides(
						bullet.x, bullet.y, bullet.width, bullet.height,
						asteroid.x, asteroid.y, asteroid.width, asteroid.height)) {
					return phys.playerAsteroidHitScore;
				}
			}
		}

		return 0;
	}

	public boolean collides(
			float r1_x, float r1_y, float r1_width, float r1_height,
			float r2_x, float r2_y, float r2_width, float r2_height) {

		return (r1_x < r2_x + r2_width &&
				r1_x + r1_width > r2_x &&
				r1_y < r2_y + r2_height &&
				r1_height + r1_y > r2_y);
	}


}
