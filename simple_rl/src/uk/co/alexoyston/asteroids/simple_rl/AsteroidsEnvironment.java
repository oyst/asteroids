package uk.co.alexoyston.asteroids.simple_rl;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
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
	protected int warpReward = -50;

	protected int closeAsteroidReward = -50;

	private Map<String, Integer> asteroidStateRewards = new HashMap<String, Integer>();

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

		final float playerX = (player.bounds.x + player.center.x);
		final float playerY = (player.bounds.y + player.center.y);
		int diameter = (int)Math.max(player.bounds.width, player.bounds.height);

		AgentState agent = new AgentState(diameter, player.remainingShots(), player.remainingWarps(), player.rotation);

		for (Entity entity : this.sim.entities) {
			if (entity instanceof Player)
				continue;
			if (entity instanceof Bullet && ((Bullet)entity).owner == player)
				continue;

			float objX = (entity.bounds.x + entity.center.x);
			float objY = (entity.bounds.y + entity.center.y);

			// Calculate the distance between the agent and object
			float dX = (objX - playerX);
			float dY = (objY - playerY);

			// Calculate the shortest distance using wrap around if necessary
			if (Math.abs(dX) > (phys.worldWidth + player.bounds.width) / 2)
				dX -= (phys.worldWidth + player.bounds.width) * Math.signum(dX);
			if (Math.abs(dY) > (phys.worldHeight + player.bounds.height) / 2)
				dY -= (phys.worldHeight + player.bounds.height) * Math.signum(dY);

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
			int type;

			if (entity instanceof Asteroid) {
				name = "asteroid";
				container = asteroids;
				type = TYPE_ASTEROID(((Asteroid)entity).size());
			}
			else if (entity instanceof Saucer) {
				name = "saucer";
				container = saucers;
				if (entity instanceof SmallSaucer)
					type = TYPE_SMALL_SAUCER;
				else
					type = TYPE_SAUCER;
			}
			else if (entity instanceof Bullet) {
				name = "bullet";
				container = bullets;
				type = TYPE_BULLET;
			}
			else {
				throw new RuntimeException("Unknown Entity instance encountered: " + entity.toString());
			}

			name = String.format("%s_%d", name, System.nanoTime());

			PolarState obj = new PolarState(name, diameter, dist, angle, vDist, vAngle, type);
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

		player = this.sim.players.get(0);

		lastReward = 0;

		oldState = currentObservation();
		oldScore = player.getScore();

		if (a.actionName().equals(ACTION_FORWARD))
			this.sim.playerFwd(0);
		else if (a.actionName().equals(ACTION_ROTATE_RIGHT))
			this.sim.playerRotRight(0);
		else if (a.actionName().equals(ACTION_ROTATE_LEFT))
			this.sim.playerRotLeft(0);
		else if (a.actionName().equals(ACTION_WARP)) {
			this.sim.playerWarp(0);
			lastReward += warpReward;
		}
		else if (a.actionName().equals(ACTION_SHOOT)) {
			this.sim.playerShoot(0);
			lastReward += shootReward;
		}

		this.sim.update(phys.updateDelta);

		newState = currentObservation();
		newScore = player.getScore();

		lastReward += (newScore - oldScore);

		if (isInTerminalState())
			lastReward += collisionReward;

		// lastReward += domainKnowledgeRewards(newState);
		lastReward += nearbyAsteroidsRewardDynamic((OOState)oldState, (OOState)newState);

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

	protected int domainKnowledgeRewards(State state) {
		int reward = 0;

		reward += nearbyAsteroidsRewardFixed((OOState)state);
		// reward +=

		return reward;
	}

	private int calcNearbyAsteroidReward(float distance) {
		if (distance == 0)
			return 0;

		if (distance >= 150)
			return 0;

		return (int)((distance - 150) * 0.1f);
	}

	protected int nearbyAsteroidsRewardDynamic(OOState oldState, OOState newState) {
		List<ObjectInstance> oldAsteroidObjs = oldState.objectsOfClass(CLASS_ASTEROID);
		List<ObjectInstance> newAsteroidObjs = newState.objectsOfClass(CLASS_ASTEROID);

		int reward = 0;

		for (ObjectInstance newObj : newAsteroidObjs) {
			// Asteroid is joining state
			if (!asteroidStateRewards.containsKey(newObj.name())) {
				PolarState newAsteroid = (PolarState)newObj;
				int subReward = calcNearbyAsteroidReward(newAsteroid.dist);
				asteroidStateRewards.put(newObj.name(), subReward);
				reward += subReward;
			}

		}

		for (ObjectInstance oldObj : oldAsteroidObjs) {
			boolean objRemained = false;

			for (ObjectInstance newObj : newAsteroidObjs) {

				// Asteroid remained in state
				if (newObj.name().equals(oldObj.name())) {
					PolarState newAsteroid = (PolarState)newObj;
					PolarState oldAsteroid = (PolarState)oldObj;

					int subReward = calcNearbyAsteroidReward(newAsteroid.dist);
					reward -= asteroidStateRewards.get(newObj.name());
					asteroidStateRewards.put(newObj.name(), subReward);
					reward += subReward;

					objRemained = true;
					break;
				}
			}

			// Asteroid has left
			if (!objRemained) {
				String name = oldObj.name();
				if (asteroidStateRewards.containsKey(name))
					reward += asteroidStateRewards.get(name);
				asteroidStateRewards.remove(name);
			}

		}

		return reward;

	}

	protected int nearbyAsteroidsRewardFixed(OOState state) {
		float minGoodDist = 50;
		int maxRewardsGiven = 1;
		int reward = 0;

		List<ObjectInstance> asteroidObjs = state.objectsOfClass(CLASS_ASTEROID);

		for (ObjectInstance obj : asteroidObjs) {
			PolarState asteroid = (PolarState)obj;

			// == instead of <= so -1 can represent quick 'all' functionality
			if (maxRewardsGiven == 0)
				break;

			if (asteroid.dist <= minGoodDist)
				reward += closeAsteroidReward;
		}

		return reward;
	}

}
