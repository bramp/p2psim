package sim.net.overlay.dht.stealth.events;

import sim.events.Event;
import sim.events.Events;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.events.FailEvent;
import sim.net.overlay.dht.stealth.StealthPeer;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

/**
 * Fails StealthPeers due to a distribution
 * @author Andrew Brampton
 */
public class RandomFailStealthCount extends sim.net.overlay.dht.events.repeatable.FailAndPassEvent {
	static final int DEFAULT_DELAY = 10000; // The time between each event

	public static RandomFailStealthCount newEvent(int count) {
		return newEvent(new Constant(DEFAULT_DELAY), count);
	}

	public static RandomFailStealthCount newEvent(Distribution d, int count) {
		RandomFailStealthCount e = (RandomFailStealthCount) Event.newEvent(RandomFailStealthCount.class);
		e.init(d, count);
		return e;
	}

	@Override
	public void run() throws Exception {

		HostSet unfailed = getAlivePeers().getType(StealthPeer.class);

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
