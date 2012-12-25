/*
 * Created on 12-Feb-2005
 */
package sim.net.overlay.dht.pastry;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;

import sim.events.Events;
import sim.main.Global;
import sim.main.Helper;
import sim.net.Host;
import sim.net.Packet;
import sim.net.PingPacket;
import sim.net.PongPacket;
import sim.net.UnreachablePacket;
import sim.net.overlay.dht.DHTListener;
import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.NodeAddressPair;
import sim.net.overlay.dht.NodeAddressPairs;
import sim.net.overlay.dht.PeerData;
import sim.net.overlay.dht.TestMessage;
import sim.net.overlay.dht.authentication.AuthData;
import sim.net.overlay.dht.authentication.GetAuthMessage;
import sim.net.overlay.dht.authentication.GetAuthReplyMessage;
import static sim.stats.StatsObject.SEPARATOR;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;


/**
 * A implementation of a Pastry Node
 * @author Andrew Brampton
 */
public class Peer extends PeerBase implements DHTListener {

	KeepaliveEvent keepaliveevent;

	/**
	 * Used by Auth Sessions to store which node this should send to
	 */
	public long sessionhack = 0;

	/**
	 * @param address Our Address
	 */
	public Peer(int address) {
		this( address, makeRandomID() );
	}

	public Peer(int address, final long nodeID) {
		super(address);

		setID(nodeID);

	    offloadSet = new TreeMap<Long, NodeAddressPairs>();

		routingTable = new RoutingTable(nodeID, b, idBits, this.getClass());
		leafSet = new LeafSet(nodeID, l);
		allpairs = new NodeAddressPairs();
		failedpairs = new TreeMap<Long, Long>();

		joinablepairs = new NodeAddressPairs();

		storedLocation = new Hashtable<PeerData, NodeAddressPairs>();
		storedKeys = new Hashtable<Long, PeerData>();

		getRequests = new Hashtable<Long, List<Long>>();

		// We need to listen for ourself
		addListener(this);

		setFailed(false);
	}

	protected void addAuth(Message m) {
		if (!Global.auth_on)
			throw new RuntimeException("Can't call Peer.addAuth() when Global.auth_on is off!");

		AuthData auth = this.auth;
		m.addAuth(auth);

		// If we add chains to all messages go ahead, OR with PairsMessage the full
		// chain must be added because of the chicken/egg problem
		if (Global.auth_add_chain || m instanceof PairsMessage) {
			// Add all my signers
			do {
				m.addAuth(auth);
				auth = authOKCache.get(auth.signer());
			} while (auth != null);
		}
	}

	public void getAuth(long hash) {
		GetMessage get = new GetAuthMessage(this.address, this.nodeID, hash);

		if (Global.auth_on)
			addAuth(get);

		// send get message into dht
		recv(get); // TODO, Should this be route()? If its recv, we get the chance
		           // to reply to our own get request, but maybe we should do it in a "better" way
	}

	public void get(long hash) {
		GetMessage get = new GetMessage(this.address, this.nodeID, hash);

		if (Global.auth_on)
			addAuth(get);

		// acquire current get request list for this hash
		List<Long> v = getRequests.get( hash );

		if (v == null) {
			// if no list exists, create one containing the current time
			v = new ArrayList<Long>(1);
			v.add( Events.getTime() );
			getRequests.put( hash, v);

		}
		else {
			// otherwise, add the time of this latest request
			v.add( Events.getTime() );
		}

		// send get message into dht
		recv(get); // TODO, Should this be route()? If its recv, we get the chance
		           // to reply to our own get request, but maybe we should do it in a "better" way
	}

	public void getHasArrived(PeerData pd) {

		super.getHasArrived(pd);

		// remove all waiting get requests
		if ( pd != null ) {
			getRequests.remove(pd.getHash());

			// did we get something back?
			if (pd.getData() != null) {
				Global.stats.logCount("DHT" + SEPARATOR + "NonNullGet");
				return;
			}
		} else {
			Global.stats.logCount("DHT" + SEPARATOR + "ReallyNullGet");
		}

		//System.out.println("Peer Data " + pd + " ");
		Global.stats.logCount("DHT" + SEPARATOR + "NullGet");
	}

