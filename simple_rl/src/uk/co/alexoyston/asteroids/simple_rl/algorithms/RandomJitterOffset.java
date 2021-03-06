package uk.co.alexoyston.asteroids.simple_rl.algorithms;

import java.util.Random;

import uk.co.alexoyston.asteroids.simple_rl.algorithms.TilingDimension;
import uk.co.alexoyston.asteroids.simple_rl.algorithms.TilingOffsetGenerator;

public class RandomJitterOffset implements TilingOffsetGenerator {

  @Override
  public double[] generate(int numTilings, int currTiling, TilingDimension[] tilingDimensions) {
    double [] offset = new double[tilingDimensions.length];

    Random rand = new Random();

    for (int i = 0; i < offset.length; i++) {
      TilingDimension tilingDimension = tilingDimensions[i];
      if (tilingDimension == null)
        offset[i] = 0;
      else
        offset[i] = rand.nextDouble() * tilingDimension.offsetRange;
    }

    return offset;
  }
}
