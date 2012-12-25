package sim.net.overlay.cdn.workload;

/**
 * Represents a action a user will carry out
 * including the user/object/time
 * @author Andrew Brampton
 */
public class Action {
	 public enum ActionType {
		seek,
		pause,
		stop;

		public String toString() {
			switch (this) {
				case seek:
					return "seek";
				case pause:
					return "pause";
				case stop:
					return "stop";
				default:
					return "unknown";
			}
		}
	};

	/**
	 * Time in ms, from the start of the logs (always starts from zero)
	 */
	public long realTime = -1;

	/**
	 * The int that identifies this user (starts from zero)
	 */
	public int user = -1;

	/**
	 * The int that identifies this object (starts from zero)
	 */
	public int object = -1;

	/**
	 * The type of this action
	 */
	public Action.ActionType type = null;

	public Action() {}

	/**
	 * A Copy constructor
	 * @param a
	 */
	protected Action(Action a) {
		assert a != null;

		realTime = a.realTime;
		user = a.user;
		object = a.object;
		type = a.type;
	}

	public String toString() {
		return "Action user:" + user + " @ " + realTime + "ms " + type + " object:" + object;
	}
}