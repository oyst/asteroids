package uk.co.alexoyston.asteroids.simple_rl;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

import java.awt.Color;
import java.util.ArrayList;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;

import uk.co.alexoyston.asteroids.simple_rl.AsteroidsVisualizer.VisualEntity;
import uk.co.alexoyston.asteroids.simple_rl.state.AgentState;
import uk.co.alexoyston.asteroids.simple_rl.state.AsteroidsState;
import uk.co.alexoyston.asteroids.simple_rl.state.EnemyState;

import uk.co.alexoyston.asteroids.simulation.Asteroid;
import uk.co.alexoyston.asteroids.simulation.PhysicsParams;
import uk.co.alexoyston.asteroids.simulation.Bullet;
import uk.co.alexoyston.asteroids.simulation.Entity;
import uk.co.alexoyston.asteroids.simulation.Player;
import uk.co.alexoyston.asteroids.simulation.Saucer;
import uk.co.alexoyston.asteroids.simulation.Simulation;
import uk.co.alexoyston.asteroids.simulation.SmallSaucer;

public class AsteroidsEnvironment implements Environment {

	private Simulation sim;
	private int lastReward = 0;
	private PhysicsParams phys;

	public AsteroidsEnvironment(PhysicsParams phys) {
		this.phys = phys;
		resetEnvironment();
	}

	@Override
	public State currentObservation() {
		if (this.sim.players.size() == 0) {
			AgentState agent = new AgentState();
			return new AsteroidsState(agent);
		}

		ArrayList<EnemyState.Asteroid> asteroids = new ArrayList<EnemyState.Asteroid>();
		ArrayList<EnemyState.Saucer> saucers = new ArrayList<EnemyState.Saucer>();
		ArrayList<EnemyState.Bullet> bullets = new ArrayList<EnemyState.Bullet>();
		Player player = this.sim.players.get(0);

		AgentState agent = new AgentState(
			player.location.x,
			player.location.y,
			player.bounds.width,
			player.bounds.height,
			player.rotation,
			player.velocity.x,
			player.velocity.y,
			player.activeShots
		);

		for (Entity entity : this.sim.entities) {
			if (entity instanceof Asteroid) {
				EnemyState.Asteroid asteroid = new EnemyState.Asteroid(
						"Asteroid",
						entity.location.x,
						entity.location.y,
						entity.velocity.x,
						entity.velocity.y,
						entity.bounds.width,
						entity.bounds.height,
						entity.rotation);
				asteroids.add(asteroid);
			}

			if (entity instanceof Saucer || entity instanceof SmallSaucer) {
				EnemyState.Saucer saucer = new EnemyState.Saucer(
						"Saucer",
						entity.location.x,
						entity.location.y,
						entity.velocity.x,
						entity.velocity.y,
						entity.bounds.width,
						entity.bounds.height,
						entity.rotation);
				saucers.add(saucer);
			}

			if (entity instanceof Bullet) {
				EnemyState.Bullet bullet = new EnemyState.Bullet(
						"Bullet",
						entity.location.x,
						entity.location.y,
						entity.velocity.x,
						entity.velocity.y,
						entity.bounds.width,
						entity.bounds.height,
						entity.rotation);
				bullets.add(bullet);
			}
		}

		State state = new AsteroidsState(agent, asteroids, bullets, saucers);
		return state;
	}

	public VisualEntity[] getVisualEntities() {

		int numEntities = sim.entities.size() + sim.players.size();
		VisualEntity[] entities = new VisualEntity[numEntities];
		int currEntity = 0;

		for (Entity entity : sim.entities) {
			entities[currEntity] = new VisualEntity(entity.getVertices(), Color.BLACK);
			currEntity++;
		}
		for (Entity player : sim.players) {
			entities[currEntity] = new VisualEntity(player.getVertices(), Color.BLACK);
			currEntity++;
		}

		return entities;
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
			lastReward = -100;
		else
			lastReward = newScore - oldScore;

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

	@Override
	public void resetEnvironment() {
		this.sim = new Simulation(this.phys);
		this.sim.addPlayer(this.phys.worldWidth / 2, this.phys.worldHeight / 2);
		lastReward = 0;
	}

}
