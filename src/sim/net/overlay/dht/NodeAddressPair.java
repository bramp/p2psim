/*
 * Created on 15-Feb-2005
 */
package sim.net.overlay.dht;

import sim.net.Host;
import sim.net.overlay.dht.pastry.Peer;


public class NodeAddressPair extends Number implements Comparable<Object>, Cloneable {

	/**
	 * The ID of this Node
	 */
	public final long nodeID;

	/**
	 * The network address of the Node
	 */
	public final int address;

	/**
	 * The delay to this Node
	 */
	public int rtt = Integer.MAX_VALUE;

	/**
	 * The time a ping was last sent
	 */
	public long lastChecked = Long.MIN_VALUE;

	/**
	 * How many consective times a ping has been sent without a reply
	 */
	public int failed = 0;

	public Object oob = null;

	public NodeAddressPair(long nodeID) {
		this(nodeID, Host.INVALID_ADDRESS);
	}

	public NodeAddressPair(long nodeID, int address) {
		this.nodeID = nodeID;
		this.address = address;

		// Sanity check
		/*
		 if (address != Host.INVALID_ADDRESS) {
			Peer p = (Peer) Global.hosts.get(address);
			if (p.getID() != nodeID)
				throw new RuntimeException( "NodeID doesn't match address " + Peer.toString(nodeID, true) + " != " + Peer.toString(p.getID(), true) + " address " + Host.toString(address) );
		}
		*/

		//if (nodeID == 0)
		//	throw new RuntimeException ("Invalid nodeID");

		//if (address == 0)
		//	throw new RuntimeException ("Invalid address");
	}


	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(Object o) {

		long l = 0;

		if (o instanceof NodeAddressPair) {
			l = ((NodeAddressPair)o).nodeID;
		} else if (o instanceof Long) {
			l = (Long)o;
		}

		if (l > nodeID)
			return -1;
		else if (l < nodeID)
			return 1;
		else
			return 0;
	}

	@Override
	public Object clone() {
		try {
			Object ob = super.clone();
			//We don't want to copy delay data
			((NodeAddressPair)ob).rtt = Integer.MAX_VALUE;
			((NodeAddressPair)ob).lastChecked = Integer.MIN_VALUE;
			return ob;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {

		String end = oob == null ? " ?" : " " + Double.toString((Double)oob);

		// If our delay is not set, show no value
		if (rtt == Integer.MAX_VALUE)
			return Peer.toString(nodeID, true) + end;

		return Peer.toString(nodeID, true) + " " + rtt + "ms" + end;
	}

	@Override
	public int intValue() {
		return (int) nodeID;
	}

	@Override
	public long longValue() {
		return nodeID;
	}

	@Override
	public float floatValue() {
		return nodeID;
	}

	@Override
	public double doubleValue() {
		return nodeID;
	}

	@Override
	public int hashCode() {
		return intValue();
	}

}