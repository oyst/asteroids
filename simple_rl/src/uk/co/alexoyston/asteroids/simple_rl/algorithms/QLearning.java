package uk.co.alexoyston.asteroids.simple_rl.algorithms;

import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.LearningAgent;
// import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.mdp.singleagent.oo.OOSADomain;

public class QLearning {
	protected static String name = "Q-Learning";

	// public static class LearningFactory implements LearningAgentFactory {
  //
	// 	protected OOSADomain domain;
  //
	// 	public LearningFactory(OOSADomain domain) {
	// 		this.domain = domain;
	// 	}
  //
	// 	@Override
	// 	public String getAgentName() {
	// 		return name;
	// 	}
  //
	// 	@Override
	// 	public LearningAgent generateAgent() {
	// 		return new QLearning(domain, 0.99, new SimpleHashableStateFactory(), 0.3, 0.1);
	// 	}
  //
	// }
}
