package uk.co.alexoyston.asteroids.simple_rl.algorithms;

import java.util.List;
import java.util.ArrayList;

import burlap.behavior.functionapproximation.dense.ConcatenatedObjectFeatures;
import burlap.behavior.functionapproximation.dense.DenseStateFeatures;
import burlap.behavior.functionapproximation.dense.NumericVariableFeatures;
import burlap.behavior.functionapproximation.dense.NormalizedVariableFeatures;
import burlap.behavior.functionapproximation.dense.fourier.FourierBasis;
import burlap.behavior.functionapproximation.sparse.tilecoding.TileCodingFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TilingArrangement;
import burlap.mdp.core.state.vardomain.VariableDomain;

import uk.co.alexoyston.asteroids.simulation.PhysicsParams;

import uk.co.alexoyston.asteroids.simple_rl.state.AgentState;
import uk.co.alexoyston.asteroids.simple_rl.state.AsteroidsState;
import uk.co.alexoyston.asteroids.simple_rl.state.PolarState;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

public class PolarFeaturesFactory {

  protected PhysicsParams phys;
  protected DenseStateFeatures inputFeatures;

  protected final static int numAsteroids = AsteroidsState.closestAsteroidsCount;
  protected final static int numSaucers = AsteroidsState.closestSaucersCount;
  protected final static int numBullets = AsteroidsState.closestBulletsCount;

  protected final static List<Object> polarAgentFeatures = AgentState.keys;
  protected final static List<Object> polarObjectFeatures = PolarState.keys;

  protected double polarMaxDist;
  protected double polarMaxAngle;
  protected double polarMaxAsteroidVelocity;
  protected double polarMaxSaucerVelocity;
  protected double polarMaxBulletVelocity;
  protected double polarMaxVelocity;

  protected double maxPlayerVelocity;
  protected double maxActiveShots;

  public PolarFeaturesFactory(PhysicsParams phys) {
    this.phys = phys;

    maxPlayerVelocity = 2*(phys.playerThrustPower / phys.playerDrag) - (phys.playerThrustPower * phys.updateDelta);
    maxActiveShots = phys.playerMaxActiveShots;

    polarMaxDist = Math.max(phys.worldWidth, phys.worldHeight) / 2;
    polarMaxAngle = (Math.PI*2);
    polarMaxAsteroidVelocity = (maxPlayerVelocity + phys.asteroidMaxSpeed);
    polarMaxSaucerVelocity = (maxPlayerVelocity + phys.saucerSpeed);
    polarMaxBulletVelocity = (maxPlayerVelocity + phys.playerShotSpeed);
    polarMaxVelocity = Math.max(polarMaxAsteroidVelocity, Math.max(polarMaxSaucerVelocity, polarMaxBulletVelocity));

    inputFeatures = getNormalizedFeatures(numAsteroids, numSaucers, numBullets);
  }

  protected DenseStateFeatures getFeatures(int numAsteroids, int numSaucers, int numBullets) {
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

    return inputFeatures;
  }

  protected DenseStateFeatures getNormalizedFeatures(int numAsteroids, int numSaucers, int numBullets) {
    ConcatenatedObjectFeatures inputFeatures = new ConcatenatedObjectFeatures();

    inputFeatures.addObjectVectorizion(
      CLASS_AGENT, new NormalizedVariableFeatures()
        .variableDomain(VAR_ACTIVE_SHOTS, new VariableDomain(0, maxActiveShots))
    );
    inputFeatures.addObjectVectorizion(
      CLASS_ASTEROID, new NormalizedVariableFeatures()
        .variableDomain(VAR_DIST, new VariableDomain(-polarMaxDist, polarMaxDist))
        .variableDomain(VAR_ANGLE, new VariableDomain(0, 2*Math.PI))
    );
    inputFeatures.addObjectVectorizion(
      CLASS_SAUCER, new NormalizedVariableFeatures()
        .variableDomain(VAR_DIST, new VariableDomain(-polarMaxDist, polarMaxDist))
        .variableDomain(VAR_ANGLE, new VariableDomain(0, 2*Math.PI))
    );
    inputFeatures.addObjectVectorizion(
      CLASS_BULLET, new NormalizedVariableFeatures()
        .variableDomain(VAR_DIST, new VariableDomain(-polarMaxDist, polarMaxDist))
        .variableDomain(VAR_ANGLE, new VariableDomain(0, 2*Math.PI))
    );

    return inputFeatures;
  }

  public TileCodingFeatures getTileCoding(int resolution) {
    int numObjects = numAsteroids + numSaucers + numBullets;
    int numFeatures = polarAgentFeatures.size() + numObjects * polarObjectFeatures.size();
    double[] weights = new double[numFeatures];

    int j = 0;

    weights[j++] = maxActiveShots / resolution;
    for (int n = 0; n < numObjects; n++) {
      weights[j++] = polarMaxDist / resolution;
      weights[j++] = polarMaxAngle / resolution;
      weights[j++] = polarMaxVelocity / resolution;
      weights[j++] = polarMaxVelocity / resolution;
    }

    // weights[j++] = 1f/resolution;
    // for (int n = 0; n < numObjects; n++) {
    //   weights[j++] = polarMaxDist / resolution;
    //   weights[j++] = polarMaxAngle / resolution;
    //   weights[j++] = polarMaxVelocity / resolution;
    //   weights[j++] = polarMaxVelocity / resolution;
    // }

    TileCodingFeatures tilecoding = new TileCodingFeatures(inputFeatures);
    tilecoding.addTilingsForAllDimensionsWithWidths(
    weights, weights.length,
    TilingArrangement.RANDOM_JITTER);

    return tilecoding;
  }

  public FourierBasis getFourierBasis(int order, int maxNonZeroEntries) {
		return new FourierBasis(inputFeatures, order, maxNonZeroEntries);
  }
}
