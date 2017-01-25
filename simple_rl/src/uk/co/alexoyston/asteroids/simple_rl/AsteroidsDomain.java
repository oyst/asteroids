package uk.co.alexoyston.asteroids.simple_rl;

import java.awt.Graphics2D;

import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.shell.visual.VisualExplorer;
import burlap.visualizer.StatePainter;
import burlap.visualizer.StateRenderLayer;
import burlap.visualizer.Visualizer;

class AsteroidsDomain implements DomainGenerator {

	public static final String ACTION_FWD = "fwd";
	public static final String ACTION_ROT_RIGHT = "rot_right";
	public static final String ACTION_ROT_LEFT = "rot_left";
	public static final String ACTION_SHOOT = "shoot";
	public static final String ACTION_NO_OP = "no_op";

	@Override
	public SADomain generateDomain() {

		SADomain domain = new SADomain();

		domain.addActionTypes(
				new UniversalActionType(ACTION_FWD),
				new UniversalActionType(ACTION_ROT_RIGHT),
				new UniversalActionType(ACTION_ROT_LEFT),
				new UniversalActionType(ACTION_SHOOT),
				new UniversalActionType(ACTION_NO_OP));

		AsteroidsStateModel stateModel = new AsteroidsStateModel();
		RewardFunction rewardFunc = new AsteroidsReward();
		TerminalFunction terminalFunc = new AsteroidsTerminal();

		domain.setModel(new FactoredModel(stateModel, rewardFunc, terminalFunc));

		return domain;
	}


	public StateRenderLayer getStateRenderLayer(){
		StateRenderLayer rl = new StateRenderLayer();
		rl.addStatePainter(new AsteroidsDomain.WallPainter());

		return rl;
	}

	public Visualizer getVisualizer(){
		return new Visualizer(this.getStateRenderLayer());
	}

	public class WallPainter implements StatePainter {
		public void paint(Graphics2D g2, State s, float cWidth, float cHeight) {
		}
	}

	public static void main(String [] args){

		AsteroidsDomain gen = new AsteroidsDomain();
		SADomain domain = gen.generateDomain();
		Environment env = new AsteroidsEnvironment();

		Visualizer v = gen.getVisualizer();
		VisualExplorer exp = new VisualExplorer(domain, env, v);

		exp.addKeyAction("w", ACTION_FWD, "");
		exp.addKeyAction("s", ACTION_ROT_RIGHT, "");
		exp.addKeyAction("d", ACTION_ROT_LEFT, "");
		exp.addKeyAction("a", ACTION_SHOOT, "");

		exp.initGUI();
	}

}