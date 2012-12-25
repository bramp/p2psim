package sim.net.overlay.dht.events.repeatable;

import sim.events.Event;
import sim.events.Events;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.HostSet;
import sim.net.overlay.dht.PeerData;
import sim.net.overlay.dht.events.PutEvent;
import sim.net.overlay.dht.pastry.Peer;

public class PutAndPassEvent extends RepeatableHelperEvent {
	static final int DEFAULT_DELAY = 10000; // The time between each event

	private int k;

	public static PutAndPassEvent newEvent(int count) {
		// no replication
		return newEvent(count,1);
	}

	public static PutAndPassEvent newEvent(int count,int k) {
		return newEvent(new Constant(DEFAULT_DELAY), count, k);
	}

	public static PutAndPassEvent newEvent(Distribution d, int count) {
		// no replication
		return newEvent(d,count,1);
	}

	public static PutAndPassEvent newEvent(Distribution d, int count, int k) {
		PutAndPassEvent e = (PutAndPassEvent) Event.newEvent(PutAndPassEvent.class);
		e.init(d,count);
		e.k = k;
		return e;
	}

	@Override
	public void run() throws Exception {
		HostSet peers = getJoinedPeers();

		if (!peers.isEmpty()) {
			Peer p0 = (Peer)peers.getRandom();
			Events.addNow(PutEvent.newEvent(p0,PeerData.newContent(k)));
		}

		reschedule();
	}
}
