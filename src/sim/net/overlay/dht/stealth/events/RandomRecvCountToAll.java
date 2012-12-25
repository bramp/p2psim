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

/**
 * Sends TestMessage from a random Stealth node to a random Service Node
 * Can be made to send messages directly to Service, or to a random ID
 * @author Andrew Brampton
 *
 */
public class RandomRecvCountToAll extends sim.net.overlay.dht.events.repeatable.RandomRecvCountToAll {

	static final int DEFAULT_DELAY = 10000; // The time between each event

	public static RandomRecvCountToAll newEvent(int msgCount, boolean direct) {
		RandomRecvCountToAll e = (RandomRecvCountToAll) Event.newEvent(RandomRecvCountToAll.class);
		e.init(msgCount, direct, TestMessage.class);
		return e;
	}

	@Override
	public void run() throws Exception {
		HostSet joined = getJoinedPeers();

		HostSet joinedSender = joined.getType(sim.net.overlay.dht.pastry.Peer.class);

		// Check that we have enough hosts to play with
		if (!joinedSender.isEmpty()) {

			Host h0 = joinedSender.getRandom();
			long toID = DHTInterface.INVALID_ID;

			if (direct) {

				HostSet joinedNormal = joined.getType(sim.net.overlay.dht.pastry.Peer.class);

				// Check that there is atleast 1 other node to send to
				if (!joinedNormal.isEmpty()) {
					Host h1 = joinedNormal.getRandom();
					toID = ((DHTInterface)h1).getID();
				}

			} else {
				toID = Global.rand.nextLong();
			}

			if (DHTInterface.INVALID_ID != toID) {
				DHTInterface p0 = (DHTInterface)h0;

				Object params[] = {h0.getAddress(), p0.getID(), toID};
				Message m = constructor.newInstance( params );

				Event e = RecvEvent.newEvent(p0, m);
				Events.addNow(e);
			}
		}

		// decrease count and recurse
		reschedule();
	}

}
