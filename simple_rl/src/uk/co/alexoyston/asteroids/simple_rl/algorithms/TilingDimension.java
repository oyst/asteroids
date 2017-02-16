package uk.co.alexoyston.asteroids.simple_rl.algorithms;

import burlap.behavior.functionapproximation.sparse.tilecoding.Tiling;

public abstract class TilingDimension {

  public abstract int getReceptiveTile(double input);
  public double offsetRange = 0;

  public static class Uniform extends TilingDimension {
    protected double lowerVal;
    protected double width;

    /* numTiles across the range lowerVal -> upperVal */
    public Uniform(int numTiles, double lowerVal, double upperVal) {
      this.lowerVal = lowerVal;
      this.width = (upperVal - lowerVal) / numTiles;
      this.offsetRange = (upperVal - lowerVal);
    }

    @Override
    public int getReceptiveTile(double input) {
      return input == this.lowerVal ? 1 : (int)Math.ceil((input - this.lowerVal) / this.width);
    }
  }

  public static class Exponential extends TilingDimension {
    protected double scale;
    protected double shift;
    protected double lnRate;

    public Exponential(int numTiles, double lowerVal, double upperVal, double rate) {
      double newLower = 1;
      double newUpper = Math.pow(rate, numTiles) - 1;
      scale = (newUpper - newLower) / (upperVal - lowerVal);
      shift = -lowerVal*scale + newLower;
      lnRate = Math.log(rate);
      this.offsetRange = (upperVal - lowerVal);
    }

    @Override
    public int getReceptiveTile(double input) {
      input = (input * scale) + shift;
      return (int)Math.floor(Math.log(input) / lnRate) + 1;
    }
  }

  public static class Discrete extends TilingDimension {
    public Discrete() {}

    @Override
    public int getReceptiveTile(double input) {
      return (int)input;
    }
  }

}
