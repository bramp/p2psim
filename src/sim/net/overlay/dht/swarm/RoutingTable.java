/**
 *
 */
package sim.net.overlay.dht.swarm;

import java.util.Iterator;

import sim.net.overlay.dht.NodeAddressPair;
import sim.net.overlay.dht.NodeAddressPairs;

/**
 * @author Andrew Brampton
 *
 */
public class RoutingTable extends sim.net.overlay.dht.pastry.RoutingTable {

	/**
	 * Set this to use the Swarm Capability values
	 */
	public static final boolean USECAPABILITY = true;

	/**
	 * @param nodeID
	 * @param b
	 * @param idBits
	 * @param cl
	 */
	public RoutingTable(long nodeID, int b, int idBits, Class<?> cl) {
		super(nodeID, b, idBits, cl);
	}


	@Override
	public NodeAddressPair findClosest(long ID, NodeAddressPairs pairs) {
		if (!USECAPABILITY)
			return super.findClosest(ID, pairs);

		return findCapClosest(pairs);
	}


	/**
	 * @param pairs
	 * @return
	 */
	public static NodeAddressPair findCapClosest(NodeAddressPairs pairs) {

		if (pairs.isEmpty())
			return null;

		NodeAddressPair smallest = pairs.first();
		double smallestval = smallest.rtt * (Double)smallest.oob;

		//Iterator them all looking for the one with the lowest delay
		Iterator<NodeAddressPair> i = pairs.iterator();
		while (i.hasNext()) {
			NodeAddressPair next = i.next();
			double nextval = next.rtt * (Double)next.oob;
			if (smallestval > nextval) {
				smallest = next;
				smallestval = nextval;
			}
		}

		return smallest;
	}

}
