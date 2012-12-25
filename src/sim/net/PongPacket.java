/*
 * Created on 22-Mar-2005
 */
package sim.net;

import sim.events.Events;

/**
 * @author Andrew Brampton
 */
public class PongPacket extends Packet {

	public long pingSentTime;

	protected void init(final int from, final int to, final TrackableObject data) {
		super.init(from, to, data);
		size += 8; // add the length of pingSentTime
	}

	/**
	 * Create a {@link PongPacket} in reply to a {@link PingPacket}
	 * @param ping The {@link PingPacket} this is in reply to
	 */
	public static PongPacket newPacket(PingPacket ping) {
		PongPacket p = (PongPacket) Packet.newPacket(PongPacket.class);
		p.init(ping.to, ping.from, ping.data);
		p.pingSentTime = ping.sentTime;
		return p;
	}

	public int getRoundTripTime() {
		return (int) (Events.getTime() - pingSentTime);
	}
}
