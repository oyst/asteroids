package uk.co.alexoyston.asteroids;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.Disposable;

import uk.co.alexoyston.asteroids.simulation.Entity;
import uk.co.alexoyston.asteroids.simulation.Simulation;

/**
 * The renderer receives a simulation and renders it.
 */
public class Renderer implements Disposable {
	ShapeRenderer shapeRenderer;

	public Renderer(Simulation simulation) {
		shapeRenderer = new ShapeRenderer();
	}

	public void clear() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	public void renderEntities(Simulation simulation, float delta) {
		shapeRenderer.begin(ShapeType.Line);

		for (Entity entity : simulation.entities) {
			shapeRenderer.setColor(entity.color);
			shapeRenderer.polygon(entity.getVertices());
		}

		shapeRenderer.end();
	}

	public void renderDebug(Simulation simulation, float delta) {
		shapeRenderer.begin(ShapeType.Line);

		for (Entity entity : simulation.entities) {
			// Debug
			float minX = entity.bounds.x;
			float minY = entity.bounds.y;
			float x = entity.location.x;
			float y = entity.location.y;
			float cx = x + entity.center.x;
			float cy = y + entity.center.y;
			float width = entity.bounds.width;
			float height = entity.bounds.height;
			float vx = entity.velocity.x;
			float vy = entity.velocity.y;

			shapeRenderer.setColor(Color.VIOLET);
			float[][] triangles = entity.getTriangles();
			for (int i = 0; i < triangles.length; i++) {
				float[] triangle = triangles[i];
				shapeRenderer.triangle(
						triangles[i][0], triangle[1],
						triangles[i][2], triangle[3],
						triangles[i][4], triangle[5]);
			}

			// Debug direction
			shapeRenderer.setColor(Color.CYAN);
			shapeRenderer.arc(cx, cy, 10, 90, -(float)Math.toDegrees(entity.rotation), 10);

			// Debug bound
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.rect(minX, minY, width, height);

			// Debug center
			shapeRenderer.setColor(Color.GREEN);
			shapeRenderer.x(cx, cy, 2);

			// Debug location
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.x(x, y, 2);

			// Debug velocity
			shapeRenderer.setColor(Color.ORANGE);
			shapeRenderer.line(cx, cy, cx + vx, cy + vy);
		}

		shapeRenderer.setColor(Color.CORAL);
		shapeRenderer.rect(simulation.bounds.x, simulation.bounds.y, simulation.bounds.width, simulation.bounds.height);

		shapeRenderer.end();
	}

	@Override
	public void dispose() {
		shapeRenderer.dispose();
	}
}