	public void get(String key) {
		// hash key into long
		long hash = hash(key);
		this.get(hash);
	}

	/**
	 * This method makes sure that the keys are replicated on the correct hosts
	 */
	public void checkKeys() {
		// See if we should move some keys
		Iterator<PeerData> i = storedKeys.values().iterator();
		Map<Long, List<PeerData>> putMap = new Hashtable<Long, List<PeerData>> ();

		while(i.hasNext()) {
			PeerData p = i.next();
			NodeAddressPairs oldreplicas = storedLocation.get(p);
			NodeAddressPairs newreplicas = kNearestNodes(p.getHash(), p.getK());
			boolean responsible = false;

			if (oldreplicas == null) {
				oldreplicas = new NodeAddressPairs();
				oldreplicas.add(myPair);
				storedLocation.put(p, oldreplicas);
			}

			// Check if we are the closest
			NodeAddressPair np = newreplicas.findNumClosest(p.getHash());
			responsible = (np.nodeID == nodeID);

			if (responsible) {

				// Remove the list of peers that already have this key
				newreplicas.removeAll(oldreplicas);

				// Send it to everyone else that is a valid replica
				Iterator<NodeAddressPair> ii = newreplicas.iterator();
				while (ii.hasNext()) {
					NodeAddressPair pair = ii.next();

					//PutMessage put = new PutMessage(this.address, this.nodeID, pair.nodeID, p);
					//route(put);
					List<PeerData> v = putMap.get(pair.nodeID);
					if (v == null) {
						v = new ArrayList<PeerData>();
						putMap.put(pair.nodeID,v);
					}
					v.add(p);

					oldreplicas.add(pair);
				}
			} else {

				// I'm not even in the list anymore...
				if (!newreplicas.contains(nodeID)) {

					// If I was responsable, then move to the new node
					responsible = (oldreplicas.findNumClosest(p.getHash()).nodeID == nodeID);

					if (responsible) {
						//PutMessage put = new PutMessage(this.address, this.nodeID, np.nodeID, p);
						//route(put);
						List<PeerData> v = putMap.get(np.nodeID);
						if (v == null) {
							v = new ArrayList<PeerData>();
							putMap.put(np.nodeID,v);
						}
						v.add(p);
					}

					Trace.println(LogLevel.INFO, this + ": Removed " + p );

					i.remove();
					storedLocation.remove(p);
				}
			}
		}

		// route all necessary put messages
		Iterator<Long> ii = putMap.keySet().iterator();
		while(ii.hasNext()) {
			long toID = ii.next();
			List<PeerData> data = putMap.get(toID);
			PutMessage put = new PutMessage(this.address,this.nodeID,toID,data);
			route(put);
			//System.out.println(this + " Batched " + data.size() + " " + Peer.toString(toID, true)  + " > " + data.firstElement());
		}
	}

	/**
	 * Returns a list of the closest Nodes to this hash (including myself)
	 * @param hash
	 * @param k
	 * @return
	 */
	public NodeAddressPairs kNearestNodes(long hash, int k) {
		return leafSet.getClosestPairs(hash, k);
	}

	public void newLeafSet(NodeAddressPair newPair, boolean addition) {

		// If we haven't joined yet, don't do anything more
		if (!hasJoined())
			return;

		// Move the keys to their correct places
		checkKeys();

		// Log that the leafset has changed
		//Trace.println(LogLevel.LOG2, this + ": newLeafSet( " + (addition ? "added " : "remove ") + newPair + ") " + leafSet);

		// If our leafset is now too small we need to re-populate it
		if (!addition && leafSet.size() < l) {
			leafSetAddAll( allpairs );
		}

		// Send our leafset to this newcomer
		if (addition) {
			PairsMessage pm = new PairsMessage(address, nodeID, newPair.nodeID);
			pm.addAll(leafSet.getSet());

			//Send them our leftset
			send(newPair.address, pm);
		}
	}

