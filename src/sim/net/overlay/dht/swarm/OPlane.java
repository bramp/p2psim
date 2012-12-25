/*
 * Created on 15-Apr-2005
 */
package sim.net.overlay.dht.swarm;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import sim.net.overlay.dht.DHTInterface;


class OPlane {

	public final static int INVALIDOPLANE = -1;

	public int OPlane = INVALIDOPLANE;

	/**
	 * The ID of this peer's OPlane parent
	 */
	public long parentID = DHTInterface.INVALID_ID;

	public List<Long> receivers = new ArrayList<Long>();

	public OPlane (int OPlane) {
		this.OPlane = OPlane;
	}

	public void addReceiver(long nodeID) {
		if (!receivers.contains( nodeID ))
			receivers.add( nodeID );
	}

	public List<Long> getReceiver() {
		return receivers;
	}

	@Override
	public boolean equals(Object obj) {
		OPlane o = (OPlane)obj;
		return o.OPlane == OPlane;
	}

	@Override
	public String toString() {
		String ret = Integer.toString(OPlane) + " ";
		Iterator<Long> i = receivers.iterator();
		boolean empty = true;

		while (i.hasNext() ) {
			ret += SwarmPeer.toString((i.next()).longValue(), true) + ", ";
			empty = false;
		}

		if (!empty)
			ret = ret.substring(0, ret.length() - 2);

		return ret;
	}
}