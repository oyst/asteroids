package uk.co.alexoyston.asteroids.simple_rl;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.ACTION_FORWARD;
import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.ACTION_ROTATE_LEFT;
import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.ACTION_ROTATE_RIGHT;
import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.ACTION_SHOOT;

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
import uk.co.alexoyston.asteroids.simple_rl.state.ThreatState;
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
	private int reward = 0;
	private boolean terminal = false;
	private PhysicsParams phys;

	public AsteroidsEnvironment(PhysicsParams phys) {
		this.phys = phys;
		resetEnvironment();
	}

	@Override
	public State currentObservation() {
		if (this.sim.players.size() == 0) {
			AgentState agent = new AgentState();
			terminal = true;
			return new AsteroidsState(agent);
		}

		Player player = this.sim.players.get(0);

		float x = player.location.x;
		float y = player.location.y;
		float width = player.bounds.width;
		float height = player.bounds.height;
		float rot = player.rotation;
		float vx = player.velocity.x;
		float vy = player.velocity.y;
		AgentState agent = new AgentState(x, y, width, height, rot, vx, vy);

		ArrayList<EnemyState.Asteroid> asteroids = new ArrayList<EnemyState.Asteroid>();
		ArrayList<EnemyState.Saucer> saucers = new ArrayList<EnemyState.Saucer>();
		ArrayList<ThreatState.Bullet> bullets = new ArrayList<ThreatState.Bullet>();

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
				ThreatState.Bullet bullet = new ThreatState.Bullet(
						"Bullet",
						entity.location.x,
						entity.location.y,
						entity.velocity.x,
						entity.velocity.y);
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
		State oldState = currentObservation();

		if (this.sim.players.size() == 0) {
			terminal = true;
			reward = -100;
			return new EnvironmentOutcome(oldState, a, oldState, reward, terminal);
		}

		Player player = this.sim.players.get(0);
		int oldScore = player.getScore();

		if (a.actionName().equals(ACTION_FORWARD)) {
			this.sim.playerFwd(0);
		}
		else if (a.actionName().equals(ACTION_ROTATE_RIGHT)) {
			this.sim.playerRotRight(0);
		}
		else if (a.actionName().equals(ACTION_ROTATE_LEFT)) {
			this.sim.playerRotLeft(0);
		}
		else if (a.actionName().equals(ACTION_SHOOT)) {
			this.sim.playerShoot(0);
		}

		this.sim.update(0.01f);

		State newState = currentObservation();
		int newScore = player.getScore();

		if (this.sim.players.size() == 0) {
			terminal = true;
			reward = -100;
			return new EnvironmentOutcome(oldState, a, newState, reward, terminal);
		}

		reward = oldScore - newScore + 1;

		return new EnvironmentOutcome(oldState, a, newState, reward, isInTerminalState());
	}

	@Override
	public double lastReward() {
		return reward;
	}

	@Override
	public boolean isInTerminalState() {
		return terminal;
	}

	@Override
	public void resetEnvironment() {
		this.sim = new Simulation(this.phys);
		this.sim.addPlayer(this.phys.worldWidth / 2, this.phys.worldHeight / 2);
		terminal = false;
		reward = 0;
	}

}