	public void put(PeerData data) {

		//Optomisation (if its for us, just handle it)
		if (data.getHash() == nodeID) {
			localPut(data);
		} else {
			// put data into put message
			PutMessage put = new PutMessage(this.address, this.nodeID, data);

			if (Global.auth_on)
				addAuth(put);
			// send put message into dht
			recv(put);
		}
	}

	/**
	 * This method stores PeerData on the local node (and will try and replicate)
	 * @param pd The PeerData
	 */
	public void localPut(PeerData pd) {
		localPut(pd, true);
	}

	/**
	 * This method stores PeerData on the local node
	 * @param pd The PeerData
	 * @param checkKeys If true the local peer will try to replicate this PeerData
	 */
	public void localPut(PeerData pd, boolean checkKeys) {
		// store in this peer's hashtable
		if (storedKeys.containsKey(pd.getHash())) {
			Trace.println(LogLevel.WARN, this + ": WARNING Object already exists at " + Peer.toString(pd.getHash(), true));
		}

		Trace.println(LogLevel.INFO, this + ": Stored " + pd );
		storedKeys.put(pd.getHash(), pd);

		if (checkKeys)
			checkKeys();
	}

	/**
	 * DEBUG tests if a message got to the correct destination
	 * Returns the NodeID this message should have arrived at
	 * @param msg
	 * @return
	 */
	protected long testMessageSanityCheck(Message msg) {

		// determine if this peer is the closest known to the message's address
		Iterator<? extends Host> i = Global.hosts.getType(Peer.class).iterator();
		// acquire global knowledge of all existing ids
		// TODO: change this so it isn't done on demand, if
		// it's a common operation
		NodeAddressPairs existingIDs = new NodeAddressPairs();

		while(i.hasNext()) {
			Peer p = (Peer)i.next();
			if (!p.hasFailed() && p.hasJoined())
				existingIDs.add(new NodeAddressPair(p.nodeID));
		}

		return existingIDs.findNumClosest(msg.toID).nodeID;
	}

	/**
	 * Announce that we have joined the network!
	 *
	 */
	@Override
	protected void announce(String reason) {
		Trace.println(LogLevel.LOG1, this + ": announcement " + reason);

		// Send our announcement to this percentage of nodes
		final double sendToPercentage = 0.25;

		// Send these nodes this percentage of our routing table
		final double sendPercentage = 0.25;

		//If so now reply to everyone to say Hello by sending our table :)
		NodeAddressPairs all = new NodeAddressPairs();

		// Now add a randomset of all - to be sent
		for (Iterator<NodeAddressPair> i = allpairs.iterator(); i.hasNext(); ) {
			NodeAddressPair p = i.next();

			if (Global.rand.nextDouble() < sendPercentage) { // 25% chance
				all.add(p);
			}
		}

		for (Iterator<NodeAddressPair> i = routingTable.iterator(); i.hasNext(); ) {
			NodeAddressPair pair = i.next();

			// Don't send to this peer if its in our leafset, we do that laters
			if (!leafSet.contains(pair)) {

				if (Global.rand.nextDouble() < sendToPercentage) { // 25% chance

					PairsMessage pm = new PairsMessage(address, nodeID, pair.nodeID);

					pm.addAll(all);

					//Send them our table
					send(pair.address, pm);
				}
			}
		}

		// Send our leafset to everyone in the leafset
		for (Iterator<NodeAddressPair> i = leafSet.iterator(); i.hasNext(); ) {
			NodeAddressPair pair = i.next();

			PairsMessage pm = new PairsMessage(address, nodeID, pair.nodeID);
			pm.addAll(leafSet.getSet());

			//Send them our table
			send(pair.address, pm);
		}

		// Cleanup
		all.clear();
		all = null;
	}

	public void setJoined(boolean joined) {
		super.setJoined(joined);

		if (joined) {
			if (Global.debug_keep_alive) {
				if (keepaliveevent == null) {
					// Schedule a non critical keepalive for the future
					keepaliveevent = KeepaliveEvent.newEvent(this);

					Events.addFromNow(keepaliveevent, KEEP_ALIVE_EVERY / 2);
				}
			}
		}
	}


