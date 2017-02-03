package uk.co.alexoyston.asteroids.simple_rl.algorithms;

import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.statehashing.HashableStateFactory;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.tdmethods.SarsaLam;
import burlap.behavior.singleagent.learning.tdmethods.vfa.GradientDescentSarsaLam;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.behavior.functionapproximation.DifferentiableStateActionValue;


public class Sarsa {

	private static abstract class SarsaFactoryBuilder {
		public String name;
		public OOSADomain domain;
		public double gamma = 0.99;
		public double learningRate = 0.1;
		public double lambda = 1.0;

		public SarsaFactoryBuilder(String name, OOSADomain domain) {
			this.name = name;
			this.domain = domain;
		}

		public SarsaFactoryBuilder setGamma(double gamma) {
			this.gamma = gamma;
			return this;
		}

		public SarsaFactoryBuilder setLearningRate(double learningRate) {
			this.learningRate = learningRate;
			return this;
		}

		public SarsaFactoryBuilder setLambda(double lambda) {
			this.lambda = lambda;
			return this;
		}

		public abstract SarsaFactory build();
	}

	public static abstract class SarsaFactory implements LearningAgentFactory {
		protected String name;
		protected OOSADomain domain;
		protected double gamma;
		protected double learningRate;
		protected double lambda;

		public SarsaFactory(String name, OOSADomain domain, double gamma, double learningRate, double lambda) {
			this.name = name;
			this.domain = domain;
			this.gamma = gamma;
			this.learningRate = learningRate;
			this.lambda = lambda;
		}

		@Override
		public abstract String getAgentName();

		@Override
		public abstract LearningAgent generateAgent();
	}

	public static class SarsaLamFactoryBuilder extends SarsaFactoryBuilder {
		public double qInit = 0.1;
		public HashableStateFactory hashFactory = null;

		public SarsaLamFactoryBuilder(OOSADomain domain) {
			super("SARSA(lambda)", domain);
		}

		public SarsaLamFactoryBuilder setQInit(double qInit) {
			this.qInit = qInit;
			return this;
		}

		public SarsaLamFactoryBuilder setHashFactory(HashableStateFactory hashFactory) {
			this.hashFactory = hashFactory;
			return this;
		}

		public SarsaLamFactory build() {
			if (hashFactory == null) hashFactory = new SimpleHashableStateFactory();
			return new SarsaLamFactory(name, domain, gamma, hashFactory, qInit, learningRate, lambda);
		}
	}

	public static class SarsaLamFactory extends SarsaFactory {
		protected double qInit;
		protected HashableStateFactory hashFactory;

		public SarsaLamFactory(String name, OOSADomain domain, double gamma, HashableStateFactory hashFactory, double qInit, double learningRate, double lambda) {
			super(name, domain, gamma, learningRate, lambda);
			this.hashFactory = hashFactory;
			this.qInit = qInit;
		}

		@Override
		public String getAgentName() {
			return String.format("%s: (%.2f, %.2f, %.2f, %.2f)", name, gamma, qInit, learningRate, lambda);
		}

		@Override
		public LearningAgent generateAgent() {
			return new SarsaLam(domain, gamma, hashFactory, qInit, learningRate, lambda);
		}
	}

	public static class GDSarsaLamFactoryBuilder extends SarsaFactoryBuilder {
		public DifferentiableStateActionValue vfa;

		public GDSarsaLamFactoryBuilder(OOSADomain domain, DifferentiableStateActionValue vfa) {
			super("Gradient Descent SARSA(lambda)", domain);
			this.vfa = vfa;
		}

		public GDSarsaLamFactory build() {
			return new GDSarsaLamFactory(name, domain, gamma, vfa, learningRate, lambda);
		}
	}

	public static class GDSarsaLamFactory extends SarsaFactory {
		protected DifferentiableStateActionValue vfa;

		public GDSarsaLamFactory(String name, OOSADomain domain, double gamma, DifferentiableStateActionValue vfa, double learningRate, double lambda) {
			super(name, domain, gamma, learningRate, lambda);
			this.vfa = vfa;
		}

		@Override
		public String getAgentName() {
			return String.format("%s: (%.2f, %.2f, %.2f)", name, gamma, learningRate, lambda);
		}

		@Override
		public LearningAgent generateAgent() {
			return new GradientDescentSarsaLam(domain, gamma, vfa, learningRate, lambda);
		}
	}
}
