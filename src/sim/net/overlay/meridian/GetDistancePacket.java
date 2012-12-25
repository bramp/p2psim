/**
 *
 */
package sim.net.overlay.meridian;

import sim.net.Packet;

/**
 * Asks a remote node the distance between it and another node
 *
 * @author Andrew Brampton
 *
 */
public class GetDistancePacket extends Packet {

	/**
	 * The host ID of the other node (that we want to get the distance to)
	 */
	public int other;

	/**
	 * @param from The source of this packet
	 * @param to The destination of this packet
	 * @param other The node we want to work out the distance to
	 */
	public static GetDistancePacket newPacket(int from, int to, int other) {
		GetDistancePacket p = (GetDistancePacket) Packet.newPacket(GetDistancePacket.class);
		p.init(from, to, null);
		p.other = other;
		return p;
	}

}
