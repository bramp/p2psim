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
public class PausePacket extends Packet {

	int request;

	/**
	 * A packet to stop some service
	 * @param from  Source address
	 * @param to	Destination address
	 * @param request	The number identifying this request
	 * @return
	 */
	public static PausePacket newPacket(int from, int to, int request) {
		PausePacket p = (PausePacket) Packet.newPacket(PausePacket.class);
		p.init(from, to, null);

		p.request = request;
		return p;
	}

}
