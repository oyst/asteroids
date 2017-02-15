package uk.co.alexoyston.asteroids.simple_rl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import burlap.behavior.functionapproximation.dense.fourier.FourierBasis;
import burlap.behavior.functionapproximation.sparse.tilecoding.TileCodingFeatures;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.performance.PerformanceMetric;
import burlap.behavior.singleagent.auxiliary.performance.TrialMode;
import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.learning.tdmethods.vfa.GradientDescentSarsaLam;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.visualizer.Visualizer;
import burlap.mdp.core.state.vardomain.VariableDomain;

import uk.co.alexoyston.asteroids.simple_rl.actions.ShootActionType;
import uk.co.alexoyston.asteroids.simple_rl.actions.WarpActionType;
import uk.co.alexoyston.asteroids.simple_rl.algorithms.PolarFeaturesFactory;
import uk.co.alexoyston.asteroids.simple_rl.algorithms.VFAGenerator;
import uk.co.alexoyston.asteroids.simple_rl.state.AgentState;
import uk.co.alexoyston.asteroids.simple_rl.state.PolarState;

import uk.co.alexoyston.asteroids.simulation.PhysicsParams;

public class AsteroidsDomain implements DomainGenerator {

	public static final String ACTION_FORWARD = "forward";
	public static final String ACTION_ROTATE_RIGHT = "rotateRight";
	public static final String ACTION_ROTATE_LEFT = "rotateLeft";
	public static final String ACTION_SHOOT = "shoot";
	public static final String ACTION_WARP = "warp";
	public static final String ACTION_NONE = "none";

	public static final String ACTIONTYPE_SHOOT = "typeShoot";
	public static final String ACTIONTYPE_WARP = "typeWarp";

	public static final String CLASS_AGENT = "agent";
	public static final String CLASS_OBJECT = "object";

	public static final String VAR_DIST = "dist"; // Absolute distance from Agent to Object
	public static final String VAR_ANGLE = "angle"; // Angle between Agent and Object
	public static final String VAR_VELOCITY_X = "velocityX"; // Velocity of Object relative to Agent
	public static final String VAR_VELOCITY_Y = "velocityY";
	public static final String VAR_VELOCITY_DIST = "velocityDist";
	public static final String VAR_VELOCITY_ANGLE = "velocityAngle";
	public static final String VAR_PRESENT = "present";
	public static final String VAR_CAN_SHOOT = "canShoot";
	public static final String VAR_CAN_WARP = "canWarp";

	private static final PhysicsParams phys = new PhysicsParams();
	protected static final Map<Object, VariableDomain> varDomains = new HashMap<Object, VariableDomain>();

	static {
		double maxDist = Math.max(phys.worldWidth, phys.worldHeight) / 2;

		double maxPlayerVelocity = Math.sqrt(2 * phys.playerThrustPower / phys.playerDrag);
    double maxAsteroidVelocity = (maxPlayerVelocity + phys.asteroidMaxSpeed);
    double maxSaucerVelocity = (maxPlayerVelocity + phys.saucerSpeed);
    double maxBulletVelocity = (maxPlayerVelocity + phys.playerShotSpeed);
    double maxVelocity = Math.max(maxAsteroidVelocity, Math.max(maxSaucerVelocity, maxBulletVelocity));

		int maxActiveShots = phys.playerMaxActiveShots;

		varDomains.put(VAR_DIST, new VariableDomain(-maxDist, maxDist));
		varDomains.put(VAR_ANGLE, new VariableDomain(-Math.PI, Math.PI));
		varDomains.put(VAR_VELOCITY_X, new VariableDomain(-maxVelocity, maxVelocity));
		varDomains.put(VAR_VELOCITY_Y, new VariableDomain(-maxVelocity, maxVelocity));
		varDomains.put(VAR_VELOCITY_DIST, new VariableDomain(-maxVelocity, maxVelocity));
		varDomains.put(VAR_VELOCITY_ANGLE, new VariableDomain(-Math.PI, Math.PI));
		varDomains.put(VAR_PRESENT, new VariableDomain(0, 1));
		varDomains.put(VAR_CAN_SHOOT, new VariableDomain(0, 1));
		varDomains.put(VAR_CAN_WARP, new VariableDomain(0, 1));
	}

