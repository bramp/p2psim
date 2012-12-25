package sim.net.overlay.dht.pastry;

import java.security.MessageDigest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import sim.events.Events;
import sim.main.Global;
import sim.main.Helper;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.InvalidHostException;
import sim.net.Packet;
import sim.net.PingPacket;
import sim.net.PongPacket;
import sim.net.RoutingException;
import sim.net.SimpleHost;
import sim.net.TrackableObjectWrapper;
import sim.net.UnreachablePacket;
import sim.net.links.Link;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.DHTListener;
import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.NodeAddressPair;
import sim.net.overlay.dht.NodeAddressPairs;
import sim.net.overlay.dht.PeerData;
import sim.net.overlay.dht.authentication.AuthData;
import sim.net.overlay.dht.authentication.AuthMePacket;
import sim.net.overlay.dht.authentication.AuthReplyPacket;
import sim.net.overlay.dht.events.RecvEvent;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

import static sim.stats.StatsObject.SEPARATOR;

/**
 * A quick run down of recv, route, send
 * "recv" will read the message and then "route" it if needed...
 * "route" will work out the best address and "send" it
 * "send" will place the message in a packet and put it on the physical network
 *
 * @author Andrew Brampton
 *
 */
abstract public class PeerBase extends SimpleHost implements DHTInterface {

	/**
	 * Random 64bit node identifier.
	 */
	public long nodeID = INVALID_ID;

	/**
	 * Some Pastry constants
	 */
	public static final int idBits = 64; // Length of the nodeIDs (64bits)
	public static final int b = 4; // 1,2,4,8,16,32
	public static final int l = 16;

	public static int KEEP_ALIVE_EVERY = 10000 * 1000; // 10000 secs

	/**
	 * The amount of time a peer stays in our Failed list
	 */
	public static int FAILED_TIMEOUT = 60 * 1000; // 60 secs

	/**
	 * The address that we originally joined
	 */
	protected int joinAddress = INVALID_ADDRESS;

	Map<Long, NodeAddressPairs> offloadSet;

	public LeafSet leafSet;	// Our leafSet
	public RoutingTable routingTable; // Our RoutingTable
	public NodeAddressPairs allpairs; // All the nodeAddressPairs
	public Map<Long, Long> failedpairs; // All the nodeAddressPairs that have failed <node ID, failed time>

	public NodeAddressPairs joinablepairs; // A set of peers we could possibly join

	public Map<Long, PeerData> storedKeys; // stored key/value pairs
	public Map<PeerData, NodeAddressPairs> storedLocation; // The address of peers with this key
	public Map<Long, List<Long>> getRequests; // pending get operations

	protected NodeAddressPair myPair; // The NodeAddressPair that represents me

	// Our auth data
	protected AuthData auth = null;

	/**
	 * Flag to say if this peer has joined the DHT
	 */
	protected boolean hasJoined;

	protected List<DHTListener> listeners = new ArrayList<DHTListener>(2);

	public boolean addListener(DHTListener list) {
		return listeners.add(list);
	}

	public boolean removeListener(DHTListener list) {
		return listeners.remove(list);
	}

	protected void notifyNewLeafSet(NodeAddressPair pair, boolean addition) {
		Iterator<DHTListener> i = listeners.iterator();
		while (i.hasNext()) {
			i.next().newLeafSet(pair, addition);
		}

		// Log the addition to the routing table
		if (Global.debug_log_dht_state) {
			if (addition)
				Trace.println(LogLevel.LOG2, this + ": ladd " + pair);
			else
				Trace.println(LogLevel.LOG2, this + ": ldel " + pair);
		}
	}

	protected void notifyGetHasArrived(PeerData pd) {
		Iterator<DHTListener> i = listeners.iterator();
		while (i.hasNext()) {
			i.next().getHasArrived(pd);
		}
	}

	public static long hash(String key) {
		MessageDigest strDigest = null;
		try {strDigest = MessageDigest.getInstance("SHA-1");}
		catch(Exception e) {
			Trace.println(LogLevel.ERR,"Could not acquire SHA-1 hasher instance!");
			return 0;
		}

		strDigest.update(key.getBytes());
		byte digest[] = strDigest.digest();

		// build long from the first 8 bytes of the digest
        long hash = ((((long) digest[7]) & 0xFF)
                  | ((((long) digest[6]) & 0xFF) << 8)
                  | ((((long) digest[5]) & 0xFF) << 16)
                  | ((((long) digest[4]) & 0xFF) << 24)
                  | ((((long) digest[3]) & 0xFF) << 32)
                  | ((((long) digest[2]) & 0xFF) << 40)
                  | ((((long) digest[1]) & 0xFF) << 48)
                  | ((((long) digest[0]) & 0xFF) << 56));

		return hash;
	}

	/**
	 * Set this node as joined or not joined
	 * @param joined
	 */
	public void setJoined(boolean joined) {
		hasJoined = joined;
	}

