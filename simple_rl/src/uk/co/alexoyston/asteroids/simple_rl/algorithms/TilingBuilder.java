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

  public Tiling build() {
    return new MixedTiling(tilingDimensions);
  }

  protected class MixedTiling extends Tiling {
    protected TilingDimension[] dimensions;

    public MixedTiling(TilingDimension[] dimensions) {
      super(new double[0], new double[0], new boolean[dimensions.length]);

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
          tiledVector[i] = this.dimensions[i].getReceptiveTile(input[i]);
      }
      Tiling.FVTile tile = new Tiling.FVTile(tiledVector);
      return tile;
    }
  }
}
