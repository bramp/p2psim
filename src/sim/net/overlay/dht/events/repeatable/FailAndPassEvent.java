package sim.net.overlay.dht.events.repeatable;


import sim.events.Event;
import sim.events.Events;
import sim.main.Global;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.events.FailEvent;
import sim.net.overlay.dht.DHTInterface;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

/**
 * Fails peers due to a distribution
 * @author Andrew Brampton
 */
public class FailAndPassEvent extends RepeatableHelperEvent {
	static final int DEFAULT_DELAY = 10000; // The time between each event

	public static FailAndPassEvent newEvent(int count) {
		return newEvent(new Constant(DEFAULT_DELAY), count);
	}

	public static FailAndPassEvent newEvent(Distribution d, int count) {
		return newEvent(d, Global.hosts.getType(DHTInterface.class), count);
	}

	public static FailAndPassEvent newEvent(Distribution d, HostSet hosts, int count) {
		FailAndPassEvent e = (FailAndPassEvent) Event.newEvent(FailAndPassEvent.class);
		e.init(d, count);
		e.hosts = hosts;
		return e;
	}

	@Override
	public void run() throws Exception {

		HostSet alive = getAlivePeers();

		if(!alive.isEmpty()) {
			Host h = alive.getRandom();
			Events.addNow(FailEvent.newEvent(h, true));
		}
		else {
			// if there are no unfailed peers left, just log this error
			Trace.println(LogLevel.WARN, "Could not cause requested number of failures!");
		}

		reschedule();
	}
}
