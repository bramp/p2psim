/*
 * Created on 13-Feb-2005
 */
package sim.net.overlay.dht.pastry;

import sim.main.Global;
import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.authentication.AuthData;

/**
 * @author Andrew Brampton
 */
public class JoinMessage extends Message {

	/**
	 * @param fromID
	 */
	public JoinMessage(int fromAddress, long fromID) {
		super(fromAddress, fromID, fromID);
	}

	/* (non-Javadoc)
	 * @see sim.net.overlay.dht.Message#setAuth(sim.net.overlay.dht.authentication.AuthData)
	 */
	@Override
	public void addAuth(AuthData auth) {
		if (Global.auth_check_join)
			super.addAuth(auth);
	}
}
