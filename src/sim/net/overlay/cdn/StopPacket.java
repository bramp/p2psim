/**
 *
 */
package sim.net.overlay.cdn;

import sim.net.Packet;

/**
 *
 * Packet sent from a client to a server to stop some kind of service
 *
 * @author Andrew Brampton
 *
 */
public class StopPacket extends Packet {

	int request;

	/**
	 * A packet to stop some service
	 * @param from  Source address
	 * @param to	Destination address
	 * @param request	The number identifying this request
	 * @return
	 */
	public static StopPacket newPacket(int from, int to, int request) {
		StopPacket p = (StopPacket) Packet.newPacket(StopPacket.class);
		p.init(from, to, null);

		p.request = request;
		return p;
	}

}
