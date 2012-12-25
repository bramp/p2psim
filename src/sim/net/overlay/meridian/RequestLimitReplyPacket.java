/**
 *
 */
package sim.net.overlay.meridian;

import sim.net.Packet;

/**
 * Simulates the client requesting some kind of service
 * @author Andrew Brampton
 *
 */
public class RequestLimitReplyPacket extends Packet {

	 int limit;
     int availCapacity;
	/**
	 * @param from
	 * @param to
	 * @param r
	 */
	public static RequestLimitReplyPacket newPacket(int from, int to, int r, int c) {
		RequestLimitReplyPacket p = (RequestLimitReplyPacket) Packet.newPacket(RequestLimitReplyPacket.class);
		p.init(from, to, null);
		p.limit  = r;
	    p.availCapacity = r;

		return p;
	}

}
