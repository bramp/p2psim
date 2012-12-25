/**
 *
 */
package sim.net.overlay.dht.events;

import sim.events.Event;
import sim.main.Global;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.pastry.PeerBase;
import sim.stats.StatsObject;

/**
 * Causes a given peer to attempt to retrieve data from the DHT, based
 * on the hash to which the GetMessage is sent
 * @author macquire
 */
public class GetEvent extends Event {
	public DHTInterface peer;
	public long hash;

	public static GetEvent newEvent(DHTInterface peer, String key) {
		return newEvent(peer,PeerBase.hash(key));
	}

	public static GetEvent newEvent(DHTInterface peer, long hash) {
		GetEvent e = (GetEvent) Event.newEvent(GetEvent.class);
		e.peer = peer;
		e.hash = hash;
		return e;
	}

	@Override
	public void run() {
		Global.stats.logCount("DHT" + StatsObject.SEPARATOR + "GetEvent");
		peer.get(hash);
	}

	@Override
	public long getEstimatedRunTime() {
		return 1;
	}
}
