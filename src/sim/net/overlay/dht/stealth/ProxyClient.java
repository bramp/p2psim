/*
 * Created on 19-May-2005
 */
package sim.net.overlay.dht.stealth;

import java.util.ArrayList;
import java.util.Iterator;

import sim.events.Event;
import sim.events.Events;
import sim.main.Global;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.PeerData;
import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.NodeAddressPair;
import sim.net.overlay.dht.NodeAddressPairs;
import sim.net.overlay.dht.events.JoinEvent;
import sim.net.overlay.dht.pastry.JoinFinishedMessage;
import sim.net.overlay.dht.pastry.LeafSet;

import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

public class ProxyClient extends Peer {
	int proxyAddress;
	/**
	 * @param address
	 */
	public ProxyClient(int address) {
		super(address);

		init();
	}

  ArrayList<Message> queuedMessages = new ArrayList<Message>();

	// proxy client doesnt have an ID!! , // WHY SUPER?

	public ProxyClient(int address, long nodeID) {
		super(address, nodeID);

		init();
	}

	protected void init() {
		// Proxy Peers do no use a leafset --
		//leafSet = null;
		leafSet = new LeafSet(nodeID, 2);
        routingTable = null;
	}

	protected NodeAddressPair addToRoutingTable(long ID, int address) {
		// Do nothing
		return null;
	}

	protected NodeAddressPairs addToRoutingTable(Iterable<NodeAddressPair> pairs) {
		return null;
	}


	@Override
	protected void leafSetAddAll(NodeAddressPairs newpairs) {
		leafSet.addAll(newpairs);
	}

	@Override
	public int getRoute(long ID) {
		if (ID == nodeID)
			return address;
		else
			return proxyAddress;
	}

	@Override
	public void fastJoin(HostSet peers) {

		peers = peers.getType(ServicePeer.class);
        //LeafSet leafset = new LeafSet(nodeID,2);

		if (peers.isEmpty()) {
			// Do the special join
			throw new RuntimeException("Proxy Node can't join itself!");
		}

		Iterator<Host> i = peers.iterator();
		while (i.hasNext()){
			ServicePeer S = (ServicePeer) i.next();
			leafSet.add( new NodeAddressPair(S.getID(),S.getAddress()) );
		}
		proxyAddress = leafSet.getClosestPairs2(1).first().address;

		joinAddress = proxyAddress;

		setJoined(true);
	}

	@Override
	public boolean recv(Message msg, boolean forUs) {
		boolean forward = true;

		if (!forUs && msg.fromAddress != address) {
			// We shouldn't be getting messages not destined for us
			Trace.println(LogLevel.ERR, this + ": ERROR StealthPeer peer is routing (" + msg + ")");
			return forward;
		}

		if (forUs && msg instanceof JoinFinishedMessage) {
			proxyAddress = joinAddress;
		}

		return super.recv(msg, forUs);
	}

	@Override
	public void setJoined(boolean joined) {
		super.setJoined(joined);
		if(joined){
			Iterator<Message> i = queuedMessages.iterator();
			while(i.hasNext()){
				route(i.next());
			}
			queuedMessages.clear();
		}
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

	@Override
	public void join(int joinAddress) {

		if (joinAddress == INVALID_ADDRESS) {
			HostSet peers = Global.hosts.getType(ProxyServer.class);

 			Host h = peers.getRandom();

			while ( h != null && h.hasFailed() ) {
				peers.remove(h);
				h = peers.getRandom();
			}

			if (h == null) {
				Trace.println(LogLevel.WARN, this + ": WARNING recvError while Joining (can't recover)");

				Event e = JoinEvent.newEvent(this, INVALID_ADDRESS);
				e.setRequired ( false );

				Events.addFromNow(e , 10000 );

				/*
				for( Message msg : queuedMessages )
					msg.dispose();
				queuedMessages.clear();*/

				return;
			}

			joinAddress = h.getAddress();
		}

		super.join(joinAddress);
	}

	protected void recvError(int address, Object failed) {

		Trace.println(LogLevel.DEBUG, "recvError " + failed);

		if (failed instanceof Message) {
			Message m = (Message)failed;
			boolean resend = m.getResend();

			m.setResend(false);

			super.recvError(address, failed);

			m.setResend(resend);

			queuedMessages.add((Message)failed);

			join(INVALID_ADDRESS);

		} else {
			super.recvError(address, failed);
		}
	}

	/* (non-Javadoc)
	 * @see sim.net.overlay.dht.pastry.Peer#send(int, sim.net.overlay.dht.Message)
	 */
	/*
	@Override
	public void send(int toAddress, Message msg) {
	//	msg.fromproxyclient = true;
		super.send(toAddress, msg);
	}
	*/

}
