/*
 * Created on 13-May-2005
 */
package sim.net.overlay.dht.stealth.events;

import sim.events.Event;
import sim.events.Events;
import sim.math.Distribution;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.events.JoinEvent;
import sim.net.overlay.dht.stealth.ServicePeer;

public class JoinAndPassEvent extends sim.net.overlay.dht.events.repeatable.JoinAndPassEvent {

	public static JoinAndPassEvent newEvent(Distribution d, int count) {
		JoinAndPassEvent e = (JoinAndPassEvent) Event.newEvent(JoinAndPassEvent.class);
		e.init(d, count);
		return e;
	}

	public static JoinAndPassEvent newEvent(HostSet hosts, Distribution d, int count) {
		JoinAndPassEvent e = (JoinAndPassEvent) Event.newEvent(JoinAndPassEvent.class);
		e.init(d, count);
		e.hosts = hosts;
		return e;
	}

	@Override
	public void run() throws Exception {

		HostSet joined = getJoinedPeers().getType(ServicePeer.class);
		HostSet notjoined = getNotJoinedPeers();

		/*
		if (!joined.isEmpty() && !notjoined.isEmpty()) {
			Host h0 = joined.getRandom();
			DHTInterface p1 = (DHTInterface) notjoined.getRandom();

			Events.addNow(JoinEvent.newEvent(p1, h0.getAddress()));
		}
		*/

		if (!notjoined.isEmpty()) {

			Host h1 = notjoined.getRandom();

			// Do special first join
			if (joined.isEmpty()) {
				Events.addNow(JoinEvent.newEvent((DHTInterface) h1, Host.INVALID_ADDRESS));
			} else {
				Host h0 = joined.getRandom();
				DHTInterface p1 = (DHTInterface) h1;
				Events.addNow(JoinEvent.newEvent(p1, h0.getAddress()));
			}
		}

		reschedule();
	}

}
