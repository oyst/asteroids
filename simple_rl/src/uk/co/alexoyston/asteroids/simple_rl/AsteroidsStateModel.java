package uk.co.alexoyston.asteroids.simple_rl;

import java.util.ArrayList;
import java.util.List;

protected class AsteroidsStateModel implements FullStateModel{
	protected double [][] transitionProbs;

	public AsteroidsStateModel() {
		this.transitionProbs = new double[4][4];
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				double p = i != j ? 0.2/3 : 0.8;
				transitionProbs[i][j] = p;
			}
		}
	}

	@Override
	public List<StateTransitionProb> stateTransitions(State s, Action a) {

		//get agent current position
		AsteroidsState gs = (AsteroidsState)s;

		int curX = gs.x;
		int curY = gs.y;

		int adir = actionDir(a);

		List<StateTransitionProb> tps = new ArrayList<StateTransitionProb>(4);
		StateTransitionProb noChange = null;
		for(int i = 0; i < 4; i++){

			int [] newPos = this.moveResult(curX, curY, i);
			if(newPos[0] != curX || newPos[1] != curY){
				//new possible outcome
				AsteroidsState ns = gs.copy();
				ns.x = newPos[0];
				ns.y = newPos[1];

				//create transition probability object and add to our list of outcomes
				tps.add(new StateTransitionProb(ns, this.transitionProbs[adir][i]));
			}
			else{
				//this direction didn't lead anywhere new
				//if there are existing possible directions
				//that wouldn't lead anywhere, aggregate with them
				if(noChange != null){
					noChange.p += this.transitionProbs[adir][i];
				}
				else{
					//otherwise create this new state and transition
					noChange = new StateTransitionProb(s.copy(), this.transitionProbs[adir][i]);
					tps.add(noChange);
				}
			}

		}


		return tps;
	}

	@Override
	public State sample(State s, Action a) {

		s = s.copy();
		AsteroidsState gs = (AsteroidsState)s;
		int curX = gs.x;
		int curY = gs.y;

		int adir = actionDir(a);

		//sample direction with random roll
		double r = Math.random();
		double sumProb = 0.;
		int dir = 0;
		for(int i = 0; i < 4; i++){
			sumProb += this.transitionProbs[adir][i];
			if(r < sumProb){
				dir = i;
				break; //found direction
			}
		}

		//get resulting position
		int [] newPos = this.moveResult(curX, curY, dir);

		//set the new position
		gs.x = newPos[0];
		gs.y = newPos[1];

		//return the state we just modified
		return gs;
	}

	protected int actionDir(Action a){
		int adir = -1;
		if(a.actionName().equals(ACTION_FWD)){
			adir = 0;
		}
		else if(a.actionName().equals(ACTION_ROT_RIGHT)){
			adir = 1;
		}
		else if(a.actionName().equals(ACTION_ROT_LEFT)){
			adir = 2;
		}
		else if(a.actionName().equals(ACTION_SHOOT)){
			adir = 3;
		}
		return adir;
	}


	protected int [] moveResult(int curX, int curY, int direction){

		//first get change in x and y from direction using 0: north; 1: south; 2:east; 3: west
		int xdelta = 0;
		int ydelta = 0;
		if(direction == 0){
			ydelta = 1;
		}
		else if(direction == 1){
			ydelta = -1;
		}
		else if(direction == 2){
			xdelta = 1;
		}
		else{
			xdelta = -1;
		}

		int nx = curX + xdelta;
		int ny = curY + ydelta;


		return new int[]{nx,ny};

	}
}