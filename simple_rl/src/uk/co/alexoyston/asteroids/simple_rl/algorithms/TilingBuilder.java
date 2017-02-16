package uk.co.alexoyston.asteroids.simple_rl.algorithms;

import burlap.behavior.functionapproximation.sparse.tilecoding.Tiling;

public class TilingBuilder {
  protected TilingDimension[] tilingDimensions;
  protected int nextDimension = 0;

  public TilingBuilder(int numDimensions) {
    tilingDimensions = new TilingDimension[numDimensions];
  }

  public TilingBuilder nextDimension(TilingDimension dimension) {
    return addDimension(dimension, nextDimension);
  }

  public TilingBuilder addDimension(TilingDimension dimension, int dimensionIndex) {
    tilingDimensions[dimensionIndex] = dimension;
    nextDimension = dimensionIndex + 1;
    return this;
  }

  public TilingBuilder skipDimension() {
    nextDimension++;
    return this;
  }

  public Tiling[] build(int numTilings, TilingOffsetGenerator offsetGenerator) {
    Tiling[] tilings = new Tiling[numTilings];
    for (int i = 0; i < numTilings; i++) {
      double[] offset = offsetGenerator.generate(numTilings, i, tilingDimensions);
      tilings[i] = new MixedTiling(tilingDimensions, offset);
    }
    return tilings;
  }

  protected class MixedTiling extends Tiling {
    protected TilingDimension[] dimensions;

    public MixedTiling(TilingDimension[] dimensions, double[] offset) {
      super(new double[0], offset, new boolean[dimensions.length]);

      int i = 0;
      for (TilingDimension td : dimensions)
        this.dimensionMask[i++] = (td != null);

      this.dimensions = dimensions;
    }

    @Override
    public Tiling.FVTile getFVTile(double [] input){
      if (input.length != dimensions.length) {
        throw new RuntimeException("lengths not matching");
      }
      int [] tiledVector = new int[input.length];
      for (int i = 0; i < input.length; i++) {
        if (this.dimensions[i] != null)
          tiledVector[i] = this.dimensions[i].getReceptiveTile(input[i] - this.offset[i]);
      }
      Tiling.FVTile tile = new Tiling.FVTile(tiledVector);
      return tile;
    }
  }
}
