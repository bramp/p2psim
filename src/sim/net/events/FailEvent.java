/*
 * Created on 05-Mar-2005
 */
package sim.net.events;

import sim.events.Event;
import sim.net.Host;

/**
 * A Host is failing (or coming alive) event
 * @author Andrew Brampton
 */
public class FailEvent extends Event {
	public Host host;
	public boolean failed;

	/**
	 * Fails (or brings to life) a Host
	 * @param host The host to fail
	 * @param failed True - The host fails, False - The host lives
	 */
	public static FailEvent newEvent(Host host, boolean failed) {
		FailEvent e = (FailEvent) Event.newEvent(FailEvent.class);
		e.host = host;
		e.failed = failed;
		return e;
	}

	@Override
	public void run() {
		host.setFailed(failed);
	}

	@Override
	public long getEstimatedRunTime() {
		return 1;
	}
}