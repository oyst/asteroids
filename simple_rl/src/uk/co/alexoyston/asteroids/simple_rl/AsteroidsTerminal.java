package uk.co.alexoyston.asteroids.simple_rl;

public static class AsteroidsTerminal implements TerminalFunction {

	public AsteroidsTerminal(){
	}

	@Override
	public boolean isTerminal(State s) {
		return false;
	}
}