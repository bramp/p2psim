/*
 * Created on 07-Mar-2005
 */
package sim.net.overlay.dht;

import java.util.Iterator;

import sim.collections.NumberSet;


/**
 * @author Andrew Brampton
 */
public class NodeAddressPairs extends NumberSet<NodeAddressPair> {

	public NodeAddressPairs ( ) {}

	public NodeAddressPairs ( NodeAddressPairs pairs ) {
		this.addAll( pairs );
	}

	/**
	 * Finds the proximetry closest peer
	 * @param ID
	 * @return
	 */
	public NodeAddressPair findProxClosest() {

		if (set.isEmpty())
			return null;

		//Iterator them all looking for the one with the lowest delay
		Iterator<NodeAddressPair> i = iterator();
		NodeAddressPair smallest = i.next();

		while (i.hasNext()) {
			NodeAddressPair next = i.next();
			if (smallest.rtt > next.rtt)
				smallest = next;
		}

		return smallest;
	}


	public boolean contains(long ID) {
		//return findNumClosest(ID).nodeID == ID;
		return contains(new NodeAddressPair(ID));
	}

	public boolean remove(long ID) {
		return remove(new NodeAddressPair(ID));
	}
}
