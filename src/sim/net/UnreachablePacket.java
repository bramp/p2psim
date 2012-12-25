package sim.net;

public class UnreachablePacket extends ErrorPacket {

	/**
	 * @param from
	 * @param to
	 * @param data
	 */
	public static UnreachablePacket newPacket(int from, int to, Packet data) {
		UnreachablePacket p = (UnreachablePacket) Packet.newPacket(UnreachablePacket.class);
		p.init(from, to, data);
		return p;
	}

}
