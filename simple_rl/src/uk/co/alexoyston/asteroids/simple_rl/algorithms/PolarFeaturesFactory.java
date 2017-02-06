package uk.co.alexoyston.asteroids.simple_rl.algorithms;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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

  protected Map<Object, VariableDomain> domains;
  protected DenseStateFeatures inputFeatures;

  protected final static int numAsteroids = AsteroidsState.closestAsteroidsCount;
  protected final static int numSaucers = AsteroidsState.closestSaucersCount;
  protected final static int numBullets = AsteroidsState.closestBulletsCount;

  protected final static List<Object> polarAgentFeatures = AgentState.keys;
  protected final static List<Object> polarObjectFeatures = PolarState.keys;

  protected final NormalizedVariableFeatures agentNormFeatures;
  protected final NormalizedVariableFeatures objNormFeatures;

  protected final NumericVariableFeatures agentNumFeatures;
  protected final NumericVariableFeatures objNumFeatures;

  public PolarFeaturesFactory(Map<Object, VariableDomain> domains) {
    this.domains = domains;

    agentNormFeatures = new NormalizedVariableFeatures();
    for (Object key : polarAgentFeatures)
      agentNormFeatures.variableDomain(key, domains.get(key));

    objNormFeatures = new NormalizedVariableFeatures();
    for (Object key : polarObjectFeatures)
      objNormFeatures.variableDomain(key, domains.get(key));

    agentNumFeatures = new NumericVariableFeatures(polarAgentFeatures);
    objNumFeatures = new NumericVariableFeatures(polarObjectFeatures);

    inputFeatures = getNormalizedFeatures();
  }

  protected DenseStateFeatures getFeatures() {
    ConcatenatedObjectFeatures inputFeatures = new ConcatenatedObjectFeatures();
    inputFeatures.addObjectVectorizion(CLASS_AGENT, agentNumFeatures);
    inputFeatures.addObjectVectorizion(CLASS_ASTEROID, objNumFeatures);
    inputFeatures.addObjectVectorizion(CLASS_SAUCER, objNumFeatures);
    inputFeatures.addObjectVectorizion(CLASS_BULLET, objNumFeatures);
    return inputFeatures;
  }

  protected DenseStateFeatures getNormalizedFeatures() {
    ConcatenatedObjectFeatures inputFeatures = new ConcatenatedObjectFeatures();
    inputFeatures.addObjectVectorizion(CLASS_AGENT, agentNormFeatures);
    inputFeatures.addObjectVectorizion(CLASS_ASTEROID, objNormFeatures);
    inputFeatures.addObjectVectorizion(CLASS_SAUCER, objNormFeatures);
    inputFeatures.addObjectVectorizion(CLASS_BULLET, objNormFeatures);
    return inputFeatures;
  }

  public TileCodingFeatures getTileCoding(int resolution) {
    int numObjects = numAsteroids + numSaucers + numBullets;
    int numFeatures = polarAgentFeatures.size() + numObjects * polarObjectFeatures.size();
    double[] weights = new double[numFeatures];

    int j = 0;

    for (Object key : polarAgentFeatures)
      weights[j++] = domains.get(key).span() / resolution;
    for (int i = 0; i < numObjects; i++) {
      for (Object key : polarObjectFeatures)
        weights[j++] = domains.get(key).span() / resolution;
    }

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
