/*
 * Created on 13-May-2005
 */
package sim.net.overlay.dht.authentication;

import sim.events.Event;
import sim.events.Events;
import sim.math.Distribution;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.events.JoinEvent;

/**
 * TODO This method won't work under churn! (coz hosts won't reflect the failed nodes)
 * Or if the joined peer hasn't actually finished joining before the next guy tries
 * @author Andrew Brampton
 *
 */
public class JoinAndPassEvent extends sim.net.overlay.dht.events.repeatable.JoinAndPassEvent {

	int gateways;
	HostSet joined = new HostSet();

	public void init(Distribution d, int count, int gateways) {
		init(d, count);
		this.gateways = gateways;
	}

	public static JoinAndPassEvent newEvent(Distribution d, int count) {
		JoinAndPassEvent e = (JoinAndPassEvent) Event.newEvent(JoinAndPassEvent.class);
		e.init(d, count, -1);
		return e;
	}

	public static JoinAndPassEvent newEvent(Distribution d, int count, int gateways) {
		JoinAndPassEvent e = (JoinAndPassEvent) Event.newEvent(JoinAndPassEvent.class);
		e.init(d, count, gateways);
		return e;
	}

	public static JoinAndPassEvent newEvent(HostSet hosts, Distribution d, int count) {
		JoinAndPassEvent e = (JoinAndPassEvent) Event.newEvent(JoinAndPassEvent.class);
		e.init(d, count, -1);
		e.hosts = hosts;
		return e;
	}

	@Override
	public void run() throws Exception {

		HostSet notjoined = getNotJoinedPeers();

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

			// If everyone can be a gateway, OR we have fewer joined nodes than gateways
			// add them to the joined list
			if (gateways == -1 || joined.size() < gateways) {
				joined.add(h1);
			}

		}

		reschedule();
	}

}
