/**
 *
 */
package sim.net.overlay.dht.authentication;

import sim.net.overlay.dht.PeerData;
import sim.net.overlay.dht.pastry.GetReplyMessage;


/**
 * @author Andrew Brampton
 *
 */
public class GetAuthReplyMessage extends GetReplyMessage {

	/**
	 * @param fromAddress
	 * @param fromID
	 * @param toID
	 * @param data
	 */
	public GetAuthReplyMessage(int fromAddress, long fromID, long toID, PeerData data, long requestID) {
		super(fromAddress, fromID, toID, data,requestID);
	}

}
