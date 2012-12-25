/**
 *
 */
package sim.net.overlay.dht.authentication;

import sim.net.overlay.dht.pastry.GetMessage;

/**
 * @author Andrew Brampton
 *
 */
public class GetAuthMessage extends GetMessage {

	/**
	 * @param fromAddress
	 * @param fromID
	 * @param hash
	 */
	public GetAuthMessage(int fromAddress, long fromID, long hash) {
		super(fromAddress, fromID, hash);
	}

}
