package uk.co.alexoyston.asteroids.simple_rl;

import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;
import burlap.shell.visual.VisualExplorer;
import burlap.visualizer.StatePainter;
import burlap.visualizer.StateRenderLayer;
import burlap.visualizer.Visualizer;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class AsteroidsDomain implements DomainGenerator {

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

			//walls will be filled in black
			g2.setColor(Color.BLACK);

			//set up floats for the width and height of our domain
			float fWidth = AsteroidsDomain.this.map.length;
			float fHeight = AsteroidsDomain.this.map[0].length;

			//determine the width of a single cell
			//on our canvas such that the whole map can be painted
			float width = cWidth / fWidth;
			float height = cHeight / fHeight;

			//pass through each cell of our map and if it's a wall, paint a black rectangle on our
			//cavas of dimension widthxheight
			for(int i = 0; i < AsteroidsDomain.this.map.length; i++){
				for(int j = 0; j < AsteroidsDomain.this.map[0].length; j++){

					//is there a wall here?
					if(AsteroidsDomain.this.map[i][j] == 1){

						//left coordinate of cell on our canvas
						float rx = i*width;

						//top coordinate of cell on our canvas
						//coordinate system adjustment because the java canvas
						//origin is in the top left instead of the bottom right
						float ry = cHeight - height - j*height;

						//paint the rectangle
						g2.fill(new Rectangle2D.Float(rx, ry, width, height));

					}
				}
			}
		}
	}

	public static void main(String [] args){

		AsteroidsDomain gen = new AsteroidsDomain();
		SADomain domain = gen.generateDomain();
		State initialState = new AsteroidsState(0, 0);
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