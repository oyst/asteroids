package uk.co.alexoyston.asteroids.simple_rl;

import java.awt.Color;
import java.util.List;
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

	protected int shootReward = -5;
	protected int collisionReward = -1000;

	public AsteroidsEnvironment(PhysicsParams phys) {
		this.phys = phys;
		resetEnvironment();
	}

	@Override
	public void resetEnvironment() {
		sim = new Simulation(phys);
		sim.addPlayer(phys.worldWidth / 2, phys.worldHeight / 2);
	}

	@Override
	public State currentObservation() {
		if (sim.players.size() == 0)
			return new AsteroidsState(new AgentState());

		ArrayList<PolarState> asteroids = new ArrayList<PolarState>();
		ArrayList<PolarState> saucers = new ArrayList<PolarState>();
		ArrayList<PolarState> bullets = new ArrayList<PolarState>();
		Player player = sim.players.get(0);

		final float playerX = (player.location.x + player.center.x);
		final float playerY = (player.location.y + player.center.y);
		int diameter = (int)Math.max(player.bounds.width, player.bounds.height);

		AgentState agent = new AgentState(diameter, player.activeShots, player.rotation);

		for (Entity entity : this.sim.entities) {
			if (entity instanceof Player)
				continue;

			float objX = (entity.location.x + entity.center.x);
			float objY = (entity.location.y + entity.center.y);

			// Calculate the distance between the agent and object
			float dX = (objX - playerX);
			float dY = (objY - playerY);

			// Calculate the shortest distance using wrap around if necessary
			if (Math.abs(dX) > phys.worldWidth / 2)
				dX -= phys.worldWidth * Math.signum(dX);
			if (Math.abs(dY) > phys.worldHeight / 2)
				dY -= phys.worldHeight * Math.signum(dY);

			float angle = (float)Math.atan2(dY, dX) + player.rotation - (float)(Math.PI/2);
			if (angle < -Math.PI) angle += (float)(2*Math.PI);
			if (angle > Math.PI) angle -= (float)(2*Math.PI);
			float dist = (float)Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));

			// Get the objects max diameter
			diameter = (int)Math.max(entity.bounds.width, entity.bounds.height);

			// Relative velocity of the object to the agent
			float vX = (entity.velocity.x - player.velocity.x);
			float vY = (entity.velocity.y - player.velocity.y);

			float vAngle = (float)Math.atan2(vY, vX) - (float)Math.atan2(-dY, -dX);
			if (vAngle < -Math.PI) vAngle += (float)(2*Math.PI);
			if (vAngle > Math.PI) vAngle -= (float)(2*Math.PI);
			float vDist = (float)Math.sqrt(Math.pow(vX, 2) + Math.pow(vY, 2));

			String name;
			List<PolarState> container;

			if (entity instanceof Asteroid) {
				name = "asteroid";
				container = asteroids;
			}
			else if (entity instanceof Saucer || entity instanceof SmallSaucer) {
				name = "saucer";
				container = saucers;
			}
			else if (entity instanceof Bullet) {
				name = "bullet";
				container = bullets;
			}
			else {
				throw new RuntimeException("Unknown Entity instance encountered: " + entity.toString());
			}

			PolarState obj = new PolarState(name, diameter, dist, angle, vDist, vAngle);
			container.add(obj);
		}

		State state = new AsteroidsState(agent, asteroids, bullets, saucers);
		return state;
	}

	@Override
	public EnvironmentOutcome executeAction(Action a) {
		Player player;
		State oldState, newState;
		int oldScore, newScore;
		boolean shotTaken = false;

		player = this.sim.players.get(0);

		oldState = currentObservation();
		oldScore = player.getScore();

		if (a.actionName().equals(ACTION_FORWARD))
			this.sim.playerFwd(0);
		else if (a.actionName().equals(ACTION_ROTATE_RIGHT))
			this.sim.playerRotRight(0);
		else if (a.actionName().equals(ACTION_ROTATE_LEFT))
			this.sim.playerRotLeft(0);
		else if (a.actionName().equals(ACTION_SHOOT)) {
			this.sim.playerShoot(0);
			shotTaken = true;
		}

		this.sim.update(phys.updateDelta);

		newState = currentObservation();
		newScore = player.getScore();

		lastReward = (newScore - oldScore);

		if (shotTaken)
			lastReward += shootReward;
		if (isInTerminalState())
			lastReward += collisionReward;

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
