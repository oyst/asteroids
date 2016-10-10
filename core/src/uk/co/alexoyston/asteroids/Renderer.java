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
			shapeRenderer.setColor(entity.color);
			shapeRenderer.polygon(entity.getVertices());
		}

		shapeRenderer.end();
	}

	public void renderDebug(Simulation simulation, float delta) {
		shapeRenderer.begin(ShapeType.Line);

		for (Entity entity : simulation.entities) {
			// Debug
			float sinRotation = (float) Math.sin(entity.rotation);
			float cosRotation = (float) Math.cos(entity.rotation);
			float cx = entity.center.x;
			float cy = entity.center.y;
			float minX = entity.bounds.x;
			float minY = entity.bounds.y;
			float x = entity.location.x;
			float y = entity.location.y;
			float width = entity.bounds.width;
			float height = entity.bounds.height;
			float vx = entity.velocity.x;
			float vy = entity.velocity.y;

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