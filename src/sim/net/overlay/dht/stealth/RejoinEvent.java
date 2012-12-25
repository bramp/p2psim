/**
 *
 */
package sim.net.overlay.dht.stealth;

import static sim.stats.StatsObject.SEPARATOR;
import sim.events.Event;
import sim.events.Events;
import sim.main.Global;
import sim.net.overlay.dht.NodeAddressPair;
import sim.net.overlay.dht.pastry.JoinMessage;

public class RejoinEvent extends Event {

	private StealthPeer p;

	public static RejoinEvent newEvent(StealthPeer p) {
		RejoinEvent e = (RejoinEvent) Event.newEvent(RejoinEvent.class);
		e.init(false);
		e.p = p;
		return e;
	}

	@Override
	public void run() throws Exception {

		// only do this if we are joined
		if (p.hasJoined() && !p.hasFailed()) {

			Global.stats.logCount("DHT" + SEPARATOR + "RejoinEvent");

			NodeAddressPair joinp = p.allpairs.random();
			if (joinp != null) {
				JoinMessage j = new JoinMessage(p.getAddress(), p.nodeID);
				j.critial = false;
				p.send(joinp.address, j);
			}

			// And reschedule
			Events.addFromNow(this, Peer.REJOIN_EVERY);
		} else {
			// Remove the reaccuring event (because we will be deleted
			// since we didn't reschedule)
			p.reaccuringEvent = null;
		}
	}

	@Override
	public long getEstimatedRunTime() {
		return Peer.REJOIN_EVERY;
	}
}