package uk.co.alexoyston.asteroids.simple_rl.props;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.oo.OODomain;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.StateUtilities;

import uk.co.alexoyston.asteroids.simple_rl.state.AgentState;
import uk.co.alexoyston.asteroids.simple_rl.state.EnemyState;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

public abstract class ObjectCollision extends PropositionalFunction{

  public ObjectCollision(String name, String objectClass1, String objectClass2) {
    super(name, new String[]{objectClass1, objectClass2});
  }

  @Override
  public boolean isTrue(OOState state, String... params) {
    ObjectInstance obj1 = (ObjectInstance)state.object(params[0]);
    ObjectInstance obj2 = (ObjectInstance)state.object(params[1]);

    return collides(obj1, obj2);
  }

  private abstract static class ObjectShot extends ObjectCollision {
    public ObjectShot(String name, String shotObjectClass) {
      super(name, CLASS_BULLET, shotObjectClass);
    }
  }

  public static class AgentShot extends ObjectShot {
    public AgentShot(String name) {
      super(name, CLASS_AGENT);
    }
  }

  public static class AsteroidShot extends ObjectShot {
    public AsteroidShot(String name) {
      super(name, CLASS_ASTEROID);
    }
  }

  public static class SaucerShot extends ObjectShot {
    public SaucerShot(String name) {
      super(name, CLASS_SAUCER);
    }
  }

  public static class AgentKilled extends PropositionalFunction {
    public AgentKilled(String name) {
      super(name, new String[]{CLASS_AGENT, CLASS_ASTEROID, CLASS_SAUCER, CLASS_BULLET});
    }

    @Override
    public boolean isTrue(OOState state, String... params) {
      ObjectInstance agent = (ObjectInstance)state.object(params[0]);
      if (!params[0].equals(CLASS_AGENT)) System.out.println(params[0]);
      for (String param : params) {
        if (param.equals(CLASS_AGENT)) continue;
        ObjectInstance obj = (ObjectInstance)state.object(param);
        if (collides(agent, obj)) return true;
      }
      return false;
    }
  }

  private static boolean collides(ObjectInstance obj1, ObjectInstance obj2) {

    float obj1_x = StateUtilities.stringOrNumber(obj1.get(VAR_X)).floatValue();
    float obj1_y = StateUtilities.stringOrNumber(obj1.get(VAR_Y)).floatValue();
    float obj1_width = StateUtilities.stringOrNumber(obj1.get(VAR_WIDTH)).floatValue();
    float obj1_height = StateUtilities.stringOrNumber(obj1.get(VAR_HEIGHT)).floatValue();

    float obj2_x = StateUtilities.stringOrNumber(obj2.get(VAR_X)).floatValue();
    float obj2_y = StateUtilities.stringOrNumber(obj2.get(VAR_Y)).floatValue();
    float obj2_width = StateUtilities.stringOrNumber(obj2.get(VAR_WIDTH)).floatValue();
    float obj2_height = StateUtilities.stringOrNumber(obj2.get(VAR_HEIGHT)).floatValue();

    if (
    obj1_x > obj2_x + obj2_width ||
    obj1_y > obj2_y + obj2_height ||
    obj1_x + obj1_width < obj2_x ||
    obj1_y + obj1_height < obj2_y) {
      return false;
    }
    return true;

  }

}
