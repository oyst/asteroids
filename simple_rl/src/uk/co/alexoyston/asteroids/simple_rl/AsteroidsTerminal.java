package uk.co.alexoyston.asteroids.simple_rl;

import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;

import uk.co.alexoyston.asteroids.simple_rl.state.AsteroidsState;
import uk.co.alexoyston.asteroids.simple_rl.state.AgentState;
import uk.co.alexoyston.asteroids.simple_rl.state.EnemyState;

import uk.co.alexoyston.asteroids.simulation.PhysicsParams;

class AsteroidsTerminal implements TerminalFunction {

	private PhysicsParams phys;

	public AsteroidsTerminal(PhysicsParams phys) {
		this.phys = phys;
	}

	@Override
	public boolean isTerminal(State s) {
		AsteroidsState as = (AsteroidsState)s;
		AgentState agent = as.agent;

		for (EnemyState.Asteroid asteroid : as.asteroids) {
			if (collides(
					agent.x, agent.y, agent.width, agent.height,
					asteroid.x, asteroid.y, asteroid.width, asteroid.height)) {
				return true;
			}
		}

		for (EnemyState.Saucer saucer : as.saucers) {
			if (collides(
					agent.x, agent.y, agent.width, agent.height,
					saucer.x, saucer.y, saucer.width, saucer.height)) {
				return true;
			}
		}

		for (EnemyState.Bullet bullet : as.bullets) {
			if (collides(
					agent.x, agent.y, agent.width, agent.height,
					bullet.x, bullet.y, bullet.width, bullet.height)) {
				return true;
			}
		}

		return false;
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
