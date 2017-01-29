package uk.co.alexoyston.asteroids.simple_rl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import burlap.behavior.functionapproximation.DifferentiableStateActionValue;
import burlap.behavior.functionapproximation.dense.ConcatenatedObjectFeatures;
import burlap.behavior.functionapproximation.dense.NumericVariableFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TileCodingFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TilingArrangement;
import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.learning.tdmethods.vfa.GradientDescentSarsaLam;
import burlap.behavior.singleagent.planning.stochastic.sparsesampling.SparseSampling;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.core.oo.OODomain;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;
import uk.co.alexoyston.asteroids.simple_rl.props.AgentKilled;
import uk.co.alexoyston.asteroids.simple_rl.props.AgentShotObject;
import uk.co.alexoyston.asteroids.simple_rl.state.AgentState;
import uk.co.alexoyston.asteroids.simple_rl.state.EnemyState;
import uk.co.alexoyston.asteroids.simulation.PhysicsParams;

public class AsteroidsDomain implements DomainGenerator {

	public static final String ACTION_FORWARD = "forward";
	public static final String ACTION_ROTATE_RIGHT = "rotateRight";
	public static final String ACTION_ROTATE_LEFT = "rotateLeft";
	public static final String ACTION_SHOOT = "shoot";
	public static final String ACTION_NONE = "none";

	public static final String CLASS_AGENT = "agent";
	public static final String CLASS_ASTEROID = "asteroid";
	public static final String CLASS_SAUCER = "saucer";
	public static final String CLASS_BULLET = "bullet";

	public static final String VAR_X = "x";
	public static final String VAR_Y = "y";
	public static final String VAR_WIDTH = "width";
	public static final String VAR_HEIGHT = "height";
	public static final String VAR_VELOCITY_X = "velocityX";
	public static final String VAR_VELOCITY_Y = "velocityY";
	public static final String VAR_ROTATION = "rotation";

	public static final String PF_AGENT_KILLED = "agentKilled";
	public static final String PF_SHOT_ASTEROID = "shotAsteroid";
	public static final String PF_SHOT_BULLET = "shotBullet";
	public static final String PF_SHOT_SAUCER = "shotSaucer";
	public static final String PF_SHOT_AGENT = "shotAgent";

	private PhysicsParams phys;
	private AsteroidsModel model;
	private RewardFunction reward;
	private TerminalFunction terminal;

	@Override
	public OOSADomain generateDomain() {

		OOSADomain domain = new OOSADomain();

		domain.addStateClass(CLASS_AGENT, AgentState.class);
		domain.addStateClass(CLASS_ASTEROID, EnemyState.Asteroid.class);
		domain.addStateClass(CLASS_SAUCER, EnemyState.Saucer.class);
		domain.addStateClass(CLASS_BULLET, EnemyState.Bullet.class);

		domain.addActionTypes(
				new UniversalActionType(ACTION_FORWARD),
				new UniversalActionType(ACTION_ROTATE_RIGHT),
				new UniversalActionType(ACTION_ROTATE_LEFT),
				new UniversalActionType(ACTION_SHOOT),
				new UniversalActionType(ACTION_NONE));

		OODomain.Helper.addPfsToDomain(domain, this.generatePfs());

		phys = new PhysicsParams();
		model = new AsteroidsModel(phys);
		reward = new AsteroidsReward(domain, phys);
		terminal = new AsteroidsTerminal(domain, phys);

		domain.setModel(new FactoredModel(model, reward, terminal));

		return domain;
	}


	public List<PropositionalFunction> generatePfs(){
		return Arrays.asList(
				new AgentShotObject.Bullet(PF_SHOT_BULLET),
				new AgentShotObject.Agent(PF_SHOT_AGENT),
				new AgentShotObject.Asteroid(PF_SHOT_ASTEROID),
				new AgentShotObject.Saucer(PF_SHOT_SAUCER),
				new AgentKilled(PF_AGENT_KILLED));
	}

	public static void main(String [] args){
		AsteroidsDomain asteroids = new AsteroidsDomain();
		OOSADomain domain = asteroids.generateDomain();
		Environment env = new AsteroidsEnvironment(asteroids.phys);

		Visualizer v = AsteroidsVisualizer.getVisualizer((AsteroidsEnvironment) env);

		explorer(domain, env, v);
		// SARSA(domain, env, v);
//		SS(domain, env, v);
	}

	public static void explorer(OOSADomain domain, Environment env, Visualizer v){
		VisualExplorer exp = new VisualExplorer(domain, env, v);

		exp.addKeyAction("w", ACTION_FORWARD, "");
		exp.addKeyAction("a", ACTION_ROTATE_RIGHT, "");
		exp.addKeyAction("d", ACTION_ROTATE_LEFT, "");
		exp.addKeyAction("e", ACTION_SHOOT, "");
		exp.addKeyAction("s", ACTION_NONE, "");

		exp.initGUI();
	}

	public static void SS(SADomain domain, Environment env, Visualizer v) {
		SparseSampling ss = new SparseSampling(domain, 1, new SimpleHashableStateFactory(), 10, 1);
		ss.setForgetPreviousPlanResults(true);
		ss.toggleDebugPrinting(true);
		Policy p = new GreedyQPolicy(ss);

		Episode e = PolicyUtils.rollout(p, env.currentObservation(), domain.getModel(), 1);
		System.out.println("Num steps: " + e.maxTimeStep());
		new EpisodeSequenceVisualizer(v, domain, Arrays.asList(e));
	}

	public static void SARSA(SADomain domain, Environment env, Visualizer v){
		ConcatenatedObjectFeatures inputFeatures = new ConcatenatedObjectFeatures()
				.addObjectVectorizion(CLASS_AGENT, new NumericVariableFeatures());

		int nTilings = 5;

		TileCodingFeatures tilecoding = new TileCodingFeatures(inputFeatures);
		tilecoding.addTilingsForDimensionsAndWidths(
				new boolean[] {true, true, false, false, true, true, true},
				new double[] {10, 10, 0, 0, 2, 2, 1.2}, 7, TilingArrangement.RANDOM_JITTER);

		double defaultQ = 0.5;
		DifferentiableStateActionValue vfa = tilecoding.generateVFA(defaultQ/nTilings);
		GradientDescentSarsaLam agent = new GradientDescentSarsaLam(domain, 0.99, vfa, 0.02, 0.5);

		List<Episode> episodes = new ArrayList<Episode>();
		for(int i = 0; i < 1; i++){
			Episode ea = agent.runLearningEpisode(env);
			episodes.add(ea);
			System.out.println(i + ": " + ea.maxTimeStep());
			env.resetEnvironment();
		}
		new EpisodeSequenceVisualizer(v, domain, episodes);

	}

}
