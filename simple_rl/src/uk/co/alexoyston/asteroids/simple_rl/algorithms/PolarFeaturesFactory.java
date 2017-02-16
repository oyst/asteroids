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
import burlap.behavior.functionapproximation.sparse.tilecoding.ExtTileCodingFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TilingArrangement;
import burlap.behavior.functionapproximation.sparse.tilecoding.Tiling;
import burlap.mdp.core.state.vardomain.VariableDomain;

import uk.co.alexoyston.asteroids.simulation.PhysicsParams;

import uk.co.alexoyston.asteroids.simple_rl.state.AgentState;
import uk.co.alexoyston.asteroids.simple_rl.state.AsteroidsState;
import uk.co.alexoyston.asteroids.simple_rl.state.PolarState;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

public class PolarFeaturesFactory {

  protected Map<Object, VariableDomain> domains;
  protected DenseStateFeatures inputFeatures;

  protected final static int numObjects = AsteroidsState.closestAsteroidsCount + AsteroidsState.closestSaucersCount + AsteroidsState.closestBulletsCount;

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
    // inputFeatures = getFeatures();
  }

  protected DenseStateFeatures getFeatures() {
    ConcatenatedObjectFeatures inputFeatures = new ConcatenatedObjectFeatures();
    inputFeatures.addObjectVectorizion(CLASS_AGENT, agentNumFeatures);
    inputFeatures.addObjectVectorizion(CLASS_OBJECT, objNumFeatures);
    return inputFeatures;
  }

  protected DenseStateFeatures getNormalizedFeatures() {
    ConcatenatedObjectFeatures inputFeatures = new ConcatenatedObjectFeatures();
    inputFeatures.addObjectVectorizion(CLASS_AGENT, agentNormFeatures);
    inputFeatures.addObjectVectorizion(CLASS_OBJECT, objNormFeatures);
    return inputFeatures;
  }

  public ExtTileCodingFeatures getExtTileCodedFeatures(int resolution, int numTilings) {
    int numFeatures = polarAgentFeatures.size() + (numObjects * polarObjectFeatures.size());
    double[] widths = new double[numFeatures];
    boolean[] dimensions = new boolean[numFeatures];

    int currFeature = 0;

    ExtTileCodingFeatures tilecoding = new ExtTileCodingFeatures(inputFeatures);

    TilingBuilder tb = new TilingBuilder(numFeatures);

    for (Object featureKey : polarAgentFeatures)
      tb.nextDimension(new TilingDimension.Discrete());

    for (Object featureKey : polarObjectFeatures)
      tb.nextDimension(new TilingDimension.Uniform(resolution, 0, 1));

    Tiling[] tilings = tb.build(numTilings, new RandomJitterOffset());
    tilecoding.addTilings(tilings);

    return tilecoding;
  }

  public FourierBasis getFourierBasis(int order, int maxNonZeroEntries) {
		return new FourierBasis(inputFeatures, order, maxNonZeroEntries);
  }
}
