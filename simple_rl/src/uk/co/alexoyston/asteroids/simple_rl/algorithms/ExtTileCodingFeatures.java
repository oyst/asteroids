package burlap.behavior.functionapproximation.sparse.tilecoding;

import java.util.HashMap;

import burlap.behavior.functionapproximation.sparse.tilecoding.TileCodingFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.Tiling;
import burlap.behavior.functionapproximation.dense.DenseStateFeatures;

public class ExtTileCodingFeatures extends TileCodingFeatures {
  public ExtTileCodingFeatures(DenseStateFeatures features) {
    super(features);
  }

  public void addTilings(Tiling[] tilings) {
    for (Tiling tiling : tilings) {
      this.stateFeatures.add(new HashMap<Tiling.FVTile, Integer>());
      this.tilings.add(tiling);
    }
  }
}
