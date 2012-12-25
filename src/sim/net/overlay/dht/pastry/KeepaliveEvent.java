/**
 *
 */
package sim.net.overlay.dht.pastry;

import sim.events.Event;
import sim.events.Events;

class KeepaliveEvent extends Event {

	private Peer p;

	public static KeepaliveEvent newEvent(Peer p) {
		KeepaliveEvent e = (KeepaliveEvent) Event.newEvent(KeepaliveEvent.class);
		e.init(false);
		e.p = p;
		return e;
	}

	@Override
	public void run() throws Exception {
		p.sendKeepAlives();

		// And reschedule
		Events.addFromNow(this, Peer.KEEP_ALIVE_EVERY / 2);
	}

	@Override
	public long getEstimatedRunTime() {
		return Peer.KEEP_ALIVE_EVERY;
	}
}