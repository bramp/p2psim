/*
 * Created on 22-Mar-2005
 */
package sim.net;

/**
 * @author Andrew Brampton
 */
public class PingPacket extends Packet {

	/**
	 * @param from
	 * @param to
	 */
	public static PingPacket newPacket(int from, int to) {
		PingPacket p = (PingPacket) Packet.newPacket(PingPacket.class);
		p.init(from, to, null);
		return p;
	}

}
