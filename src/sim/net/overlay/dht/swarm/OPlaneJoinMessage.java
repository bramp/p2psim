/*
 * Created on 21-Mar-2005
 */
package sim.net.overlay.dht.swarm;

import sim.events.Events;
import sim.net.overlay.dht.Message;

/**
 * @author Andrew Brampton
 */
public class OPlaneJoinMessage extends Message {

	public int oPlane = OPlane.INVALIDOPLANE;
	public long sentTime;

	/**
	 * @param fromAddress
	 * @param fromID
	 * @param toID
	 */
	public OPlaneJoinMessage(int fromAddress, long fromID, long toID, int oPlane) {
		super(fromAddress, fromID, toID);
		this.oPlane = oPlane;
		sentTime = Events.getTime();
	}

}
