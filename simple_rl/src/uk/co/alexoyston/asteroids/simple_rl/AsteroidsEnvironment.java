package uk.co.alexoyston.asteroids.simple_rl;

import java.awt.Color;
import java.util.ArrayList;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;

import uk.co.alexoyston.asteroids.simple_rl.state.AgentState;
import uk.co.alexoyston.asteroids.simple_rl.state.AsteroidsState;
import uk.co.alexoyston.asteroids.simple_rl.state.PolarState;

import uk.co.alexoyston.asteroids.simulation.Asteroid;
import uk.co.alexoyston.asteroids.simulation.PhysicsParams;
import uk.co.alexoyston.asteroids.simulation.Bullet;
import uk.co.alexoyston.asteroids.simulation.Entity;
import uk.co.alexoyston.asteroids.simulation.Player;
import uk.co.alexoyston.asteroids.simulation.Saucer;
import uk.co.alexoyston.asteroids.simulation.Simulation;
import uk.co.alexoyston.asteroids.simulation.SmallSaucer;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

public class AsteroidsEnvironment implements Environment {

	private Simulation sim;
	private int lastReward = 0;
	private PhysicsParams phys;

	public AsteroidsEnvironment(PhysicsParams phys) {
		this.phys = phys;
		resetEnvironment();
	}

	@Override
	public void resetEnvironment() {
		sim = new Simulation(phys);
		sim.addPlayer(phys.worldWidth / 2, phys.worldHeight / 2);
		lastReward = 0;
	}

	@Override
	public State currentObservation() {
		if (sim.players.size() == 0)
			return new AsteroidsState(new AgentState());

		ArrayList<PolarState.Asteroid> asteroids = new ArrayList<PolarState.Asteroid>();
		ArrayList<PolarState.Saucer> saucers = new ArrayList<PolarState.Saucer>();
		ArrayList<PolarState.Bullet> bullets = new ArrayList<PolarState.Bullet>();
		Player player = sim.players.get(0);

		int x = (int)player.bounds.x;
		int y = (int)player.bounds.y;
		int rad = (int)Math.max(player.bounds.width, player.bounds.height) / 2;
		float rot = player.rotation;

		AgentState agent = new AgentState(rad, rot, player.activeShots);

		for (Entity entity : this.sim.entities) {
			float dx = entity.bounds.x - player.bounds.x;
			float dy = entity.bounds.y - player.bounds.y;
			// if (Math.abs(dx) > phys.worldWidth / 2)
			// 	dx = (phys.worldWidth - Math.abs(dx)) * -Math.signum(dx);
			// if (Math.abs(dy) > phys.worldHeight / 2)
			// 	dy = (phys.worldHeight - Math.abs(dy)) * -Math.signum(dy);

			float dist = (float)Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
			float angle = (float)Math.atan(dy/dx) + (dx > 0 ? (float)Math.PI : 0f);

			rad = (int)Math.max(entity.bounds.width, entity.bounds.height) / 2;

			float vx = entity.velocity.x - player.velocity.x;
			float vy = entity.velocity.y - player.velocity.y;

			if (entity instanceof Asteroid) {
				PolarState.Asteroid asteroid = new PolarState.Asteroid("asteroid", rad, dist, angle, vx, vy);
				asteroids.add(asteroid);
			}

			if (entity instanceof Saucer || entity instanceof SmallSaucer) {
				PolarState.Saucer saucer = new PolarState.Saucer("saucer", rad, dist, angle, vx, vy);
				saucers.add(saucer);
			}

			if (entity instanceof Bullet) {
				PolarState.Bullet bullet = new PolarState.Bullet("bullet", rad, dist, angle, vx, vy);
				bullets.add(bullet);
			}
		}

		State state = new AsteroidsState(agent, asteroids, bullets, saucers);
		return state;
	}

	@Override
	public EnvironmentOutcome executeAction(Action a) {
		Player player;
		State oldState, newState;
		int oldScore, newScore;

		player = this.sim.players.get(0);

		oldState = currentObservation();
		oldScore = player.getScore();

		if (a.actionName().equals(ACTION_FORWARD))
			this.sim.playerFwd(0);
		else if (a.actionName().equals(ACTION_ROTATE_RIGHT))
			this.sim.playerRotRight(0);
		else if (a.actionName().equals(ACTION_ROTATE_LEFT))
		this.sim.playerRotLeft(0);
		else if (a.actionName().equals(ACTION_SHOOT))
			this.sim.playerShoot(0);

		this.sim.update(phys.updateDelta);

		newState = currentObservation();
		newScore = player.getScore();

		if (this.sim.players.size() == 0)
			lastReward = -1000;
		else
			lastReward = newScore - oldScore;
		lastReward--;

		return new EnvironmentOutcome(oldState, a, newState, lastReward, isInTerminalState());
	}

	@Override
	public double lastReward() {
		return lastReward;
	}

	@Override
	public boolean isInTerminalState() {
		return this.sim.players.size() == 0;
	}

}
