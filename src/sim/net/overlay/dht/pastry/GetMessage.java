/**
 *
 */
package sim.net.overlay.dht.pastry;

import sim.main.Global;
import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.authentication.AuthData;

/**
 * @author macquire
 *
 */
public class GetMessage extends Message {
	/*
	 * Accessing objects: Application-specific objects can be looked up,
	 * contacted, or retrieved by routing a Pastry message, using the
	 * objId as the key. By definition, the message is guaranteed to reach
	 * a node that maintains a replica of the requested object unless all
	 * k nodes with nodeIds closest to the objId have failed.
	 */

	/**
	 * @param fromAddress
	 * @param fromID
	 * @param hash the hash of the value being got from the dht
	 */
	public GetMessage(int fromAddress, long fromID, long hash) {
		super(fromAddress, fromID, hash);
	}

	/* (non-Javadoc)
	 * @see sim.net.overlay.dht.Message#setAuth(sim.net.overlay.dht.authentication.AuthData)
	 */
	@Override
	public void addAuth(AuthData auth) {
		if (Global.auth_check_get)
			super.addAuth(auth);
	}
}
