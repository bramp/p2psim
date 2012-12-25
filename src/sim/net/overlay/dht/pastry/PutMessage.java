package sim.net.overlay.dht.pastry;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import sim.main.Global;
import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.PeerData;
import sim.net.overlay.dht.authentication.AuthData;

/**
 * @author macquire
 *
 */
public class PutMessage extends Message {
	/*
	 * Inserting objects: Application-specific objects can be inserted by
	 * routing a Pastry message, using the objId as the key. When the message
	 * reaches a node with one of the k closest nodeIds to the objId, that node
	 * replicates the object among the other k-1 nodes with closest nodeIds
	 * (which are, by definition, in the same leaf set for k <= L/2).
	 */

	private List<PeerData> data;

	public PutMessage(int fromAddress, long fromID, PeerData data) {
		this(fromAddress,fromID,data.getHash(),data);
	}

	// allows a PutMessage to be sent to a node other than the closest to the PeerData's hash
	public PutMessage(int fromAddress, long fromID, long toID, PeerData data) {
		super(fromAddress, fromID, toID, data.getSize());
		this.data = new ArrayList<PeerData>();

		this.data.add(data);
	}

	public PutMessage(int fromAddress, long fromID, long toID, List<PeerData> data) {
		super(fromAddress, fromID, toID, sumSize(data));
		this.data = data;
	}

	private static int sumSize(List<PeerData> data) {
		int i = 0;

		Iterator<PeerData> ii = data.iterator();

		while(ii.hasNext()) {
			i += ii.next().getSize();
		}

		return i;
	}

	public List<PeerData> getData() {
		return data;
	}

	@Override
	protected String _toString() {
		return data.toString();
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
		if (Global.auth_check_put)
			super.addAuth(auth);
	}
}
