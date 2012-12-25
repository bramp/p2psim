/*
 * Created on Feb 28, 2005
 */
package sim.net.overlay.dht.pastry;

import sim.main.Global;
import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.authentication.AuthData;

/**
 * @author Andrew Brampton
 */
public class CollisionMessage extends Message {

	/**
	 * @param fromAddress
	 * @param fromID
	 * @param toID
	 */
	public CollisionMessage(int fromAddress, long fromID, long toID) throws Exception {
		super(fromAddress, fromID, toID);
		if (fromID != toID)
			throw new Exception("Collision Message sent with mismatching IDs");
	}

	@Override
	protected String _toString() {
		return "from " + fromAddress;
	}

	public void addAuth(AuthData auth) {
		if (Global.auth_check_join)
			super.addAuth(auth);
	}
}
