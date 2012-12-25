package sim.net.overlay.dht.events.repeatable;

import sim.events.Event;
import sim.events.Events;
import sim.main.Global;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.HostSet;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.PeerData;
import sim.net.overlay.dht.events.GetEvent;
import sim.net.overlay.dht.pastry.Peer;

public class GetAndPassEvent extends RepeatableHelperEvent {
	static final int DEFAULT_DELAY = 10000; // The time between each event

	//HostSet hosts;

	public static GetAndPassEvent newEvent(int count) {
		return newEvent(new Constant(DEFAULT_DELAY), count);
	}

	public static GetAndPassEvent newEvent(Distribution d, int count) {
		GetAndPassEvent e = (GetAndPassEvent) Event.newEvent(GetAndPassEvent.class);
		e.init(d, count);
		e.hosts = Global.hosts.getType(DHTInterface.class);
		return e;
	}

	public static GetAndPassEvent newEvent(HostSet hosts, Distribution d, int count) {
		GetAndPassEvent e = (GetAndPassEvent) Event.newEvent(GetAndPassEvent.class);
		e.init(d, count);
		e.hosts = hosts;
		return e;
	}

	@Override
	public void run() throws Exception {
		HostSet peers = getJoinedPeers();

		if (!peers.isEmpty()) {
			Peer p0 = (Peer)peers.getRandom();
			long request;

			if (Global.auth_per_session) {
				if (p0.sessionhack == 0)
					p0.sessionhack = PeerData.getValidID();
				request = p0.sessionhack;
			} else {
				request = PeerData.getValidID();
			}
			Events.addNow(GetEvent.newEvent(p0, request));

		}

		reschedule();
	}
}
