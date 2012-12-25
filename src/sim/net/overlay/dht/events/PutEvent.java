/**
 *
 */
package sim.net.overlay.dht.events;

import sim.events.Event;
import sim.main.Global;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.PeerData;
import sim.stats.StatsObject;

/**
 * Causes a given peer to attempt to route a put message in the
 * DHT, resulting in the PutMessage's encapsulated data being stored
 * @author macquire
 */
public class PutEvent extends Event {
	public DHTInterface peer;
	public PeerData data;

	public static PutEvent newEvent(DHTInterface peer, PeerData data) {
		PutEvent e = (PutEvent) Event.newEvent(PutEvent.class);
		e.peer = peer;
		e.data = data;
		return e;
	}

	@Override
	public void run() {
		Global.stats.logCount("DHT" + StatsObject.SEPARATOR + "PutEvent");
		peer.put(data);
	}

	@Override
	public long getEstimatedRunTime() {
		return 1;
	}
}
