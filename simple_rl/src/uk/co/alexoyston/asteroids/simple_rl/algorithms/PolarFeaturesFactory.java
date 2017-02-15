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

  public TileCodingFeatures getTileCodedFeatures(int resolution, int numTilings) {
    int numFeatures = polarAgentFeatures.size() + numObjects * polarObjectFeatures.size();
    double[] widths = new double[numFeatures];
    boolean[] dimensions = new boolean[numFeatures];

    // Apply TileCoding to the continuous features. i.e. the features
    //  belonging to a non-Agent (henceforth known as an object)
    int currWidth = 0;
    for (int obj = 0; obj < numObjects; obj++) {
      for (Object featureKey : polarObjectFeatures) {
        // widths[currWidth++] = domains.get(featureKey).span() / resolution;
        widths[currWidth++] = 1f / resolution;
      }
    }

    TileCodingFeatures tilecoding = new TileCodingFeatures(inputFeatures);

    // Add object tilings
    for (int currObject = 0; currObject < numObjects; currObject++) {

      // Set all dimensions to be disabled
      for (int i = 0; i < dimensions.length; i++)
        dimensions[i] = false;

      // Jump to the current object we are adding
      int currDimension = polarAgentFeatures.size() + currObject * polarObjectFeatures.size();
      // Enable the current objects features
      for (int i = 0; i < polarObjectFeatures.size(); i++)
        dimensions[currDimension++] = true;

      // Add tilings for this object
      // Not necessary at the moment as all objects are using the same jittering and
      //  numTilings - but future work could apply different params for each object
      tilecoding.addTilingsForDimensionsAndWidths(
        dimensions, widths, numTilings,
        TilingArrangement.RANDOM_JITTER
      );
    }

    return tilecoding;
  }

  public FourierBasis getFourierBasis(int order, int maxNonZeroEntries) {
		return new FourierBasis(inputFeatures, order, maxNonZeroEntries);
  }
}
