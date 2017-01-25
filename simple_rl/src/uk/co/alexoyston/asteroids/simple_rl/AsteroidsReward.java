package uk.co.alexoyston.asteroids.simple_rl;

public static class AsteroidsReward implements RewardFunction {

	public AsteroidsReward(){
	}

	@Override
	public double reward(State s, Action a, State sprime) {
		return -1;
	}


}