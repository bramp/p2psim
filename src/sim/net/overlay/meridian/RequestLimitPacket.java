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
public class RequestLimitPacket extends Packet {


	/**
	 * @param from
	 * @param to

	 */
	public static RequestLimitPacket newPacket(int from, int to) {
		RequestLimitPacket p = (RequestLimitPacket) Packet.newPacket(RequestLimitPacket.class);
		p.init(from, to, null);

		return p;
	}

}
