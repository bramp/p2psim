/*
 * Created on 13-May-2005
 */
package sim.net.overlay.dht.events.repeatable;

import sim.events.Event;
import sim.events.Events;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.events.FailEvent;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.events.JoinEvent;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

public class UnfailAndJoinAndPassEvent extends RepeatableHelperEvent {

	static final int DEFAULT_DELAY = 10000; // The time between each event

	public static UnfailAndJoinAndPassEvent newEvent(int count) {
		return newEvent( new Constant(DEFAULT_DELAY), count );
	}

	public static UnfailAndJoinAndPassEvent newEvent(Distribution d, int count) {
		UnfailAndJoinAndPassEvent e = (UnfailAndJoinAndPassEvent) Event.newEvent(UnfailAndJoinAndPassEvent.class);
		e.init(d, count);
		return e;
	}

	@Override
	public void run() throws Exception {

		HostSet joined = getJoinedPeers();
		HostSet notjoined = getFailedPeers();

		if (!joined.isEmpty() && !notjoined.isEmpty()) {
			Host h0 = joined.getRandom();
			Host h1 = notjoined.getRandom();
			DHTInterface p1 = (DHTInterface) h1;

			Events.addNow(FailEvent.newEvent(h1, false));
			Events.addNow(JoinEvent.newEvent(p1, h0.getAddress()));

		} else {
			// if there are no unfailed peers left, just stop
			Trace.println(LogLevel.WARN, "Could not cause requested number of unfailures + joins!");
		}

		reschedule();
	}

}
