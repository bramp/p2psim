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
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.events.JoinEvent;

public class JoinAndPassEvent extends RepeatableHelperEvent {

	static final int DEFAULT_DELAY = 10000; // The time between each event

	public static JoinAndPassEvent newEvent(int count) {
		return newEvent( new Constant(DEFAULT_DELAY), count );
	}

	public static JoinAndPassEvent newEvent(Distribution d, int count) {
		JoinAndPassEvent e = (JoinAndPassEvent) Event.newEvent(JoinAndPassEvent.class);
		e.init(d, count);
		return e;
	}

	@Override
	public void run() throws Exception {

		HostSet joined = getJoinedPeers();
		HostSet notjoined = getNotJoinedPeers();

		// Do special first join
		if (joined.isEmpty()) {
			Events.addNow(JoinEvent.newEvent((DHTInterface) notjoined.getRandom(), Host.INVALID_ADDRESS));
		} else {
			if (!notjoined.isEmpty()) {
				Host h0 = joined.getRandom();
				DHTInterface p1 = (DHTInterface) notjoined.getRandom();

				Events.addNow(JoinEvent.newEvent(p1, h0.getAddress()));
			}
		}

		reschedule();
	}

}
