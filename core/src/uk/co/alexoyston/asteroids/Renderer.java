package uk.co.alexoyston.asteroids;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
			shapeRenderer.setColor(entity.getColor());
			shapeRenderer.polygon(entity.getPolyVertices());
		}

		shapeRenderer.end();
	}

	public void renderDebug(Simulation simulation, float delta) {
		shapeRenderer.begin(ShapeType.Line);

		for (Entity entity : simulation.entities) {
			// Debug
			float sinRotation = (float) Math.sin(entity.getRotation());
			float cosRotation = (float) Math.cos(entity.getRotation());
			float cx = entity.getCenterX();
			float cy = entity.getCenterY();
			float minX = entity.getMinX();
			float minY = entity.getMinY();
			float maxX = entity.getMaxX();
			float maxY = entity.getMaxY();
			float x = entity.getX();
			float y = entity.getY();
			float width = maxX - minX;
			float height = maxY - minY;
			float vx = entity.getVelocity().x;
			float vy = entity.getVelocity().y;

			// Debug direction
			shapeRenderer.setColor(Color.CYAN);
			shapeRenderer.line(cx, cy, cx, cy - 10);
			shapeRenderer.line(cx, cy, cx + sinRotation * 10, cy - cosRotation * 10);
			// shapeRenderer.arc(cx - 10, cy - 10, 10, 270, (float)
			// Math.toDegrees(entity.getRotation())- 90);

			// Debug bound
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.rect(minX, minY, width, height);

			// Debug center
			shapeRenderer.setColor(Color.GREEN);
			shapeRenderer.line(cx - 2, cy, cx + 2, cy);
			shapeRenderer.line(cx, cy - 2, cx, cy + 2);

			// Debug location
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.line(x - 2, y, x + 2, y);
			shapeRenderer.line(x, y - 2, x, y + 2);

			// Debug velocity
			shapeRenderer.setColor(Color.ORANGE);
			shapeRenderer.line(cx, cy, cx + vx * 10, cy + vy * 10);
		}

		shapeRenderer.end();
	}

	@Override
	public void dispose() {
		shapeRenderer.dispose();
	}
}