/**
 *
 */
package sim.net.overlay.cdn;

import sim.net.Packet;

/**
 *
 * Packet sent from a client to a server to request some kind of service
 *
 * @author Andrew Brampton
 *
 */
public class RequestPacket extends Packet {

	int media;
	long start;
	long end;

	/**
	 * A number (unique to the client) to identify this request
	 */
	int request;

	/**
	 *  Byterate of this request (normally the same as the media's, but can be slower/faster)
	 */
	int byterate;

	/**
	 * A packet to request some service
	 * @param from  Source address
	 * @param to	Destination address
	 * @param r
	 * @return
	 */
	public static RequestPacket newPacket(int from, int to, Request r) {
		RequestPacket p = (RequestPacket) Packet.newPacket(RequestPacket.class);
		p.init(from, to, null);

		p.request = r.requestID;

		p.media = r.media;
		p.start = r.start;
		p.end = r.end;

		p.byterate = r.byterate;

		return p;
	}

	@Override
	public String _toString() {
		return super._toString() + " media:" + media + " start:" + start + " end:" + end;
	}

}
