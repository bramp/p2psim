/**
 *
 */
package sim.stats;

import sim.events.Event;
import sim.events.Events;
import sim.main.Global;

/**
 * @author macquire
 *
 * Allows statistics gathering on the global stats object to
 * be turned on and off during execution of the event queue
 */
public class StatEnableEvent extends Event {
	private boolean status;

	public static StatEnableEvent newEvent(boolean status) {
		StatEnableEvent e = (StatEnableEvent) Event.newEvent(StatEnableEvent.class);
		e.status = status;

		return e;
	}

	@Override
	public void run() {
		System.err.print("Statistics logging ");

		if (status) {
			System.err.print("enabled");
		} else {
			System.err.print("disabled");
		}

		System.err.println(" at " + Events.getTime());

		Global.stats.enable(status);
	}

	@Override
	public long getEstimatedRunTime() {
		return 0;
	}

}
