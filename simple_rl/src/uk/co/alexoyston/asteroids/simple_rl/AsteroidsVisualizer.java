package uk.co.alexoyston.asteroids.simple_rl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.state.State;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.singleagent.environment.Environment;
import burlap.visualizer.OOStatePainter;
import burlap.visualizer.StatePainter;
import burlap.visualizer.RenderLayer;
import burlap.visualizer.StateRenderLayer;
import burlap.visualizer.Visualizer;

import uk.co.alexoyston.asteroids.simple_rl.state.AgentState;
import uk.co.alexoyston.asteroids.simple_rl.state.AsteroidsState;
import uk.co.alexoyston.asteroids.simple_rl.state.PolarState;

import joptsimple.ValueConversionException;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

public class AsteroidsVisualizer {
		private AsteroidsVisualizer() {
		}

		public static Visualizer getVisualizer() {
			Visualizer visualizer = new Visualizer();
			visualizer.setSetRenderLayer(getStateRenderLayer());
			return visualizer;
		}

	public static StateRenderLayer getStateRenderLayer() {
		StateRenderLayer layer = new StateRenderLayer();

		OOStatePainter ooStatePainter = new OOStatePainter();
		layer.addStatePainter(new PolarPainter());

		return layer;
	}

	public static class PolarPainter implements StatePainter {

		@Override
		public void paint(Graphics2D g2, State s, float cWidth, float cHeight) {
			g2.setColor(Color.BLACK);

			AsteroidsState state = (AsteroidsState)s;
			AgentState agent = (AgentState)state.object(CLASS_AGENT);
			int rad = agent.radius;
			float rot = agent.rotation;
			int cx = 250;
			int cy = 250;
			int x = cx-rad;
			int y = cy-rad;

			g2.fillRect(0,0, 5,5);
			g2.drawOval(x, y, rad*2, rad*2);
			g2.drawRect(0, 0, 500, 500);
			g2.setColor(Color.RED);
			g2.drawLine(cx, cy, cx, cy - 30);
			g2.setColor(Color.BLACK);

			for (ObjectInstance o : state.objectsOfClass(CLASS_OBJECT)) {
				PolarState obj = (PolarState)o;
				float dist = obj.dist;
				float angle = obj.angle;
				int obj_rad = obj.radius;
				int obj_x = (int)(dist * Math.cos(angle + rot)) + cx + rad;
				int obj_y = (int)(dist * Math.sin(angle + rot)) + cy + rad;
				int obj_cx = obj_x + obj_rad;
				int obj_cy = obj_y + obj_rad;
				g2.drawLine(cx, cy, obj_cx, obj_cy);
				g2.drawOval(obj_x, obj_y, obj_rad*2, obj_rad*2);

			}
		}
	}

}
