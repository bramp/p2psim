/*
 * Created on 21-Mar-2005
 */
package sim.net.overlay.dht.swarm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sim.main.Global;
import sim.main.Helper;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.NodeAddressPair;
import sim.net.overlay.dht.pastry.LeafSet;
import sim.net.overlay.dht.pastry.PairsMessage;
import sim.stats.StatsObject;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;


/**
 * @author Andrew Brampton
 */
public class SwarmPeer extends sim.net.overlay.dht.pastry.Peer {

	/**
	 * The capability of the node
	 */
	public double capability = 1.0;

	/**
	 * Boolean to say if I joined a OPlane, or if I was pulled into one
	 */
	public boolean joinedOPlane = false;

	public List<OPlane> OPlanes = new ArrayList<OPlane>();

	protected void init() {
		// Override the routing table with our own
		routingTable = new RoutingTable(nodeID, b, idBits, this.getClass());
	}

	public SwarmPeer(int address) {
		super(address);

		init();
	}

	/**
	 * @param address
	 * @param nodeID
	 */
	public SwarmPeer(int address, long nodeID) {
		super(address, nodeID);

		init();
	}

	/**
	 * Creates a new OPlane
	 * @param OPlane
	 */
	public void createOPlane(int OPlane) {
		OPlane o = new OPlane(OPlane);
		o.parentID = INVALID_ID;
		OPlanes.add(o);
		joinedOPlane = true;
	}

	/* (non-Javadoc)
	 * @see net.bramp.p2psim.dht.DHTClientInterface#recv(net.bramp.p2psim.dht.Message)
	 */
	@Override
	public boolean recv(Message msg, boolean forUs) {
		boolean forward = super.recv(msg, forUs);

		// Everything this touchs becomes part of the OPlane
		if (msg instanceof OPlaneJoinMessage) {
			OPlane o = null;
			OPlaneJoinMessage m = (OPlaneJoinMessage) msg;

			int idx = OPlanes.indexOf( new OPlane(m.oPlane) );

			// If we aren't a member of the OPlane
			if (idx == -1) {
				// If this packet was destined for us then something went wrong
				if (forUs) {
					System.err.println(SwarmPeer.toString(msg.fromID, true) + " tried to join " + SwarmPeer.toString(nodeID, true) + " OPlane " + m.oPlane + " when he was not a member");
					o = null;
				} else {
					// Add us
					o = new OPlane(m.oPlane);
					OPlanes.add(o);
				}
			} else {
				o = OPlanes.get(idx);
			}

			long id = msg.getLastHop();

			// Don't add ourself :)
			if (o != null && id != INVALID_ID)
				o.addReceiver(id);

		} else if (msg instanceof StreamMessage) {
			if (forUs) {
				sendStreamPacket((StreamMessage) msg);
			}
		}

		// If this message if a pairs message, make sure we add the capabalities
		// into the routing tables
		if (msg instanceof PairsMessage) {
			NodeAddressPair pair = allpairs.find(msg.fromID);
			if (pair != null)
				pair.oob = msg.oob;
		}

		return forward;
	}

	/**
	 * @param OPlane The number of this OPlane
	 * @param nodeID The nodeID to send the OPlaneJoin to
	 */
	public void joinOPlane(long nodeID, int OPlane) {
		route(new OPlaneJoinMessage(address, this.nodeID, nodeID, OPlane));
		joinedOPlane = true;
	}

	public void sendStreamPacket(StreamMessage msg) {
		sendStreamPacket(msg.OPlane, null, msg.size, msg.packetID);
	}

	public void sendStreamPacket(int OPlane, Object data, int size, int packetID) {
		OPlane o = new OPlane(OPlane);
		if (!OPlanes.contains(o)) {
			Trace.println(LogLevel.ERR, "ERROR: Not a member of this OPlane");
			return;
		}
		o =  OPlanes.get(OPlanes.indexOf(o));
		List<Long> receiver = o.getReceiver();

		Iterator<Long> i = receiver.iterator();
		while (i.hasNext()) {
			long id = (i.next()).longValue();
			route(new StreamMessage(address, this.nodeID, id, OPlane, size, packetID));
		}

	}

	public void setCapability(double capability) {
		if (capability > 1 || capability < 0)
			throw new RuntimeException("capability out of range " + capability);

		if (this.capability != 1.0)
			throw new RuntimeException("capability is being changed " + this);

		this.capability = capability;

		Global.stats.logCount("Host" + StatsObject.SEPARATOR + Helper.getShortName(this) + getCapability());

		Trace.println(LogLevel.LOG2, this + ": capability " + getCapability());
	}


	public double getCapability() {
		return capability;
	}

	@Override
	public void send(int toAddress, Message msg) {
		// Add our capability on to our JoinMessages
		if (msg.fromID == nodeID && msg instanceof PairsMessage)
			msg.oob = capability;

		super.send(toAddress, msg);
	}

	@Override
	public void fastJoin(HostSet peers) {

		//HostSet peers = Global.hosts.getType(DHTInterface.class);
		Iterator<Host> i;
		LeafSet templeaf = new LeafSet(nodeID, l);

		// Remove all failed peers
		//i = peers.iterator();
		//while (i.hasNext())
		//	if (i.next().hasFailed())
		//		i.remove();

		// Remove me from the peers
		//peers.remove(this);

		// Now just add ALL peers to the temp leafset, so we can find the 16 matchs
		i = peers.iterator();
		while (i.hasNext()) {
			Host h = i.next();
			DHTInterface p = (DHTInterface)h;
			NodeAddressPair pair = new NodeAddressPair(p.getID(), h.getAddress());
			pair.rtt = getUnicastDelay(h.getAddress()) * 2;
			pair.oob = ((SwarmPeer)p).getCapability();
			templeaf.add(pair);
		}

		// Add a value every routingRows starting at a random point
		int factor = 4; // How connected the peers are (smaller the better)
		int count = Global.rand.nextInt(factor);
		i = peers.iterator();
		while (i.hasNext()) {
			Host h = i.next();

			if (count == 0) {
				SwarmPeer p = (SwarmPeer)h;

				int delay = getUnicastDelay(h.getAddress()) * 2;

				// Work out the delay, this saves on a ping being sent :)
				addToRoutingTable( p.getID(), h.getAddress(), delay, p.getCapability() );
			}

			count = (count + 1) % factor;
		}

		// Now add the correct leafset
		addToRoutingTable( templeaf.getSet() );

		if (peers.size() > 0) {
			joinAddress = peers.getRandom().getAddress();
			setJoined(true);
		} else {
			setJoined(false);
		}
	}

	protected NodeAddressPair addToRoutingTable(long ID, int address, int delay, double capability) {
		NodeAddressPair p = addToRoutingTable(ID, address, delay);
		if (p != null)
			p.oob = capability;
		return p;
	}

}
