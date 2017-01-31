package uk.co.alexoyston.asteroids.simple_rl.actions;

import java.util.List;
import java.util.ArrayList;

import burlap.mdp.core.state.State;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.SimpleAction;
import burlap.mdp.core.action.ActionType;

import uk.co.alexoyston.asteroids.simulation.PhysicsParams;

import uk.co.alexoyston.asteroids.simple_rl.state.AgentState;
import uk.co.alexoyston.asteroids.simple_rl.state.AsteroidsState;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

public class ShootActionType implements ActionType {

  private PhysicsParams phys;
  private String name;
  private String actionName;

  public ShootActionType(String actionTypeName, String actionName, PhysicsParams phys) {
    this.phys = phys;
    this.name = actionTypeName;
    this.actionName = actionName;
  }

  @Override
  public List<Action>	allApplicableActions(State s) {
    ArrayList<Action> actions = new ArrayList<Action>();

    AsteroidsState state = (AsteroidsState)s;
    AgentState agent = (AgentState)state.object(CLASS_AGENT);
    int activeShots = (int)agent.get(VAR_ACTIVE_SHOTS);

    if (activeShots < phys.playerMaxActiveShots)
      actions.add(new SimpleAction(actionName));
      
    return actions;
  }

  @Override
  public Action	associatedAction(String strRep) {
    if (strRep.equals(actionName))
      return new SimpleAction(actionName);
    throw new IllegalArgumentException("Unknown action name");
  }

  @Override
  public String	typeName() {
    return name;
  }
}
