/**
 *
 */
package sim.net.overlay.cdn;

import sim.net.Packet;

/**
 *
 * Packet sent from a server to a client containing some kind of data
 *
 * @author Andrew Brampton
 *
 */
public class MediaPacket extends Packet {

	/**
	 * The ID of the media
	 */
	public int media;

	/**
	 * The start time (in bytes)
	 */
	public long start;

	/**
	 * The end time (in bytes)
	 */
	public long end;

	/**
	 * The request id the client assigned
	 */
	public int request;

	/**
	 * A packet to request some service
	 * @param from  Source address
	 * @param to	Destination address
	 * @param media	ID of the media being requested
	 * @param start The start time (in bytes)
	 * @param end	The end time (in bytes)
	 * @return
	 */
	public static MediaPacket newPacket(int from, int to, int request, int media, long start, long end) {

		assert media >= 0;
		assert start >= 0;
		assert end > start;

		MediaPacket p = (MediaPacket) Packet.newPacket(MediaPacket.class);
		p.init(from, to, null);
		p.size += (end - start);
		p.request = request;
		p.media = media;
		p.start = start;
		p.end = end;

		// Check we are larger than 0, and less than 100mb
		assert p.size > 0 && p.size < 100000000;

		return p;
	}

	@Override
	public String _toString() {
		return super._toString() + " media:" + media + " start:" + start + " end:" + end;
	}
}
