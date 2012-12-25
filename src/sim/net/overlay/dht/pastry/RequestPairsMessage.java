/*
 * Created on 26-Jun-2005
 */
package sim.net.overlay.dht.pastry;

import sim.net.overlay.dht.Message;

public class RequestPairsMessage extends Message {

	public RequestPairsMessage(int fromAddress, long fromID, long toID) {
		super(fromAddress, fromID, toID);
	}

}
