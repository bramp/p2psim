package sim.net.multicast;

import sim.net.Packet;

public class MulticastPacket extends Packet {

	/**
	 * @param from
	 * @param to
	 */
	public static MulticastPacket newPacket(int from, int to) {
		MulticastPacket p = (MulticastPacket) Packet.newPacket(MulticastPacket.class);
		p.init(from, to, null);
		return p;
	}

	public MulticastPacket duplicate() {
		// NB this ignores any contained data at the moment!
		MulticastPacket dupe = newPacket(this.from,this.to);
		return dupe;
	}
}