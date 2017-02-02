package uk.co.alexoyston.asteroids.simple_rl.props;

import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.oo.state.OOState;

import uk.co.alexoyston.asteroids.simple_rl.state.EntityState;

import static uk.co.alexoyston.asteroids.simple_rl.AsteroidsDomain.*;

public abstract class ObjectCollision extends PropositionalFunction {

	public ObjectCollision(String name, String objectClass1, String objectClass2) {
		super(name, new String[]{objectClass1, objectClass2});
	}

	@Override
	public boolean isTrue(OOState state, String... params) {
		EntityState obj1 = (EntityState)state.object(params[0]);
		EntityState obj2 = (EntityState)state.object(params[1]);

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
			EntityState agent = (EntityState)state.object(params[0]);

			for (String param : params) {
				if (param.equals(CLASS_AGENT)) continue;

				EntityState obj = (EntityState)state.object(param);
				if (collides(agent, obj)) return true;
			}
			return false;
		}
	}

	private static boolean collides(EntityState obj1, EntityState obj2) {
		if (obj1.x > obj2.x + obj2.width	||
				obj1.y > obj2.y + obj2.height ||
				obj1.x + obj1.width < obj2.x	||
				obj1.y + obj1.height < obj2.y)
			return false;
		return true;
	}

}
