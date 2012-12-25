/**
 *
 */
package sim.net.overlay.meridian;

import sim.net.Packet;

/**
 *
 * Returns the distance between this node and a remote node
 *
 * @author Andrew Brampton
 *
 */
public class GetDistancePacketReply extends Packet {

	/**
	 * The host ID of the remote node (that we want to get the distance to)
	 */
	public int other;

	/**
	 * The distance to the remote node
	 */
	public int distance;

	/**
	 * @param from
	 * @param to
	 */
	public static GetDistancePacketReply newPacket(int from, int to, int distance) {
		GetDistancePacketReply p = (GetDistancePacketReply) Packet.newPacket(GetDistancePacketReply.class);
		p.init(from, to, null);
		p.distance = distance;
		return p;
	}
}
