/**
 *
 */
package sim.net.overlay.dht.stealth;

import sim.events.Event;
import sim.events.Events;

public class PollEvent extends Event {

	private StealthPeer p;

	public static PollEvent newEvent(StealthPeer p) {
		PollEvent e = (PollEvent) Event.newEvent(PollEvent.class);
		e.init(false);
		e.p = p;
		return e;
	}

	@Override
	public void run() throws Exception {

		// only do this if we are joined
		if (p.hasJoined() && !p.hasFailed()) {
			p.pollNodes();

			// And reschedule
			Events.addFromNow(this, Peer.POLLING_EVERY);
		} else {
			// Remove the reaccuring event (because we will be deleted
			// since we didn't reschedule
			p.reaccuringEvent = null;
		}
	}

	@Override
	public long getEstimatedRunTime() {
		return Peer.KEEP_ALIVE_EVERY;
	}
}