package uk.co.alexoyston.asteroids.simple_rl.algorithms;

import uk.co.alexoyston.asteroids.simple_rl.algorithms.TilingDimension;

public interface TilingOffsetGenerator {
  public double[] generate(int numTilings, int currTiling, TilingDimension[] tilingDimensions);
}
