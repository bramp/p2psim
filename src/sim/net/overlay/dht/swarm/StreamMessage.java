/*
 * Created on 04-May-2005
 */
package sim.net.overlay.dht.swarm;

import sim.net.overlay.dht.Message;

/**
 * @author Andrew Brampton
 */
public class StreamMessage extends Message {

	public int packetID;
	public int OPlane;

	/**
	 * @param fromAddress
	 * @param fromID
	 * @param toID
	 */
	public StreamMessage(int fromAddress, long fromID, long toID, int OPlane, int size, int packetID) {
		super(fromAddress, fromID, toID);
		this.OPlane = OPlane;
		this.size = size;
		this.packetID = packetID;
	}

	@Override
	protected String _toString() {
		return "OPlane: " + OPlane + ", ID: " + packetID;
	}
}
