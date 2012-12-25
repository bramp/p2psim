/**
 *
 */
package sim.net.overlay.dht.pastry;

import sim.main.Global;
import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.PeerData;
import sim.net.overlay.dht.authentication.AuthData;

/**
 * @author macquire
 *
 */
public class GetReplyMessage extends Message {

	private PeerData data;
    public long requestID;

	public GetReplyMessage(int fromAddress, long fromID, long toID, PeerData data, long requestID) {
		super(fromAddress, fromID, toID, data != null ? data.getSize() : 0);
		this.data = data;
		this.requestID = requestID;
	}

	public PeerData getData() {
		return data;
	}

	/* (non-Javadoc)
	 * @see sim.net.overlay.dht.Message#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		data = null;
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
