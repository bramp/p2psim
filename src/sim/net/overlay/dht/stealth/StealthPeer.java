/*
 * Created on 19-May-2005
 */
package sim.net.overlay.dht.stealth;

import static sim.stats.StatsObject.SEPARATOR;
import sim.events.Event;
import sim.events.Events;
import sim.main.Global;
import sim.net.HostSet;
import sim.net.overlay.dht.PeerData;
import sim.net.overlay.dht.TreeTestMessage;
import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.NodeAddressPair;
import sim.net.overlay.dht.NodeAddressPairs;
import sim.net.overlay.dht.pastry.JoinFinishedMessage;
import sim.net.overlay.dht.pastry.PairsMessage;
import sim.net.overlay.dht.pastry.RequestPairsMessage;

import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

public class StealthPeer extends Peer {

	Event reaccuringEvent = null;

	/**
	 * @param address
	 */
	public StealthPeer(int address) {
		super(address);

		init();
	}

	public StealthPeer(int address, long nodeID) {
		super(address, nodeID);

		init();
	}

	protected void init() {
		// Stealth Peers do no use a leafset
		leafSet = null;
	}


	@Override
	protected void leafSetAddAll(NodeAddressPairs newpairs) {
		// We don't have a leafset, so do nothing
	}


	@Override
	public int getRoute(long ID) {

		if (ID == nodeID)
			return address;

		//Check in routing and leaf sets
		NodeAddressPair routePair = routingTable.getRoute(ID);

		if (routePair == null) {
			Global.stats.logCount("DHT" + SEPARATOR + "RouteFailed");
			Trace.println(LogLevel.WARN, this + ": WARNING Unable to find route (ie me Stealth node know no other nodes");
			//return Host.INVALID_ADDRESS; // This should never happen, unless we can't route
			return address;
		}

		return routePair.address;

	}

	/*
	HostSet routingPeers = new HostSet();

	// Pick a node randomly!
	public int getRoute(long ID) {
		if (ID == nodeID)
			return address;

		return routingPeers.getRandom().getAddress();
	}

	// This method only picks 16 peers for this node to know
	@Override
	public void fastJoin(HostSet peers) {

		peers = peers.getType(ServicePeer.class);

		// Add 16 routing peers
		while (routingPeers.size() < 16 && routingPeers.size() != peers.size() ) {
			routingPeers.add( peers.getRandom() );
		}

		if (peers.isEmpty()) {
			// Do the special join
			throw new RuntimeException("Stealth Node can't join itself!");
		}

		joinAddress = routingPeers.getRandom().getAddress();

		setJoined(true);
	}

	*/

	@Override
	public void fastJoin(HostSet peers) {

		peers = peers.getType(ServicePeer.class);

		fastJoinRoutingTable(peers);

		if (peers.isEmpty()) {
			// Do the special join
			throw new RuntimeException("Stealth Node can't join itself!");
		}

		joinAddress = peers.getRandom().getAddress();

		setJoined(true);
	}

	@Override
	public void setJoined(boolean joined) {
		super.setJoined(joined);

		if (joined) {
			// Schedule a Peer Poll, if we are doing that.
			if (USE_RECOVERY_POLLING) {
				if (reaccuringEvent == null) {
					reaccuringEvent = PollEvent.newEvent(this);
					Events.addFromNow(reaccuringEvent, POLLING_EVERY);
				}
			}

			if (USE_RECOVERY_REJOIN) {
				if (reaccuringEvent == null) {
					Event reaccuringEvent = RejoinEvent.newEvent(this);
					Events.addFromNow(reaccuringEvent, REJOIN_EVERY);
				}
			}
		}
	}

	@Override
	public boolean recv(Message msg, boolean forUs) {
		boolean forward = true;

		if (!forUs && msg.fromAddress != address) {
			// We shouldn't be getting messages not destined for us
			Trace.println(LogLevel.ERR, this + ": ERROR StealthPeer peer is routing (" + msg + ")");
			return forward;
		}

		// If there is info pigging backing, use it
		if (USE_RECOVERY_PIGGYBACK && msg.oob != null) {
			NodeAddressPairs pairs = (NodeAddressPairs) msg.oob;
			addToRoutingTable(pairs);
		}

		// If the message is for us, and its the last Join message. Overload
		// what recv does and stop us from replying to the peers
		if (forUs && msg instanceof JoinFinishedMessage) {
			// We are joined, don't spam our routing tables at people
			// We don't want them to remember me

			PairsMessage m = (PairsMessage)msg;

			//Add this host to our routing table
			addToRoutingTable(m.fromID, m.fromAddress);

			//Add any returned entries to our routing table
			addToRoutingTable(m.getPairs());

			setJoined(true);
		} else {
			return super.recv(msg, forUs);
		}

		if (msg instanceof TreeTestMessage) {
			// stealth nodes can only be an origin
			Global.writers.addItem("origin-stealth",this.nodeID);
		}

		return forward;
	}

	/**
	 * Used to poll the network for a list of nodes
	 */
	void pollNodes() {

		Global.stats.logCount("DHT" + SEPARATOR + "PollEvent");

		// Pick a random node to ask
		long toID = Global.rand.nextLong();

		// Send him a request
		Message m = new RequestPairsMessage(address, nodeID, toID);
		m.critial = false;
		route(m);
	}

	/* (non-Javadoc)
	 * @see sim.net.overlay.dht.pastry.Peer#announce()
	 */
	@Override
	protected void announce(String reason) {
		// Do nothing! We never announce
	}

	@Override
	public void newLeafSet(NodeAddressPair newPair, boolean addition) {}

	@Override
	public void localPut(PeerData data) {
		throw new RuntimeException("Can't store PeerData on Stealth Peer");
	}


	protected void recvError(int address, Object failed) {
		super.recvError(address, failed);

		if (failed instanceof Message)
			Global.stats.logCount("Stealth_recvError");
	}

	/* (non-Javadoc)
	 * @see sim.net.overlay.dht.pastry.Peer#send(int, sim.net.overlay.dht.Message)
	 */
	@Override
	public void send(int toAddress, Message msg) {
		super.send(toAddress, msg);
	}
}
