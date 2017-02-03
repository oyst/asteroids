package uk.co.alexoyston.asteroids.simple_rl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.singleagent.environment.Environment;
import burlap.visualizer.OOStatePainter;
import burlap.visualizer.ObjectPainter;
import burlap.visualizer.RenderLayer;
import burlap.visualizer.StateRenderLayer;
import burlap.visualizer.Visualizer;

import uk.co.alexoyston.asteroids.simple_rl.state.EntityState;
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
		layer.addStatePainter(ooStatePainter);

		ooStatePainter.addObjectClassPainter(CLASS_AGENT, new EntityPainter());
		ooStatePainter.addObjectClassPainter(CLASS_ASTEROID, new EntityPainter());
		ooStatePainter.addObjectClassPainter(CLASS_SAUCER, new EntityPainter());
		ooStatePainter.addObjectClassPainter(CLASS_BULLET, new EntityPainter());

		ooStatePainter.addObjectClassPainter(CLASS_ASTEROID, new PolarPainter());
		ooStatePainter.addObjectClassPainter(CLASS_BULLET, new PolarPainter());

		return layer;
	}

	public static class EntityPainter implements ObjectPainter {

		@Override
		public void paintObject(Graphics2D g2, OOState s, ObjectInstance ob, float cWidth, float cHeight) {
			g2.setColor(Color.BLACK);

			EntityState entity = (EntityState)ob;
			float x = entity.x;
			float y = entity.y;
			float width = entity.width;
			float height = entity.height;

			Path2D.Float poly = new Path2D.Float();
			poly.moveTo(x, y);
			poly.lineTo(x + width, y);
			poly.lineTo(x + width, y + height);
			poly.lineTo(x, y + height);
			poly.closePath();

			g2.draw(poly);

		}
	}


	public static class PolarPainter implements ObjectPainter {

		@Override
		public void paintObject(Graphics2D g2, OOState s, ObjectInstance ob, float cWidth, float cHeight) {
			g2.setColor(Color.RED);

			PolarState entity = (PolarState)ob;
			float x = entity.x;
			float y = entity.y;
			float dist = entity.dist;
			float angle = entity.angle;
			float vx = entity.rel_vx;
			float vy = entity.rel_vy;

			// Path2D.Float poly = new Path2D.Float();
			// poly.moveTo(x, y);
			// poly.lineTo(x + Math.asin(angle), y + Math.acos(angle));
			// poly.closePath();
			g2.drawLine((int)x, (int)y, (int)x - (int)(dist*Math.sin(angle)), (int)y - (int)(dist*Math.cos(angle)));

			// g2.draw(poly);
		}
	}

}
