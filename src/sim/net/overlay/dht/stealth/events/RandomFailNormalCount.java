package sim.net.overlay.dht.stealth.events;

import sim.events.Event;
import sim.events.Events;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.events.FailEvent;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

/**
 * Fails NormalPeers due to a distribution
 * @author Andrew Brampton
 */
public class RandomFailNormalCount extends sim.net.overlay.dht.events.repeatable.FailAndPassEvent {
	static final int DEFAULT_DELAY = 10000; // The time between each event

	public static RandomFailNormalCount newEvent(int count) {
		return newEvent(new Constant(DEFAULT_DELAY), count);
	}

	public static RandomFailNormalCount newEvent(Distribution d, int count) {
		RandomFailNormalCount e = (RandomFailNormalCount) Event.newEvent(RandomFailNormalCount.class);
		e.init(d, count);
		return e;
	}

	@Override
	public void run() throws Exception {

		HostSet unfailed = getAlivePeers().getType(ServicePeer.class);

		if(!unfailed.isEmpty()) {
			Host h = unfailed.getRandom();
			Events.addNow(FailEvent.newEvent(h, true));
			reschedule();
		}
		else {
			// if there are no unfailed peers left, just stop
			Trace.println(LogLevel.WARN, "Could not cause requested number of failures!");
		}
	}
}
