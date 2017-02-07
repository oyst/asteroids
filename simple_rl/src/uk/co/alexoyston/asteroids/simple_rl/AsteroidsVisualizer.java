package uk.co.alexoyston.asteroids.simple_rl;

import java.awt.Color;
import java.awt.Graphics2D;

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

	public static Visualizer getVisualizer(int offsetX, int offsetY, int width, int height) {
		Visualizer visualizer = new Visualizer();
		visualizer.setSetRenderLayer(getStateRenderLayer(offsetX, offsetY, width, height));
		return visualizer;
	}

	public static Visualizer getVisualizer(int width, int height) {
		return getVisualizer(0, 0, width, height);
	}

	public static StateRenderLayer getStateRenderLayer(int offsetX, int offsetY, int width, int height) {
		StateRenderLayer layer = new StateRenderLayer();

		OOStatePainter ooStatePainter = new OOStatePainter();
		layer.addStatePainter(new PolarPainter(offsetX, offsetY, width, height));

		return layer;
	}

	public static class PolarPainter implements StatePainter {
		int offsetX;
		int offsetY;
		int width;
		int height;

		public PolarPainter(int offsetX, int offsetY, int width, int height) {
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.width = width;
			this.height = height;
		}

		public void paintObject(Graphics2D g2, int cx, int cy, int diameter) {
			int x = cx - (diameter/2);
			int y = cy - (diameter/2);

			g2.setColor(Color.RED);
			g2.drawLine(cx-3, cy-3, cx+3, cy+3);
			g2.drawLine(cx-3, cy+3, cx+3, cy-3);

			g2.setColor(Color.MAGENTA);
			g2.drawLine(x-3, y-3, x+3, y+3);
			g2.drawLine(x-3, y+3, x+3, y-3);

			g2.setColor(Color.BLUE);
			g2.drawOval(x, y, diameter, diameter);

			g2.setColor(Color.BLACK);
		}

		@Override
		public void paint(Graphics2D g2, State s, float cWidth, float cHeight) {
			AsteroidsState state = (AsteroidsState)s;
			AgentState agent = (AgentState)state.object(CLASS_AGENT);

			g2.setColor(Color.BLACK);
			g2.drawRect(offsetX, offsetY, width, height);

			int x = offsetX + width/2;
			int y = offsetY + height/2;
			paintObject(g2, x, y, agent.diameter);

			for (ObjectInstance o : state.objectsOfClass(CLASS_OBJECT)) {
				PolarState obj = (PolarState)o;

				if (obj.diameter == 0) continue;

				float dist = obj.dist;
				float angle = obj.angle + (float)Math.PI/2;
				int obj_x = (int)(dist * Math.cos(angle)) + x;
				int obj_y = (int)(dist * Math.sin(angle)) + y;
				paintObject(g2, obj_x, obj_y, obj.diameter);

				g2.drawLine(x, y, obj_x, obj_y);
			}
		}
	}

}
