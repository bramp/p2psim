package sim.net;


/**
 * Class to represent a error has occured on the network
 * @author Andrew Brampton
 *
 */
abstract public class ErrorPacket extends Packet {

	public static ErrorPacket newPacket(int from, int to, TrackableObject data) {
		ErrorPacket p = (ErrorPacket) Packet.newPacket(ErrorPacket.class);
		p.init(from, to, data);
		return p;
	}

}
