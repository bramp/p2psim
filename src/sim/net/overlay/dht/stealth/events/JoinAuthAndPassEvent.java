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

/**
 * Joins via a AuthServer
 * @author Andrew Brampton
 *
 */
public class JoinAuthAndPassEvent extends sim.net.overlay.dht.events.repeatable.JoinAndPassEvent {

	public static JoinAuthAndPassEvent newEvent(Distribution d, int count) {
		JoinAuthAndPassEvent e = (JoinAuthAndPassEvent) Event.newEvent(JoinAuthAndPassEvent.class);
		e.init(d, count);
		return e;
	}

	public static JoinAuthAndPassEvent newEvent(HostSet hosts, Distribution d, int count) {
		JoinAuthAndPassEvent e = (JoinAuthAndPassEvent) Event.newEvent(JoinAuthAndPassEvent.class);
		e.init(d, count);
		e.hosts = hosts;
		return e;
	}

	@Override
	public void run() throws Exception {

		HostSet joined = getAuthGateways().getType(ServicePeer.class);
		HostSet notjoined = getNotJoinedPeers();

		if (!joined.isEmpty() && !notjoined.isEmpty()) {
			Host h0 = joined.getRandom();
			DHTInterface p1 = (DHTInterface) notjoined.getRandom();

			Events.addNow(JoinEvent.newEvent(p1, h0.getAddress()));
		}

		reschedule();
	}

}
