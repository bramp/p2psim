/**
 *
 */
package sim.net.overlay.dht.pastry;

import sim.net.overlay.dht.PeerData;

/**
 * @author idris
 *
 */
public class GetReplyProxyMessage extends GetReplyMessage {

	public GetReplyProxyMessage(int fromAddress, long fromID, long toID, PeerData data, long requestID) {
		super(fromAddress, fromID, toID, data, requestID);
	}

}
