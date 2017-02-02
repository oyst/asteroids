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

	public static Visualizer getVisualizer(Environment env){
		Visualizer visualizer = getVisualizer();
		visualizer.addRenderLayer(getEnvRenderLayer(env));
		return visualizer;
	}

	public static RenderLayer getEnvRenderLayer(Environment env) {
		return new AsteroidsEnvRenderLayer(env);
	}

	public static StateRenderLayer getStateRenderLayer() {
		StateRenderLayer layer = new StateRenderLayer();

		OOStatePainter ooStatePainter = new OOStatePainter();
		layer.addStatePainter(ooStatePainter);

		ooStatePainter.addObjectClassPainter(CLASS_AGENT, new EntityPainter());
		ooStatePainter.addObjectClassPainter(CLASS_ASTEROID, new EntityPainter());
		ooStatePainter.addObjectClassPainter(CLASS_SAUCER, new EntityPainter());
		ooStatePainter.addObjectClassPainter(CLASS_BULLET, new EntityPainter());

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

}
