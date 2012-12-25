package sim.net.overlay.dht.stealth.events;

import sim.events.Event;
import sim.events.Events;
import sim.main.Global;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.TestMessage;
import sim.net.overlay.dht.events.RecvEvent;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.StealthPeer;

public class RandomRecvCountPerPeer extends sim.net.overlay.dht.events.repeatable.RandomRecvCountPerPeer {

	/**
	 * @param msgCount
	 * @param direct
	 */
	public static RandomRecvCountPerPeer newEvent(int msgCount, boolean direct) {
		RandomRecvCountPerPeer e = (RandomRecvCountPerPeer) Event.newEvent(RandomRecvCountPerPeer.class);
		e.init(msgCount, direct, TestMessage.class);
		return e;
	}

	@Override
	public void run() throws Exception {

		if (i == null) // The list of peers sending
			i = Global.hosts.getType(StealthPeer.class).iterator();

		if (i.hasNext()) {
			// The list of peers recving
			HostSet peers = Global.hosts.getType(ServicePeer.class);
			Host h0 = i.next();
			DHTInterface p0 = (DHTInterface) h0;

			if (!h0.hasFailed()) {
				for(int i=0; i<msgCount; i++) {
					long nodeID;

					if (direct) {
						Host h1 = peers.getRandom();

						// TODO: improve this search for unfailed nodes
						while(h1.hasFailed() || h0 == h1) {
							h1 = peers.getRandom();
						}

						DHTInterface p1 = (DHTInterface)h1;
						nodeID = p1.getID();
					}
					else {
						nodeID = Global.rand.nextLong();
					}

					Object params[] = {h0.getAddress(), p0.getID(), nodeID};
					Message m = constructor.newInstance( params );

					Event e = RecvEvent.newEvent(p0, m);
					Events.addNow(e);
				}
			}
		}

		reschedule();
	}
}
