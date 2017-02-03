package uk.co.alexoyston.asteroids.simple_rl.algorithms;

import java.util.List;
import java.util.ArrayList;

import burlap.behavior.functionapproximation.dense.ConcatenatedObjectFeatures;
import burlap.behavior.functionapproximation.dense.NumericVariableFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TileCodingFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TilingArrangement;

import uk.co.alexoyston.asteroids.simulation.PhysicsParams;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

public class TileCodingFactory {

  protected PhysicsParams phys;

  protected double polarMaxDist;
  protected double polarMaxAngle;
  protected double polarMaxAsteroidVelocity;
  protected double polarMaxSaucerVelocity;
  protected double polarMaxBulletVelocity;
  protected double polarMaxVelocity;

  protected double maxPlayerVelocity;
  protected double maxActiveShots;

  protected static final Object[] polarAgentFeatures = new Object[]{VAR_ACTIVE_SHOTS};
  protected static final Object[] polarObjectFeatures = new Object[]{VAR_DIST, VAR_ANGLE, VAR_VELOCITY_X, VAR_VELOCITY_Y};

  public TileCodingFactory(PhysicsParams phys) {
    this.phys = phys;

    maxPlayerVelocity = 2*(phys.playerThrustPower / phys.playerDrag) - (phys.playerThrustPower * phys.updateDelta);
    maxActiveShots = phys.playerMaxActiveShots;

    polarMaxDist = Math.max(phys.worldWidth, phys.worldHeight) / 2;
    polarMaxAngle = (Math.PI*2);
    polarMaxAsteroidVelocity = (maxPlayerVelocity + phys.asteroidMaxSpeed);
    polarMaxSaucerVelocity = (maxPlayerVelocity + phys.saucerSpeed);
    polarMaxBulletVelocity = (maxPlayerVelocity + phys.playerShotSpeed);
    polarMaxVelocity = Math.max(polarMaxAsteroidVelocity, Math.max(polarMaxSaucerVelocity, polarMaxBulletVelocity));
  }

  public TileCodingFeatures getPolarFeatures(int resolution, int numAsteroids, int numSaucers, int numBullets) {
    ConcatenatedObjectFeatures inputFeatures = new ConcatenatedObjectFeatures();

    inputFeatures.addObjectVectorizion(
      CLASS_AGENT, new NumericVariableFeatures(polarAgentFeatures)
    );
    inputFeatures.addObjectVectorizion(
      CLASS_ASTEROID, new NumericVariableFeatures(polarObjectFeatures)
    );
    inputFeatures.addObjectVectorizion(
      CLASS_SAUCER, new NumericVariableFeatures(polarObjectFeatures)
    );
    inputFeatures.addObjectVectorizion(
      CLASS_BULLET, new NumericVariableFeatures(polarObjectFeatures)
    );

    int numObjects = numAsteroids + numSaucers + numBullets;
    int numFeatures = polarAgentFeatures.length + numObjects * polarObjectFeatures.length;
    double[] weights = new double[numFeatures];

    int j = 0;

    weights[j++] = maxActiveShots / resolution;

    for (int n = 0; n < numObjects; n++) {
      weights[j++] = polarMaxDist / resolution;
      weights[j++] = polarMaxAngle / resolution;
      weights[j++] = polarMaxVelocity / resolution;
      weights[j++] = polarMaxVelocity / resolution;
    }

    TileCodingFeatures tilecoding = new TileCodingFeatures(inputFeatures);
    tilecoding.addTilingsForAllDimensionsWithWidths(
        weights, weights.length,
        TilingArrangement.RANDOM_JITTER);

    return tilecoding;
  }


}
