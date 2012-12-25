package sim.net;

/**
 * Used to represent a packet being dropped in the network
 * @author Andrew Brampton
 *
 */
public class DroppedPacket extends ErrorPacket {

	/**
	 * @param from
	 * @param to
	 * @param data
	 */
	public static DroppedPacket newPacket(int from, int to, Packet data) {
		DroppedPacket p = (DroppedPacket) Packet.newPacket(DroppedPacket.class);
		p.init(from, to, data);
		return p;
	}

}
