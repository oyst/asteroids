package uk.co.alexoyston.asteroids.simple_rl;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.CLASS_AGENT;
import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.CLASS_ASTEROID;

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
//		visualizer.addRenderLayer(getEnvRenderLayer(env));
		return visualizer;
	}
		
	public static RenderLayer getEnvRenderLayer(Environment env) {
		return new AsteroidsEnvRenderLayer(env);
	}
	
	public static StateRenderLayer getStateRenderLayer() {
		StateRenderLayer layer = new StateRenderLayer();

		OOStatePainter ooStatePainter = new OOStatePainter();
		layer.addStatePainter(ooStatePainter);

		ooStatePainter.addObjectClassPainter(CLASS_AGENT, new AgentPainter());
		ooStatePainter.addObjectClassPainter(CLASS_ASTEROID, new EnemyPainter());
		ooStatePainter.addObjectClassPainter(CLASS_SAUCER, new EnemyPainter());
		ooStatePainter.addObjectClassPainter(CLASS_BULLET, new ThreatPainter());
		
		return layer;
	}
	
	public static class VisualEntity {
		public float[] vertices;
		public Color color;
		
		public VisualEntity(float[] vertices, Color color) {
			this.vertices = vertices;
			this.color = color;
		}
	}
	
	public static class AsteroidsEnvRenderLayer implements RenderLayer {
		
		private AsteroidsEnvironment env;
		
		public AsteroidsEnvRenderLayer(Environment env) {
			if (env instanceof AsteroidsEnvironment) {
				this.env = (AsteroidsEnvironment)env;
			} 
			else {
				throw new ValueConversionException("Passed environment must extend AsteroidsEnvironment");
			}
		}
		
		
		@Override
		public void render(Graphics2D g2, float width, float height) {
			g2.setColor(Color.GREEN);
			g2.fillRect(0, 0, 5, 5);
			
			for (VisualEntity entity : env.getVisualEntities()) {
				
				Path2D.Float poly = new Path2D.Float();
				poly.moveTo(entity.vertices[0], entity.vertices[1]);
				for (int i = 2; i < entity.vertices.length; i += 2) {
					poly.lineTo(entity.vertices[i], entity.vertices[i+1]);
				}
				poly.closePath();
				
				g2.draw(poly);
			}
			
		}
	}
	
	public static class AgentPainter implements ObjectPainter {
		
		@Override
		public void paintObject(Graphics2D g2, OOState s, ObjectInstance ob, float cWidth, float cHeight) {
			g2.setColor(Color.RED);
			g2.fillRect(5, 0, 5, 5);

			float x = (float) ob.get(VAR_X);
			float y = (float) ob.get(VAR_Y);
			float width = (float) ob.get(VAR_WIDTH);
			float height = (float) ob.get(VAR_HEIGHT);
						
			g2.setColor(Color.RED);

			Path2D.Float poly = new Path2D.Float();
			poly.moveTo(x, y);
			poly.lineTo(x + width, y);
			poly.lineTo(x + width, y + height);
			poly.lineTo(x, y + height);
			poly.closePath();
			
			g2.draw(poly);
		}
	}
	
	public static class EnemyPainter implements ObjectPainter {
		
		@Override
		public void paintObject(Graphics2D g2, OOState s, ObjectInstance ob, float cWidth, float cHeight) {
			g2.setColor(Color.CYAN);
			g2.fillRect(10, 0, 5, 5);

			float x = (float) ob.get(VAR_X);
			float y = (float) ob.get(VAR_Y);
			float width = (float) ob.get(VAR_WIDTH);
			float height = (float) ob.get(VAR_HEIGHT);
						
			g2.setColor(Color.CYAN);

			Path2D.Float poly = new Path2D.Float();
			poly.moveTo(x, y);
			poly.lineTo(x + width, y);
			poly.lineTo(x + width, y + height);
			poly.lineTo(x, y + height);
			poly.closePath();
			
			g2.draw(poly);
		}
	}
	
	public static class ThreatPainter implements ObjectPainter {
		
		@Override
		public void paintObject(Graphics2D g2, OOState s, ObjectInstance ob, float cWidth, float cHeight) {
			g2.setColor(Color.MAGENTA);
			g2.fillRect(10, 0, 5, 5);

			float x = (float) ob.get(VAR_X);
			float y = (float) ob.get(VAR_Y);

			Path2D.Float poly = new Path2D.Float();
			poly.moveTo(x, y);
			poly.lineTo(x + 2, y);
			poly.lineTo(x + 2, y + 2);
			poly.lineTo(x, y + 2);
			poly.closePath();
			
			g2.fill(poly);
		}
	}
}