	/* -- TO DO IDRIS----

 	/**
	 * Announce after promotion!
	 *
	 */
	public void offload(long key, int k, double sndToFraction ) {

		Trace.println(LogLevel.LOG1, this + ": announceoffload ");

		// Find the key, and increase its replication factor
		PeerData pd = storedKeys.get(key);
		pd.setK( k + 1 );

		// Now replicate these keys to the responsible hosts
		checkKeys();

		// Find the nearest nodes to the key (and then announce them)
		NodeAddressPairs all = kNearestNodes(pd.getHash(), pd.getK());

		 // Do we want to announce ourself
		final boolean sendOurselfs = false;

		if (!sendOurselfs) {
			all.remove(nodeID);
		}

		// Now add the promoted and promoting node
		for (Iterator<NodeAddressPair> i = routingTable.iterator(); i.hasNext(); ) {
			NodeAddressPair pair = i.next();

			if (Global.rand.nextDouble() < sndToFraction) { // 25% chance
				OffloadAnnouceMessage pmsg = new OffloadAnnouceMessage(address, nodeID, pair.nodeID, key);
				pmsg.addAll(all);
				//Send them our table
				send(pair.address, pmsg);
			}
		}
	}

	/**
	 * Receives a message, with a boolean saying if its for us
	 * returns a boolean to say if we should forward this message
	 */
	@Override
	protected boolean recv(Message msg, boolean forUs) {
		boolean forward = true;

		if (hasFailed())
			return false;

		if (forUs) {

			if (Global.debug_extra_sanity) {
				long correctID = testMessageSanityCheck(msg);

				// check this is the correct node to have received the message
				if (correctID != nodeID) {
					Trace.println(LogLevel.ERR, this + ": ERROR Incorrect recipient! " + Peer.toString(correctID, true) + " != " + Peer.toString(nodeID, true) + " " + msg.toString());
					Global.stats.logCount("DHT" + SEPARATOR + "Message" + SEPARATOR + "IncorrectRecipient");
					printRoutingTable();
				}
			}

			if (msg instanceof PairsMessage) {
				PairsMessage m = (PairsMessage)msg;

				//Add this host to our routing table
				addToRoutingTable(m.fromID, m.fromAddress);

				//Add any returned entries to our routing table
				addToRoutingTable(m.getPairs());

				//Check if this is also a Finished Message
				if (msg instanceof JoinFinishedMessage) {

					// Announce to people on the network!
					announce("Join announce");

					// Finally move any keys - but wait a sec,
					// if I only just joined I shouldn't have any?
					checkKeys();

					// If using Auth, re-request any waiting Auth
					// This is done, coz we couldn't check any pre-join
					if (Global.auth_on) {
						Iterator<AuthData> i = new ArrayList<AuthData>(authQueue).iterator();

						while (i.hasNext()) {
							authHasArrived(i.next());
						}
					}

					joinablepairs.clear();
					setJoined(true);
				}

			} else if (msg instanceof CollisionMessage) {
				// Re gen our ID
				String txt = address + " Changing ID " + Peer.toString(nodeID, true) + " to ";
				setID (makeRandomID());
				txt += Peer.toString(nodeID, true);

				Trace.println(LogLevel.WARN, txt);

				// Try to join again
				join(joinAddress);
			} else if (msg instanceof TestMessage) {

				// If this message was sent direct to me then there is no point checking its destination
				if (msg.toID != nodeID) {

					long correctID = testMessageSanityCheck(msg);

					// check this is the correct node to have received the message
					if (correctID != nodeID) {
						Trace.println(LogLevel.ERR, this + ": ERROR Incorrect recipient! " + Peer.toString(correctID, true) + " != " + Peer.toString(nodeID, true) + " " + msg.toString());
						printRoutingTable();
					}
				}

			} else if (msg instanceof PutMessage) {
				Iterator<PeerData> i = ((PutMessage)msg).getData().iterator();
				while(i.hasNext()) {
					localPut(i.next());
				}

			} else if (msg instanceof GetReplyMessage) {
				// have received content.
				PeerData pd = ((GetReplyMessage)msg).getData();
				notifyGetHasArrived(pd);

			} else if (msg instanceof RequestPairsMessage) {
				// This guy has just asked for some pairs.
				// We should send him some. For the moment just send him our leafset
				PairsMessage pm = new PairsMessage(address, nodeID, msg.fromID);
				pm.addAll(routingTable.getRow(0));
				send(msg.fromAddress, pm);
			}
			else if (msg instanceof OffloadAnnouceMessage){
				OffloadAnnouceMessage m = (OffloadAnnouceMessage) msg;

				// Make a copy of these pairs
				NodeAddressPairs newPairs = new NodeAddressPairs( m.pairs );

				// The offload set should never include myself (otherwise it breaks stats)
				newPairs.remove( nodeID );

				if (!newPairs.isEmpty())
					offloadSet.put(m.key, newPairs );
			}

		} // if (forUs)


		//If its a joinMessage it doesn't matter if its for us we still need to reply
		if (msg instanceof JoinMessage ) {

			//First check if there is a ID collision
			if (msg.fromID == nodeID) {
				try {
					Message m = new CollisionMessage(address, nodeID, msg.fromID);
					send(msg.fromAddress, m);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				PairsMessage pm;

				if (forUs) {
					JoinFinishedMessage jfm = new JoinFinishedMessage(address, nodeID, msg.fromID);
					pm = jfm;

					//If its for us we must add the leafset
					pm.addAll(leafSet.getSet());
				} else {
					pm = new PairsMessage(address, nodeID, msg.fromID);
				}

				int i = routingTable.commonDigits(nodeID, msg.fromID);

				NodeAddressPairs row[] = routingTable.getRow(i);
				for (int ii=0; ii < row.length; ii++) {
					if (row[ii] != null) {
						pm.addAll(row[ii]);
						//pm.add(row[ii].random());
					}
				}

				send(msg.fromAddress, pm);
			}
		} else
		// check to see if we have a copy of the data
		if (msg instanceof GetMessage) {

			// attempt to retrieve the data
			PeerData value = storedKeys.get(msg.toID);

			// Log the number of get message that were handled by an assisting node
			if (value != null & !forUs) {
				Global.stats.logCount("DHT" + SEPARATOR + "Get" + SEPARATOR + "Offload");
			}

			// If we have the key, OR the message is for us, we must reply
			if (value != null || forUs) {
				// if we do, reply to the request with the data
				GetReplyMessage g;

				if (value == null)
					value = new PeerData(msg.toID);

				if (value == null) // If its null, create null PeerData
					value = new PeerData(msg.toID);

				if (msg instanceof GetAuthMessage) {
					g = new GetAuthReplyMessage(this.address,
										this.nodeID,
										msg.fromID,
										value,
										msg.objectID);
				} else {
					g = new GetReplyMessage(this.address,
										this.nodeID,
										msg.fromID,
										value,
										msg.objectID);
				}

				//g.setSize(NormalLink.BANDWIDTH_56k);

				// send reply directly to the requesting address
				send(msg.fromAddress, g);

				if (!forUs) {
					//Trace.println(LogLevel.INFO, this + ": GetMessage Interception!");
					// stop sending the message on
					forward = false;
				}
			}
			// otherwise continue passing it on
		}

		return forward;
	}


	/**
	 * We have received a error packet from this address, with this message
	 * @param address The address that the packet failed to reach
	 * @param failed The message (or packet) we tried to send that failed
	 */
	protected void recvError(int address, Object failed) {

		// Find the Pair for the node with this address
		NodeAddressPair pair = getRoutingPair(address);

		if (pair == null) {
			//Something has gone wrong
			if (address != joinAddress) // Make sure this isn't a packet caused by a join
				Trace.println(LogLevel.WARN, this + ": WARNING recvError from address " + Host.toString(address) + " that is not in our routingtables");
		} else {
			//Whoever it is remove them from our routing/leafsets
			removeFromRoutingTable(pair.nodeID, "recvError from " +  Host.toString(address) + " (" + failed + ")");
		}

		if (failed instanceof Message) {
			Message msg = (Message) failed;

			if (msg instanceof JoinMessage && msg.fromID == nodeID) {
				// If a join message doesn't reach its destination, then we can't join
				this.joinAddress = INVALID_ADDRESS; // Roll back the join

				// Try and find someone else to join
				if (joinablepairs.isEmpty()) {
					Trace.println(LogLevel.WARN, this + ": WARNING recvError while Joining (can't recover)");
				} else {
					int joinaddress = joinablepairs.first().address;
					joinablepairs.remove(joinablepairs.first()); // Remove the guy we are joining on to
					Trace.println(LogLevel.DEBUG, this + ": DEBUG failed to join, retrying " + joinablepairs.size() + " more retries");
					join(joinaddress);
				}

			} else {
				// Any other kind of message, then just resend
				if (msg.getResend()) {
					msg.reset();
					Global.stats.logCount("DHT" + SEPARATOR + "Message" + SEPARATOR + "Resent");
					Global.stats.logCount("DHT" + SEPARATOR + Helper.getShortName(this) + SEPARATOR + "Resent");
					recv(msg);
				}
			}

		} else if (failed instanceof Packet) {
			Packet p = (Packet) failed; // This most likly is a ping or pong
			if (p instanceof PongPacket || p instanceof PingPacket) {
				// Do nothing
			} else {
				Trace.println(LogLevel.ERR, this + ": ERROR Unhandled recvError " + failed);
			}
		} else {
			Trace.println(LogLevel.ERR, this + ": ERROR Unhandled recvError " + failed);
		}
	}

	@Override
	protected void recvError(UnreachablePacket p) {
		Packet badPacket = (Packet) p.data;

		if (badPacket.data instanceof Message) {
			recvError(p.from, badPacket.data);
		} else if (badPacket instanceof PongPacket
				|| badPacket instanceof PingPacket) {
			recvError(p.from, badPacket);
		} else {
			Trace.println(LogLevel.ERR, this + ": ERROR Unhandled recvError " + p);
		}
	}

	@Override
	protected void removeFromRoutingTable(long nodeID, String reason) {
		NodeAddressPair entry = getRoutingPair(nodeID);

		if (entry != null) {
			Trace.println(LogLevel.INFO, this + ": Removed " + Peer.toString(nodeID, true) + " (" + entry.address + ") from routing tables (" + reason + ")");

			allpairs.remove(entry);
			routingTable.remove(entry);

			if ( leafSet != null && leafSet.remove(entry) )
				notifyNewLeafSet(entry, false);

			failedpairs.put(nodeID, Events.getTime());

			// Log the deletion to the routing table
			if (Global.debug_log_dht_state) {
				Trace.println(LogLevel.LOG2, this + ": rdel " + entry);
			}
		}
	}

	/**
	 * Allows messages to be sent directly to a host
	 * @param to
	 * @param msg
	 */
	public void send(int toAddress, Message msg) {

		assert (toAddress != INVALID_ADDRESS);

		if (hasFailed())
			return;

		//if (msg.fromAddress == this.address) {

		if (Global.auth_on && msg.fromAddress == this.address) {
			// Add our auth data to this message
			addAuth(msg);
		}

		Trace.println(LogLevel.LOG2, this + ": sent " + msg);

		Global.stats.logCount("DHT" + SEPARATOR + "Message" + SEPARATOR + "Sent");
		Global.stats.logCount("DHT" + SEPARATOR + Helper.getShortName(msg) + SEPARATOR + "Sent");

		Global.stats.logCount("DHT" + SEPARATOR + Helper.getShortName(this) + SEPARATOR + "Sent");



		//}

		// If this is for us, just recv instantly
		if (toAddress == address) {
			recv(msg);
			return;
		}

		msg.addHop(this);
		Packet p;

		//Wrap the message in a Packet, and send towards next hop
		p = Packet.newPacket(this.address, toAddress, msg);
		p.critical = msg.critial;
		send(p);

	}

}
