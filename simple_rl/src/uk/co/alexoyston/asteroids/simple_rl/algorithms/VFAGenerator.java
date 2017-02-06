package uk.co.alexoyston.asteroids.simple_rl.algorithms;

import burlap.behavior.functionapproximation.DifferentiableStateActionValue;

public interface VFAGenerator {
  public DifferentiableStateActionValue generateVFA(double defaultWeightValue);
}
