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
public class RequestPacket extends Packet {

	Rings rings;

	/**
	 * @param from
	 * @param to
	 * @param r
	 */
	public static RequestPacket newPacket(int from, int to, Rings r) {
		RequestPacket p = (RequestPacket) Packet.newPacket(RequestPacket.class);
		p.init(from, to, null);
//		System.out.println("here"+ p.from +"  " + p.to);

		p.rings = r;
		return p;
	}

}
