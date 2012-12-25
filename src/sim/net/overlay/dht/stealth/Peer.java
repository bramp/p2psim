package sim.net.overlay.dht.stealth;

import java.util.Iterator;

import sim.main.Global;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.NodeAddressPair;
import sim.net.overlay.dht.NodeAddressPairs;
import sim.net.overlay.dht.TestMessage;
import sim.net.overlay.dht.pastry.GetMessage;
import sim.net.overlay.dht.pastry.GetReplyMessage;
import sim.net.overlay.dht.pastry.LeafSet;
import sim.net.overlay.dht.pastry.PutMessage;

/**
 * Stealth DHT Node's base class. This bases the {@link StealthPeer} and {@link ServicePeer}
 *
 * @author Andrew Brampton
 *
 */
public abstract class Peer extends sim.net.overlay.dht.pastry.Peer {

	/**
	 * Different constants to describe how the recovery is done
	 */

	/**
	 * Should we use Piggybacking
	 */
	public static boolean USE_RECOVERY_PIGGYBACK = true;

	/**
	 * How many NodeAddressPairs should be piggybacked at most (per message)
	 */
	public static int PIGGYBACK_COUNT = 15;

	/**
	 * Make nodes perodically rejoin
	 */
	public static boolean USE_RECOVERY_REJOIN = false;

	/**
	 * How often should we rejoin the network (for recovery)
	 */
	public static int REJOIN_EVERY = 300 * 1000;

	/**
	 * Should we poll for NodeAddressPairs
	 */
	public static boolean USE_RECOVERY_POLLING = false;

	/**
	 * How often should we poll for NodeAddressPairs
	 */
	public static int POLLING_EVERY = 100 * 1000;

	// Can't have both on (due to a single reaccuring event in stealthdht)
	{ assert (USE_RECOVERY_POLLING & USE_RECOVERY_REJOIN) == false; }

	/**
	 * Should stealth nodes use the first routing row only?
	 */
	public static final boolean USE_FIRST_ROW_ONLY = true;

	// does this node misbehave, given the chance?
	public boolean malicious = false;

	public Peer(int address) {
		super(address);
	}

	public Peer(int address, long nodeID) {
		super(address, nodeID);
	}

	// DEBUG Method, checks if the ID is a NormalPeer
	protected void routingTableSanityCheck(long ID, int address) {
		// Look in the Global Hosts list
		Iterator<Host> i = Global.hosts.getType(ServicePeer.class).iterator();
		while (i.hasNext()) {
			if (i.next().getAddress() == address)
				return;
		}

		throw new RuntimeException("Node (" + this + ") is adding non ServicePeer (" + Peer.toString(ID, true) + ", " + Host.toString(address) + ") to its RoutingTable");
	}

	@Override
	protected NodeAddressPair addToRoutingTable(long ID, int address) {
		if (Global.debug_extra_sanity)
			routingTableSanityCheck(ID, address);

		return super.addToRoutingTable(ID, address);
	}

	@Override
	protected NodeAddressPairs addToRoutingTable(Iterable<NodeAddressPair> pairs) {
		Iterator<NodeAddressPair> i = pairs.iterator();
		while (i.hasNext()) {
			NodeAddressPair p = i.next();
			routingTableSanityCheck(p.nodeID, p.address);
		}

		return super.addToRoutingTable(pairs);
	}

	protected void fastJoinRoutingTable(HostSet peers) {
		// create a routing table for this peer
		final double powb = Math.pow(2,b);
		// number of entries is based on the average number of hops for
		// the existing network size * size of a row
		int entries = (int)Math.ceil(powb * (Math.log(peers.size()) / Math.log(powb)));
		//int entries = peers.size() / 4;

		// Ensure that if we have a minimum number of peers
		if (entries < 16)
			entries = 16;

		// Entries can't be larger than peer.size
		if (entries > peers.size())
			entries = peers.size();

		// Remove all but the interesting peers
		while (peers.size() > entries) {
			peers.remove(peers.getRandom());
		}

		// Now add them all to our table
		Iterator<Host> i = peers.iterator();
		while (i.hasNext()) {
			Host h = i.next();
			DHTInterface p = (DHTInterface)h;
			int delay = getUnicastDelay(h.getAddress()) * 2;
			addToRoutingTable(p.getID(), h.getAddress(), delay);
		}

	}

	protected void fastJoinLeafSet(HostSet peers) {

		Iterator<Host> i;
		LeafSet templeaf = new LeafSet(nodeID, l);

		// Now just add ALL peers to the temp leafset, so we can find the 16 matchs
		i = peers.iterator();
		while (i.hasNext()) {
			Host h = i.next();
			DHTInterface p = (DHTInterface)h;
			NodeAddressPair pair = new NodeAddressPair(p.getID(), h.getAddress());
			pair.rtt = getUnicastDelay(h.getAddress()) * 2;
			templeaf.add(pair);
		}

		// Now add the correct leafset
		addToRoutingTable( templeaf.getSet() );
	}

	@Override
	protected long testMessageSanityCheck(Message msg) {

		// determine if this peer is the closest known to the message's address
		Iterator<Host> i;

		// If this is a Request, only NormalPeers should receive it
		if (msg instanceof GetReplyMessage)
			return msg.toID;
		else if (msg instanceof GetMessage || msg instanceof PutMessage || msg instanceof TestMessage)
			i = Global.hosts.getType(ServicePeer.class).iterator();
		// Any other packet can end up anywhere
		else
			i = Global.hosts.getType(Peer.class).iterator();

		// acquire global knowledge of all existing ids
		// TODO: change this so it isn't done on demand, if
		// it's a common operation
		NodeAddressPairs existingIDs = new NodeAddressPairs();

		while(i.hasNext()) {
			Peer p = (Peer)i.next();
			if (!p.hasFailed() && p.hasJoined())
				existingIDs.add(new NodeAddressPair(p.nodeID));
		}

		long correctID = existingIDs.findNumClosest(msg.toID).nodeID;

		/* // Print out a list of hosts and how far they are away
		if (correctID != nodeID) {
			Iterator<NodeAddressPair> ii = existingIDs.iterator();
			while (ii.hasNext()) {
				NodeAddressPair p = ii.next();
				Global.trace.println(LogLevel.ERR, p + " " + Math.abs( p.nodeID - msg.toID ) );
			}
		}
		*/
		return correctID;
	}
}