	@Override
	public OOSADomain generateDomain() {

		OOSADomain domain = new OOSADomain();

		domain.addStateClass(CLASS_AGENT, AgentState.class);
		domain.addStateClass(CLASS_OBJECT, PolarState.class);

		domain.addActionTypes(
				new UniversalActionType(ACTION_FORWARD),
				new UniversalActionType(ACTION_ROTATE_RIGHT),
				new UniversalActionType(ACTION_ROTATE_LEFT),
				new ShootActionType(ACTIONTYPE_SHOOT, ACTION_SHOOT),
				new WarpActionType(ACTIONTYPE_WARP, ACTION_WARP),
				new UniversalActionType(ACTION_NONE));

		domain.setModel(null);

		return domain;
	}

	public static void main(String [] args){
		AsteroidsDomain asteroids = new AsteroidsDomain();
		OOSADomain domain = asteroids.generateDomain();
		Environment env = new AsteroidsEnvironment(phys);

		Visualizer v = AsteroidsVisualizer.getVisualizer(50, 50, phys.worldWidth, phys.worldHeight);

		PolarFeaturesFactory featuresFactory = new PolarFeaturesFactory(varDomains);

		TileCodingFeatures tileCodedFeatures = featuresFactory.getTileCodedFeatures(20, 3);
		VFAGenerator tileCodedVFA = (dq) -> {return tileCodedFeatures.generateVFA(dq);};

		// FourierBasis fourierBasis = featuresFactory.getFourierBasis(2, 2);
		// VFAGenerator fourierBasisVFA = (dq) -> {return fourierBasis.generateVFA(dq);};

		LearningAgentFactory[] factories = new LearningAgentFactory[1];
		factories[0] = getSarsaAgentFactory(domain, null, tileCodedVFA, 0.999, 0.02, 0.5/3, 0.5);

		// explorer(domain, env, v);
		// episodicView(domain, env, v, factories[0], 50);
		expAndPlot(env, 5, 2000, factories);
	}

	public static LearningAgentFactory getSarsaAgentFactory(OOSADomain domain, String tag, VFAGenerator vfaGen,
		double gamma, double learningRate, double defaultQ, double lambda) {

		String tagStr = (tag == null) ? "" : tag + ", ";
		LearningAgentFactory agentFactory = new LearningAgentFactory() {
			public String getAgentName() {
				return String.format("GD-SARSA: (%s%f, %f, %f, %f)", tagStr, gamma, learningRate, defaultQ, lambda);
			}
			public LearningAgent generateAgent() {
				return new GradientDescentSarsaLam(domain, gamma, vfaGen.generateVFA(defaultQ), learningRate, lambda);
			}
		};
		return agentFactory;
	}

	public static void explorer(OOSADomain domain, Environment env, Visualizer v){
		VisualExplorer exp = new VisualExplorer(domain, env, v);

		exp.addKeyAction("w", ACTION_FORWARD, "");
		exp.addKeyAction("a", ACTION_ROTATE_RIGHT, "");
		exp.addKeyAction("d", ACTION_ROTATE_LEFT, "");
		exp.addKeyAction("e", ACTIONTYPE_SHOOT, ACTION_SHOOT);
		exp.addKeyAction("s", ACTION_NONE, "");

		exp.initGUI();
	}

	public static void episodicView(OOSADomain domain, Environment env, Visualizer v,
		LearningAgentFactory agentFactory, int numEpisodes) {

			LearningAgent agent = agentFactory.generateAgent();
			List<Episode> episodes = new ArrayList<Episode>();
			for(int i = 0; i < numEpisodes; i++){
				Episode ep = agent.runLearningEpisode(env);
				episodes.add(ep);
				System.out.println(i + ": " + ep.maxTimeStep());
				env.resetEnvironment();
			}
			new EpisodeSequenceVisualizer(v, domain, episodes);
	}

	public static void expAndPlot(Environment env, int numTrials, int trialLength, LearningAgentFactory... agentFactories){
		LearningAlgorithmExperimenter exp = new LearningAlgorithmExperimenter(
			env, numTrials, trialLength, agentFactories
		);

		exp.setUpPlottingConfiguration(500, 250, 2, 1000,
				TrialMode.MOST_RECENT_AND_AVERAGE,
				PerformanceMetric.STEPS_PER_EPISODE,
				PerformanceMetric.CUMULATIVE_REWARD_PER_EPISODE);

		exp.startExperiment();
	}

}
