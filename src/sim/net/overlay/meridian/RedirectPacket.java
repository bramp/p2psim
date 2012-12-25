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
public class RedirectPacket extends Packet {


    Rings rings;
    int newServer;

	/**
	 * @param from client ID from
	 * @param to the new server
	 * @param sending server
	 */
	public static RedirectPacket newPacket(int from, int to, int server ) {
		RedirectPacket p = (RedirectPacket) Packet.newPacket(RedirectPacket.class);
		p.init(from, to, null);

		p.newServer = server;

		return p;
	}

}