	/**
	 * Debug method that uses global knowledge to quickly join all peers
	 *
	 */
	public void fastJoin(HostSet peers) {

		Iterator<Host> i;
		LeafSet templeaf = new LeafSet(nodeID, l);

		// Now just add ALL peers to the temp leafset, so we can find the 16 matches
		i = peers.iterator();
		while (i.hasNext()) {
			Host h = i.next();

			if (h.hasFailed())
				throw new RuntimeException("blah");

			DHTInterface p = (DHTInterface)h;
			NodeAddressPair pair = new NodeAddressPair(p.getID(), h.getAddress());
			pair.rtt = getUnicastDelay(h.getAddress()) * 2;
			templeaf.add(pair);
		}

		// Add a value every routingRows starting at a random point
		// Factor how connected the peers are (smaller the better)
		//int factor = (int)Math.ceil( peers.size() / (Math.log(peers.size()) / Math.log(Math.pow(2,b)) * Math.pow(2,b)));;
		int factor = 4;
		int count = Global.rand.nextInt(factor);
		i = peers.iterator();
		while (i.hasNext()) {
			Host h = i.next();

			if (count == 0) {
				DHTInterface p = (DHTInterface)h;

				int delay = getUnicastDelay(h.getAddress()) * 2;

				// Work out the delay, this saves on a ping being sent :)
				addToRoutingTable( p.getID(), h.getAddress(), delay );
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

	public int getJoinAddress() {
		return joinAddress;
	}

	public static long lastID = 0;
	protected static long makeRandomID() {

		if (Global.debug_sequential_ids) {
			// use sequential addressing if debugging
			lastID++;
			return lastID;
		}

		// generate random address normally
		return PeerBase.hash( Long.toString( Global.rand.nextLong() ) );
	}

	// Caches the calls to toString()
	private static Map<Long, String> toStringCache = new HashMap<Long, String>();
	private static Map<Long, String> toStringGapCache = new HashMap<Long, String>();
	public static String toString(long nodeID, boolean gaps) {

		String ret;

		if (gaps) {
			ret = toStringGapCache.get(nodeID);
			if (ret == null) {
				ret = toString(nodeID, false);

				//Break up every 4th digit
				String ret2 = "";
				for (int i = 0; i < idBits / 4; i+=4) {
					ret2 += ret.substring(i, i+4) + " ";
				}
				ret2 = ret2.substring(0, ret2.length() - 1);

				ret = ret2;
				toStringGapCache.put(nodeID, ret);
			}
		} else {
			ret = toStringCache.get(nodeID);
			if (ret == null) {
				ret = Long.toHexString(nodeID).toUpperCase();

				//Zero pad it
				while (ret.length() < (idBits / 4))
					ret = '0' + ret;

				toStringCache.put(nodeID, ret);
			}
		}

		return ret;
	}


	public PeerBase(int address) {
		super(address);
	}

	/**
	 * Returns true if this node is in our failed list
	 * @param nodeID
	 * @return
	 */
	public boolean checkedFailed(long nodeID) {
		// Check if the failedpair is old
		Long time = failedpairs.get(nodeID);

		if (time == null)
			return false;

		if (time.longValue() + FAILED_TIMEOUT < Events.getTime()) {
			failedpairs.remove(nodeID);
			return false;
		}

		return true;
	}


	protected NodeAddressPair addToRoutingTable(long ID, int address) {
		return addToRoutingTable(ID, address, Integer.MAX_VALUE);
	}

	private static NodeAddressPairs tempPairs = new NodeAddressPairs();
	protected NodeAddressPair addToRoutingTable(long ID, int address, int delay) {
		if (!canAddToRoutingTable(ID, address))
			return null;

		// Create the new pair
		NodeAddressPair pair = new NodeAddressPair(ID, address);
		pair.rtt = delay;

		// Add this single pair in to the tempPairs, and addToTable
		tempPairs.clear();
		tempPairs.add(pair);
		_addToRoutingTable(tempPairs);

		return pair;
	}

	protected NodeAddressPairs addToRoutingTable(Iterable<NodeAddressPair> pairs) {

		// We must clone each Pair
		NodeAddressPairs newpairs = new NodeAddressPairs();
		Iterator<NodeAddressPair> i = pairs.iterator();
		while (i.hasNext()) {
			NodeAddressPair p = i.next();
			if (canAddToRoutingTable(p.nodeID, p.address)) {
				NodeAddressPair np = new NodeAddressPair(p.nodeID, p.address);
				np.oob = p.oob;
				newpairs.add( np );
			}
		}

		return _addToRoutingTable(newpairs);
	}

	/**
	 * Inserts the newpairs without checking their state
	 * We assume that only internal methods call this
	 * @param newpairs
	 * @return
	 */
	protected NodeAddressPairs _addToRoutingTable(NodeAddressPairs newpairs) {

		// Add to all the tables
		allpairs.addAll(newpairs);
		routingTable.addAll(newpairs);
		leafSetAddAll(newpairs);

		Iterator<NodeAddressPair> i = newpairs.iterator();
		while (i.hasNext()) {
			NodeAddressPair p = i.next();

			// Log the addition to the routing table
			if (Global.debug_log_dht_state) {
				Trace.println(LogLevel.LOG2, this + ": radd " + p);
			}

			// Ping the new host
			if (p.rtt == Integer.MAX_VALUE) {
				sendKeepAlive(p);
			}
		}

		return newpairs;
	}

	/**
	 * Checks if this nodeID address combo can be added to the routing tables
	 * It can only be added if it doesn't match our nodeID or address
	 * also that it hasn't previously failed
	 * OR if its a old failed node but now with a different address
	 * @param nodeID
	 * @param address
	 * @return
	 */
	protected boolean canAddToRoutingTable(final long nodeID, final int address) {
		// If this p isn't us
		if ((this.nodeID == nodeID) || (this.address == address))
			return false;

		// Check if its already in our tables
		NodeAddressPair oldPair = allpairs.find(nodeID);
		if (oldPair != null) {

			// Nothing has changed, so just return
			if (oldPair.address == address) {
				return false;
			}

			// If the address has changed, remove the old, and in with the new
			removeFromRoutingTable(oldPair.nodeID, "Node's address has changed");
			failedpairs.remove(oldPair.nodeID); // This node hasn't failed

			return true;
		}

		// Don't add previously failedpairs
		if (checkedFailed(nodeID))
			return false;

		return true;
	}

	protected void leafSetAddAll(NodeAddressPairs newpairs) {
		// Record all the new entries in the leafset
		NodeAddressPairs addpairs = leafSet.addAll(newpairs);

		// Loop all the entries that got added (if any)
		Iterator<NodeAddressPair> i = addpairs.iterator();
		while (i.hasNext()) {
			notifyNewLeafSet(i.next(), true);
		}
	}

	/* (non-Javadoc)
	 * @see net.bramp.p2psim.dht.DHTPeerInterface#getID()
	 */
	public long getID() {
		return nodeID;
	}

	/**
	 * Sets our node ID
	 * @param ID
	 */
	protected void setID(long ID) {
		this.nodeID = ID;

		// If we call this then we will reannounce
		setAddress(address);
	}

	@Override
	public void addLink(Link c) throws InvalidHostException {
		super.addLink(c);

		// If we call this then we will reannounce
		setAddress(address);
	}

	protected abstract void announce(String reason);
	protected int lastAnnounceAddress = Host.INVALID_ADDRESS;

	@Override
	public void setAddress(int newAddress) {
		// If our ID or address changes, we need to do this
		myPair = new NodeAddressPair(nodeID, newAddress);

		// We also need to re-announce
		if (hasJoined() && link != null) {
			// Little line to stop reannouncements
			if (lastAnnounceAddress != address) {
				announce("Changed Address");
				lastAnnounceAddress = address;
			}
		}

		super.setAddress(newAddress);
	}

	/**
	 * Gets the address to route to for a certain ID
	 * Returns our own address if we are the nearest ID
	 * @param ID
	 * @return
	 */
	public int getRoute(long ID) {

		final int ME = 0;
		final int LEAF = 1;
		final int RT = 2;
        final int OFFLOAD = 3;
		int choice = ME;

		NodeAddressPair routePair = null;
		NodeAddressPair leafPair = null;
		NodeAddressPair offloadPair = null;

		do {
			long myDiff = Math.abs(nodeID - ID);
			long routeDiff = Long.MAX_VALUE;

			// Speed optimisation
			if (ID == nodeID) {
				choice = ME;
				break;
			}

			// check on the offload set for the exact key/detination
			if(offloadSet.containsKey(ID)){
				NodeAddressPairs p = offloadSet.get(ID);

				offloadPair = p.random();
				choice = OFFLOAD;
				break;
			}

			// If the ID is within the leafset
			if (leafSet.inRange(ID)) {
				Trace.println(LogLevel.INFO, this + " In Range" );

				// Check the leafset, and if null is returned we are closest
				leafPair = leafSet.getRoute(ID);
				if (leafPair == null)
					choice = ME;
				else
					choice = LEAF;

				break;
			}

			// Check the routing table
			routePair = routingTable.getRoute(ID);

			// If the routing table returned nothing, then use the leafset
			if (routePair == null) {
				leafPair = leafSet.getRoute(ID);
				if (leafPair == null)
					choice = ME;
				else
					choice = LEAF;

				break;
			}

			// We have already decided that the ID isn't within the leafset
			// so use the routing table
			//choice = RT;
			//break;

			routeDiff = Math.abs(routePair.nodeID - ID);

			// We have already decided that the ID isn't within the leafset
			// so use the routing table
			if (routeDiff < myDiff) {
				choice = RT;
				break;
			} else if (routeDiff == myDiff) { // Special case, return lowest ID
				if (routePair.nodeID < nodeID) {
					choice = RT;
					break;
				}
			} else { // (routingDiff > myDiff)
				//Trace.println(LogLevel.INFO, this + " one in a million");
				leafPair = leafSet.getRoute(ID);
				if (leafPair == null) {
					choice = ME;
				} else {
					choice = LEAF;
				}
				break;
			}

		} while (false); // Fake loop so we can use break;

		switch (choice) {
			case OFFLOAD:
				Trace.println(LogLevel.INFO, this + " Offload" );
				return offloadPair.address;
			case LEAF:
				Trace.println(LogLevel.INFO, this + " Leaf" );
				return leafPair.address;
			case RT:
				Trace.println(LogLevel.INFO, this + " Routing" );
				return routePair.address;
			default: // ME
				Trace.println(LogLevel.INFO, this + " Me" );
				return address;
		}
	}

	/**
	 * Get the routing pair entry for this ID
	 * @param nodeID
	 * @return
	 */
	NodeAddressPair getRoutingPair(long nodeID) {
		return allpairs.find(nodeID);
	}

	/**
	 * Get the routing pair entry for this address
	 * TODO Make this function more effecient
	 * @param address
	 * @return
	 */
	NodeAddressPair getRoutingPair(int address) {
		// We have to iterator through all entries to find what
		// we want :(
		@SuppressWarnings(value={"unchecked"})
		Iterator <NodeAddressPair> i = allpairs.iterator();

		while (i.hasNext()) {
			NodeAddressPair pair = i.next();
			if (pair.address == address)
				return pair;
		}

		return null;
	}

	protected void clearRoutingTable() {
		allpairs.clear();
		routingTable.clear();
		if (leafSet != null) {leafSet.clear();}
	}

	public enum AuthValid {
		OK, FAILED, WAIT;

		public final String toString() {
			switch (this) {
				case OK:
					return "OK";
				case FAILED:
					return "FAILED";
				case WAIT:
					return "WAIT";
				default:
					throw new RuntimeException("Unknown value");
			}
		}
	}

	/**
	 * Cache of all certificates
	 */
	Map<Long, AuthData> authOKCache = Global.auth_on ? new HashMap<Long, AuthData>() : null;
	Map<Long, AuthData> authFailCache = Global.auth_on ? new HashMap<Long, AuthData>() : null;

	/**
	 * Check if this AuthData is valid
	 * @param auth
	 * @return
	 */
	public AuthValid checkAuth(AuthData auth) {

		if (!Global.auth_on)
			throw new RuntimeException("Can't checkAuth() when !Global.auth_on");

		// First check if the signature is invalid
		//if (auth.valid) // This can't be here, To check a certificate, you need the signers
		//	return AuthValid.FAILED;

		// If we pass in nothing, then this obviously failed,
		// Also check the failed cache
		if (auth == null || authFailCache.containsKey(auth)) {
			Global.stats.logCount("Auth" + SEPARATOR + "Cache" + SEPARATOR + "FAILED");
			return AuthValid.FAILED;
		}

		// Check the OK cache
		if (authOKCache.containsValue(auth)) {
			Global.stats.logCount("Auth" + SEPARATOR + "Cache" + SEPARATOR + "OK");
			return AuthValid.OK;
		}

		// Now check the signer agaisnt out trusted Global Key
		if (auth.signer() == AuthData.GLOBAL_KEY) {
			Global.stats.logCount("Auth" + SEPARATOR + "OK");
			authOKCache.put(auth.owner(), auth);
			return AuthValid.OK;
		}

		// Check if no one signed it OR Check if he signed himself
		if (auth.signer() == AuthData.INVALID_KEY || auth.signer() == auth.owner()) {
			Global.stats.logCount("Auth" + SEPARATOR + "FAILED");
			authFailCache.put(auth.owner(), auth);
			return AuthValid.FAILED;
		}

		// At this point, the AuthData isn't in the OK or Failed caches and otherwise looks ok
		// So lets check its signer

		// Check the FAIL cache for the signer
		if (authFailCache.containsKey(auth.signer())) {
			Global.stats.logCount("Auth" + SEPARATOR + "FAILED");
			authFailCache.put(auth.owner(), auth);
			return AuthValid.FAILED;
		}

		// Now check the OK cache
		if (authOKCache.containsKey(auth.signer())) {
			Global.stats.logCount("Auth" + SEPARATOR + "OK");
			authOKCache.put(auth.owner(), auth);
			return AuthValid.OK;
		}

		// Else, issue a query for the AuthData, and return a wait
		getAuth( auth.signer() );

		// Check that this auth isn't already in there (this can be acheived with a set, but this is faster)
		if (!authQueue.contains(auth)) {
			Global.stats.logCount("Auth" + SEPARATOR + "WAIT");
			authQueue.add(auth);
		}

		return AuthValid.WAIT;
	}

	public abstract void getAuth(long hash);


	/* (non-Javadoc)
	 * @see sim.net.overlay.dht.DHTInterface#getAuth()
	 */
	public AuthData getAuth() {
		return auth;
	}

	public void setAuth(AuthData auth) {

		Trace.println(LogLevel.DEBUG, this + ": DEBUG setAuth(" + auth + ")");

		if (auth.owner() != this.nodeID)
			throw new RuntimeException("Setting a authdata to the wrong owner!");

		// Remove my old auth
		if (this.auth != null)
			authOKCache.remove(this.auth.owner());

		// I trust my new auth
		authOKCache.put(auth.owner(), auth);

		this.auth = auth;

		// Put this message into the DHT (this message will end up at me!)
		// only if this Auth data can sign others!
		if (auth.canSignOthers())
			put(new PeerData(auth.owner(), auth, auth.getSize(), 1));
	}

	/**
	 * Tells the node to aquire auth creditials from this address
	 * @param authaddress
	 */
	public void auth(int authaddress) {

		// Generate Auth Data
		auth = new AuthData(nodeID);

		send( AuthMePacket.newPacket(address, authaddress, auth) );
	}

	/**
	 * Initilise state and join the DHT network
	 */
	public void join(int joinAddress) {

		if (hasFailed())
			return;

		if (hasJoined() != false)
			setJoined(false);

		hasJoined = false;

		if (joinAddress == INVALID_ADDRESS) {
			Trace.println(LogLevel.DEBUG, this + ": DEBUG Special first join");
			this.joinAddress = address;
			setJoined(true);
			return;
		}

		if (joinAddress == address) {
			Trace.println(LogLevel.ERR, "ERROR Joining myself!");
		}

		// If we are doing Auth, issue the request if we haven't already
		if ( Global.auth_on ) {

			// Check if this is our first call to join
			if ((this.joinAddress == INVALID_ADDRESS) && (auth == null)) {
				this.joinAddress = joinAddress;

				// Try and auth
				auth(joinAddress);

				return;
			} // else continue as normal
		}

		this.joinAddress = joinAddress;

		// If we know about peers, move them to joinablepairs, and blank
		// our routing tables
		if (!allpairs.isEmpty()) {
			joinablepairs = allpairs;
			allpairs = new NodeAddressPairs();
			clearRoutingTable();
		}

		//Send the join message to node numberically closest to us
		send(joinAddress, new JoinMessage(address, nodeID));
	}

	/**
	 * A List of messages waiting for their signers to be validated
	 */
	public List<Message> msgQueue = Global.auth_on ? new ArrayList<Message>() : null;

	/**
	 * List of authData that is waiting to be validated
	 */
	public List<AuthData> authQueue = Global.auth_on ? new ArrayList<AuthData>() : null;

	/**
	 * Indicates that a AuthData has a arrived.
	 * @param auth
	 * @return Returns an array of all AuthData's that have been dealt with due to this
	 * arrival.
	 */
	protected Collection<AuthData> _authHasArrived(AuthData auth) {

		//Global.trace.println(LogLevel.DEBUG, this + ": _authHasArrived(" + auth + ")");

		ArrayList<AuthData> v = null;

		// First check this auth data is valid
		AuthValid valid = checkAuth(auth);

		if (valid == AuthValid.OK || valid == AuthValid.FAILED) {

			v = new ArrayList<AuthData>(authQueue.size());

			{
			// Re-run the auth queue (looking for anyone needing this AuthData)
			Iterator<AuthData> i = authQueue.iterator();

			while (i.hasNext()) {
				// If this auth data's signer, is the nearly arrived authdata
				// then we need to recheck it
				AuthData iauth = i.next();
				if (iauth.signer() == auth.owner()) {
					v.addAll( _authHasArrived(iauth) );
				} else if (iauth == auth) {
					v.add(iauth);
				}
			}
			}

			// Check the message queue
			Iterator<Message> i = msgQueue.iterator();

			while (i.hasNext()) {
				// If this auth data's signer, is the nearly arrived authdata
				// then we need to recheck it
				Message m = i.next();
				if (m.getAuth() == auth) {
					i.remove();

					// Re-receive the message
					Events.addNow( RecvEvent.newEvent(this, m) );
				}
			}
		}

		return v;
	}

	protected void authHasArrived(AuthData auth) {
		// Runs down the tree of updates
		Collection<AuthData> c = _authHasArrived(auth);

		// Now removes all updated authdatas
		if (c != null) {
			authQueue.removeAll(c);

			// Help the GC
			c.clear();
			c = null;
		}
	}

	/**
	 *
	 * @param pd
	 */
	public void getHasArrived(PeerData pd) {
		// If this is a AuthPacket
		if (Global.auth_on) {
			if (pd.getData() instanceof AuthData) {
				authHasArrived((AuthData) pd.getData());
			}
		}
	}

	/**
	 * Prints the current routing table and leafset
	 */
	public void printRoutingTable() {
		StringBuilder sb = new StringBuilder(4096);

		sb.append("Routing Tables for ");
		sb.append(Helper.getShortName(this));
		sb.append(' ');
		sb.append(this);
		sb.append('\n');
		sb.append(routingTable.toString());
		Trace.println(LogLevel.INFO, sb.toString());

		if (leafSet != null)
			Trace.println(LogLevel.INFO, "  LeafSet: " + leafSet.toString());

		sb.setLength(0);
		sb.append( "FailedSet: " );
		Iterator<Long> i = failedpairs.keySet().iterator();
		while (i.hasNext()) {
			sb.append( Peer.toString( i.next(), true) ).append( " | " );
		}
		sb.append('\n');
		Trace.println(LogLevel.INFO, sb.toString());
	}

	/* (non-Javadoc)
	 * @see net.bramp.p2psim.Host#recv(net.bramp.p2psim.Link, net.bramp.p2psim.Packet)
	 */
	@Override
	public void recv(Link link, Packet p) {

		if (hasFailed())
			return;

		super.recv(link, p);

		// If its a normal packet with a message
		if (p.data instanceof Message) {

			Message msg = (Message) p.data;

			if (joinAddress == INVALID_ADDRESS) {
				// This happens in two cases. Once if there is truely a error,
				// or secondly you rejoin a network with just you, and a stealth node
				// sends you a message (without noticing your previous failure)
				Trace.println(LogLevel.WARN, this + ": WARNING Received message before joining");
			}

			// Now signal the Peer we have a message
			recv(msg);

			// If its a pong message
		} else if (p instanceof PongPacket) {
			recv((PongPacket) p);

			// If the packet couldn't be sent
		} else if (p instanceof UnreachablePacket) {
			recvError((UnreachablePacket) p);

		} else if (p instanceof AuthMePacket) {

			// Check we are allowed to auth users
			if (auth != null && auth.canSignOthers()) {
				AuthData theirAuth = ((AuthMePacket)p).auth;

				// No need to check, but here is where login details would be checked

				// Sign it
				theirAuth.sign(auth);

				send( AuthReplyPacket.newPacket(this.address, p.from, auth, theirAuth) );

			} else {
				Trace.println(LogLevel.ERR, this + ": ERROR received AuthMePacket when we are not able to auth users! (" + auth + ")" );
			}

		} else if (p instanceof AuthReplyPacket) {

			AuthData[] auths = ((AuthReplyPacket)p).authreply;

			// Yay our Auth has come back, lets check if it was accepted

			authHasArrived(((AuthReplyPacket)p).auth);

			// Check remote auth
			// We should check sender's validity, but we can't atm (since we are
			// not on the DHT)
			// TODO The Auth Chain should be in the AuthReplyPacket, so we should be able to

			if (auths[0] == null) {
				throw new RuntimeException("Our auth was invalid... why?");
			}

			setAuth(auths[0]);

			// Store any remaining AuthDatas
			for (int i = 1; i < auths.length; i++)
				authHasArrived(auths[i]);

			// Now continue with the join
			join(p.from); //TODO make this join someone other than the Auth Gateway
		}

		// After the packet has been used, free() it
		p.free();
	}

	/* (non-Javadoc)
	 * @see net.bramp.p2psim.dht.DHTPeerInterface#recv(net.bramp.p2psim.dht.Message)
	 */
	public void recv(Message msg) {

		if ( msg.objectID == 49040 )
			System.out.println("blah");

		// If this message is direct from someone previous failed, remove them
		// from the failed list
		if (checkedFailed(msg.fromID))
			failedpairs.remove(msg.fromID);

		// Special case. If this is a join message, make sure we don't know the node
		if (msg instanceof JoinMessage)
			removeFromRoutingTable(msg.fromID, "A node rejoining can't be known");

		// Figure out if we should fwd this message
		// TODO Change this to check only leafset, coz that will tell us if the message is for us
		int address = getRoute(msg.toID);

		boolean forUs = (address == this.address);

		// Now check the auth
		if (Global.auth_on && (Global.auth_per_hop || forUs) ) {
			AuthValid valid = checkMessage(msg);

			if ( forUs )
				Trace.println(LogLevel.LOG2, this + ": recv " + msg + " " + valid);

			// Add the message to a queue to be used later
			if (valid == AuthValid.WAIT) {
				msgQueue.add(msg);
				return;
			} else if (valid != AuthValid.OK) {
				// If the message has not been accepted, well drop it
				msg.dispose();
				return;
			}
		}

		// If the message is for us do lots of logging
		if (forUs) {
			//Trace.println(LogLevel.LOG2, this + ": recv " + msg);
			//Global.stats.logCount("DHT" + SEPARATOR + "MessageRecv");

			if (!Global.auth_on) { // If auth is on this has already been displayed
				Trace.println(LogLevel.LOG2, this + ": recv " + msg);
			}

			// This is the last hop, so add it
			msg.addHop(this, true);

			int unicast = getUnicastDelay(msg.fromAddress);

			// Test for errors
			if (unicast < 0) {
				Global.stats.logCount("Sim" + SEPARATOR + "UnicastWarning");
			} else {

				int delay = msg.getDelay();

				String logs[] = {"Message", Helper.getShortName( msg ), null};

				// HACK record good messages
				if (Global.record_good) {
					if (msg.getBadLookup()) {
						logs[2] = "Bad" + logs[1];
					} else {
						logs[2] = "Good" + logs[1];
					}
				}

				// We have to log stuff twice, generic Message log + Mess
				for (int i = 0; i < logs.length; i++) {
					final String log = logs[i];

					if (log == null)
						break;

					final String dht_log = "DHT" + SEPARATOR + log + SEPARATOR;

					// Log some stats about it
					Global.stats.log(dht_log + "Hops", msg.getHopCount());
					Global.stats.log(dht_log + "E2ELatency", delay);

					Global.stats.logCount(dht_log + "Hops" + msg.getHopCount());

					// Even if this isn't auth, it is still counted as received
					Global.stats.logCount(dht_log + "Recv");

					Global.stats.log(dht_log + "Resent", msg.getResents());

					if (delay == 0) {
						Global.stats.logCount(dht_log + "ZeroLatency");
					}

					Global.stats.log("DHT" + SEPARATOR + log + SEPARATOR + "Resent", msg.getResents());

					if (delay == 0) {
						Global.stats.logCount("DHT" + SEPARATOR + log + SEPARATOR + "ZeroLatency");
					}

					// Log the Stretch
					Global.stats.logRunningTotal(dht_log + "E2ELatencyTotal", delay);
					Global.stats.logRunningTotal(dht_log + "E2ELatencyUnicastTotal", unicast);

					// If the unicast is zero, then don't count this message (in Stretch)
					if (unicast == 0) {
						Global.stats.logAverage(dht_log + "Stretch2", (double)1);
					} else {
						Global.stats.logAverage(dht_log + "Stretch", (double)delay / (double)unicast);
						Global.stats.logAverage(dht_log + "Stretch2", (double)delay / (double)unicast);
					}

					// Log Message size
					Global.stats.logAverage("DHT" + SEPARATOR + log + SEPARATOR + "Size", msg.getSize());
				}
			}
		} else /* (!forUs) */ {
			// HACK
			if (routingTable != null && routingTable.badLookup) {
				msg.setBadLookup();
			}
		}

		// If it wasn't for us, its time to route it!
		if (recv(msg, forUs) && !forUs && address != INVALID_ADDRESS) {
			route(address, msg);
		} else {
			// Otherwise Cleanup a little
			msg.dispose();
		}
	}

	public AuthValid checkMessage(Message msg) {

		if (!Global.auth_on)
			throw new RuntimeException("Can't Auth checkMessage when Global.auth_on is off!");

		// Check that this message is from the auth owner
		if (msg.getAuth() != null) {
			if (msg.fromID != msg.getAuth().owner())
				return AuthValid.FAILED;
		}

		// Boolean to set if we should Auth this message
		boolean checkme = false;

		// Decide if we should check this message
		if (msg instanceof GetMessage || msg instanceof GetReplyMessage) {
			if (Global.auth_check_get)
				checkme = true;

		} else if (msg instanceof PutMessage) { //|| msg instanceof PutReplyMessage) {
			if (Global.auth_check_put)
				checkme = true;

		} else if (msg instanceof JoinMessage) {
			if (Global.auth_check_join)
				checkme = true;

		} else if (msg instanceof JoinFinishedMessage || msg instanceof PairsMessage) {
			if (Global.auth_check_join)
				checkme = true;

		} else { // Unknown message type
			checkme = true; // Lets check it anyway!
			Global.stats.logCount("Auth" + SEPARATOR + "UnknownMessage");
		}

		if (checkme) {

			AuthValid lastAuth = AuthValid.FAILED;

			// Read the certificate chain backwards
			// and return the code for the last checkAuth;
			final List<AuthData> authChain = msg.getAuthChain();
			if (authChain != null) {
				for (int i = authChain.size() - 1; i >= 0; i--) {
					lastAuth = checkAuth(authChain.get(i));
				}
			}

			return lastAuth;
		}
		else
			return AuthValid.OK;

	}

	/**
	 *
	 * @param msg the message being received
	 * @param forUs whether or not the message is for this peer
	 * @return true if the message can be forwarded, otherwise false
	 */
	protected abstract boolean recv(Message msg, boolean forUs);

	public void recv(PongPacket p) {
		if (p.data instanceof TrackableObjectWrapper) {
			Object obj = ((TrackableObjectWrapper) p.data).get();
			if (obj instanceof NodeAddressPair) {
				NodeAddressPair pair = (NodeAddressPair) obj;

				// Find this node pair & update
				pair.rtt = p.getRoundTripTime();
				pair.failed = 0;

				//Trace.println(LogLevel.LOG2, this + ": recv " + p + " from " + pair);
			}
		}
	}

	abstract protected void recvError(UnreachablePacket packet);

	protected abstract void removeFromRoutingTable(long nodeID, String reason);

	protected void route(int address, Message msg) {

		if (hasFailed())
			return;

		if (msg.fromID != this.nodeID) {

			Global.stats.logCount("DHT" + SEPARATOR + "Message" + SEPARATOR + "Fwd");
			Global.stats.logCount("DHT" + SEPARATOR + Helper.getShortName(msg) + SEPARATOR + "Fwd");

			if (Global.auth_on & Global.auth_per_hop) {
				AuthValid valid = checkMessage(msg);

				Trace.println(LogLevel.LOG2, this + ": fwd  " + msg + " " + valid);

				// Add the message to a queue to be used later
				if (valid == AuthValid.WAIT) {
					msgQueue.add(msg);
					return;
				} else if (valid != AuthValid.OK) {
					// If the message has not been accepted, well drop it
					msg.dispose();
					return;
				}

			} else {
				Trace.println(LogLevel.LOG2, this + ": fwd  " + msg);
			}

		}

		// Since we are routing this message, flag it to be resendable
		msg.setResend(true);


		send(address, msg);
	}

	/**
	 * Tries to route the message to the msg.toID
	 * If it can't and the we are the nearest node it just gives up
	 * @param msg The message to send
	 */
	public void route(Message msg) {
		//Find the closest match to the msg.to ID, and route to that IP
		int address = getRoute(msg.toID);

		if (address == this.address) {
			return;
		}

		// HACK
		if (routingTable != null &&  routingTable.badLookup) {
			msg.setBadLookup();
		}

		route(address, msg);
	}
	/**
	 * Send a keepalive to the new host (to obtain delay data, and aliveness)
	 * @param pair The pair to ping
	 */
	protected void sendKeepAlive(NodeAddressPair pair) {

		//Invalidate the delay, and set that we just checked it
		pair.rtt = Integer.MAX_VALUE;
		pair.lastChecked = Events.getTime();
		pair.failed++;

		PingPacket p = PingPacket.newPacket(address, pair.address);
		p.data = new TrackableObjectWrapper(pair); // BAD HACK. pair should be changed to pair.nodeID

		send(p);

	}

	/**
	 * Overridded so we can catch the routing exception
	 */
	@Override
	public void send(Packet p) {
		try {
			super.send(p);
		} catch (RoutingException e) {
			Trace.println(LogLevel.WARN, this + ": WARNING " + e + " (" + p + ")");
		}
	}

	/**
	 * Sends keepalives to all hosts that have not been checked in the last
	 * KEEP_ALIVE_EVERY period.
	 *
	 */
	protected void sendKeepAlives() {
		final long now = Events.getTime();
		NodeAddressPairs remove = new NodeAddressPairs();

		Iterator<NodeAddressPair> i = leafSet.iterator();
		while (i.hasNext()) {
			NodeAddressPair pair = i.next();

			// If this peer has failed 3 times
			if (pair.failed > 3) {
				// Because we can't modify the leafSet during iteration, we place
				// This pair in another set for later removal
				remove.add(pair);
			} else {

				if ((now - pair.lastChecked) > KEEP_ALIVE_EVERY)
					sendKeepAlive(pair);

			}
		}

		i = remove.iterator();
		while (i.hasNext()) {
			NodeAddressPair pair = i.next();
			removeFromRoutingTable(pair.nodeID, "Keep alive failed 3 times");
		}
	}

	@Override
	public void setFailed(boolean failed) {
		if (failed)
			Trace.println(LogLevel.LOG2, this + ": fail " + Helper.getShortName(this) + " address " + Host.toString( getAddress() ));
		else
			Trace.println(LogLevel.LOG2, this + ": aliv " + Helper.getShortName(this) + " address " + Host.toString( getAddress() ));

		setJoined(false);

		super.setFailed(failed);
	}

	@Override
	public String toString() {
		return toString(this.nodeID, true);
	}

	public boolean hasJoined() {
		return hasJoined;
	}

	@Override
	public int compareTo(Host o) {
		if (o instanceof DHTInterface) {
			long id = ((DHTInterface) o).getID();
			if (nodeID > id)
				return 1;
			else if (nodeID < id)
				return -1;
		}

		return super.compareTo(o);
	}



}
