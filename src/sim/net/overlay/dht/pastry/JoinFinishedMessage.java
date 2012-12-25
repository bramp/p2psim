/*
 * Created on Feb 28, 2005
 */
package sim.net.overlay.dht.pastry;

import sim.main.Global;
import sim.net.overlay.dht.authentication.AuthData;

/**
 * Sent whenever the last node on the route replys to a joinMessage
 * This or a collisionMessage is sent
 * @author Andrew Brampton
 */
public class JoinFinishedMessage extends PairsMessage {

	/**
	 * @param fromAddress
	 * @param fromID
	 * @param toID
	 */
	public JoinFinishedMessage(int fromAddress, long fromID, long toID) {
		super(fromAddress, fromID, toID);
	}

	public void addAuth(AuthData auth) {
		if (Global.auth_check_join)
			super.addAuth(auth);
	}

}
