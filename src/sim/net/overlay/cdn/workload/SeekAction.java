/**
 *
 */
package sim.net.overlay.cdn.workload;

public class SeekAction extends Action {

	final static boolean STORE_WHY = true;

	public SeekAction(Action a) {
		super(a);

		if (a.type != ActionType.seek)
			throw new RuntimeException("Action must be of type seek");

	}

	/**
	 * The time we are seeking to (in seconds)
	 */
	public int mediaTime = -1;

	/**
	 * Optional reason why we are seeking
	 */
	public String why = null;

	public String toString() {
		return "Action user:" + user + " @ " + realTime + "ms " + type + " to " + mediaTime + " object:" + object + " " + why;
	}
}