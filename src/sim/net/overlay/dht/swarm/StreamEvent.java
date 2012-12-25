/*
 * Created on 04-May-2005
 */
package sim.net.overlay.dht.swarm;

import sim.events.Event;
import sim.events.EventException;
import sim.events.Events;

/**
 * @author Andrew Brampton
 */
public class StreamEvent extends Event {

	public int packetCount = 0;

	public final SwarmPeer peer;
	public final int OPlane;
	public final int rate;
	public final int duration;

	/**
	 *
	 * @param peer The peer to send the stream
	 * @param OPlane The OPlane to send on
	 * @param rate The rate in bytes/second
	 * @param duration The duration of the stream
	 * @throws EventException
	 */
	public StreamEvent(SwarmPeer peer, int OPlane, int rate, int duration) {
		this.peer = peer;
		this.OPlane = OPlane;
		this.rate = rate;
		this.duration = duration;
	}

	/* (non-Javadoc)
	 * @see net.bramp.p2psim.events.Event#run()
	 */
	@Override
	public void run() {
		//Send a packet of this stream
		peer.sendStreamPacket(OPlane, null, rate, packetCount++);

		// Reschedule ourselfs
		if ((Events.getTime() - time) < duration) {
			Events.addFromNow(this, 1000);
		}

	}

	@Override
	public long getEstimatedRunTime() {
		return duration;
	}

}
