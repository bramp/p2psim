package sim.net.multicast;

import sim.net.Packet;

public class MulticastJoinPacket extends Packet {

	/**
	 * @param from
	 * @param to
	 */
	public static MulticastJoinPacket newPacket(int from, int to) {
		MulticastJoinPacket p = (MulticastJoinPacket) Packet.newPacket(MulticastJoinPacket.class);
		p.init(from, to, null);
		return p;
	}
}
