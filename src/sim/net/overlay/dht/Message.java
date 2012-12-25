/*
 * Created on 13-Feb-2005
 */
package sim.net.overlay.dht;

import java.util.List;
import java.util.ArrayList;

import sim.events.Events;
import sim.main.Global;
import sim.main.Helper;
import sim.net.Disposable;
import sim.net.TrackableObject;
import sim.net.overlay.dht.authentication.AuthData;
import sim.net.overlay.dht.pastry.Peer;
import sim.stats.StatsObject;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

/**
 * @author Andrew Brampton
 */
public abstract class Message extends TrackableObject implements Disposable {

	/**
	 * ID of node this message is destined for
	 */
	public final long toID;

	/**
	 * ID of the node that this message orginiated from
	 */
	public long fromID;

	/**
	 * Network address of the node that originally sent this message
	 */
	public int fromAddress;


	/**
	 * The simulation time when the message was first sent
	 */
	public long startTime = Long.MIN_VALUE;

	/**
	 * Current hop count. Starts at 0 and is incremented each time it
	 * hops from one machine to the next.
	 */
	public int hop = 0;

	/**
	 * List of hops this packet has traveled
	 * Only used in debug mode
	 */
	public final List<DHTInterface> hops = Global.debug ? new ArrayList<DHTInterface>() : null;

	/**
	 * The ID of the last host to forward this message
	 */
	public long lastID;

	int resets = 0;

	boolean doresend = false;

	/**
	 * Size of this message
	 */
	public int size = 0;
	protected int payload_size = 0;

	/**
	 * The authenication data of the sender
	 * Contain one or more AuthDatas
	 */
	private List<AuthData> authchain;

	/**
	 * Somewhere to store some Out of band data
	 */
	public Object oob = null;

	/**
	 * Indicates if this message must be delivered or if the simulator can finish early
	 */
	public boolean critial = true;

	// HACK, Indicates if an invalid spot in the routing table was looked at while routing this message
	private boolean badLookup;

	//public boolean  fromproxyclient = false;

	public void setBadLookup() {
		badLookup = true;
		Global.stats.logCount("DHT" + StatsObject.SEPARATOR + "RTFailure" + hop);
	}

	public boolean getBadLookup() {
		return badLookup;
	}

	// has the message been tampered with in transit?
	private boolean tampered = false;

	public void setTampered() {
		if (!tampered) {
			tampered = true;
			Global.stats.logCount("DHT" + StatsObject.SEPARATOR + "Tamper");
			Global.stats.logCount("DHT" + StatsObject.SEPARATOR + "Tamper" + hop);
		}
	}

	public boolean getTampered() {
		return tampered;
	}



	/**
	 *
	 * @param fromAddress
	 * @param fromID
	 * @param toID
	 * @param size The size of the contents of this packet
	 */
	public Message(int fromAddress, long fromID, long toID, int size) {
		this.fromAddress = fromAddress;
		this.fromID = fromID;
		this.toID = toID;
		lastID = fromID;
		toStringCache = null;
		authchain = null;

		badLookup = false;

		setSize(size);

		Global.stats.logCount("DHT" + StatsObject.SEPARATOR + "Message");
		Global.stats.logCount("DHT" + StatsObject.SEPARATOR + Helper.getShortName(this));
	}

	public Message(int fromAddress, long fromID, long toID) {
		this(fromAddress, fromID, toID, 0);
	}

	public int getHopCount() {
		return hop;
	}

	public void addHop(DHTInterface host) {
		addHop(host, false);
	}

	/*
	 * If this is the last hop, don't update the hop count
	 */
	public void addHop(DHTInterface host, boolean finalHop) {
		if (startTime == Long.MIN_VALUE) {
			startTime = Events.getTime();
		}

		if (!finalHop) {
			hop++;
			lastID = host.getID(); // This is only used in swarm atm
		}

		// Don't record the path unless we are in debug mode
		if (Global.debug) {
			hops.add(host);
		}

		// Sanity check
		if (hop > Global.hosts.size()) {
			Trace.println(LogLevel.ERR, "ERROR: Jumped too many hops!");
			Trace.println(LogLevel.ERR, "ERROR: " + toString() + " " + hops);
			Global.fatalExit();
		}
	}

	public long getLastHop() {
		return lastID;
	}

	public int getDelay() {

		if (startTime == Long.MIN_VALUE) {
			//Trace.println(LogLevel.WARN, "Checking the delay, but its got an invalid startTime");
			return 0;
		}

		return (int) (Events.getTime() - startTime);
	}

	/**
	 * Resets the msg's hop count. Well actually just remove the last hop (since it failed)
	 * Useful for when we wish to resend this message
	 */
	public void reset() {

		if (hops != null && !hops.isEmpty())
			hops.remove( hops.size() - 1);

		hop--;
		resets++;

		// We can only resend hosts * hops times, otherwise something has gone wrong
		if (resets > Global.hosts.size() * (hop + 1)) {
			Trace.println(LogLevel.ERR, "Too many resends fatal error");
			Global.fatalExit();
		}
	}

	public int getResents() {
		return resets;
	}

	/**
	 * Override this to add addition info to the log
	 * @return
	 */
	protected String _toString() {
		return null;
	}

	public String toStringCache;

	@Override
	public final String toString() {

		if (toStringCache == null) {
			toStringCache = Helper.getShortName(this) + "(" + objectID + ")" +
			" from " + Peer.toString(fromID, true) +
			" for " + Peer.toString(toID, true) + " ";

			if (Global.auth_on) {
				if (authchain != null && !authchain.isEmpty()) {
					toStringCache += "auth ([";

					for (AuthData auth : authchain)
						toStringCache += auth + ", ";

					// Chop the ", " off the end
					toStringCache = toStringCache.substring(0, toStringCache.length() - 2);
					toStringCache += "]) ";
				}
				else
					toStringCache += "auth (null) ";
			}
		}

		return toStringCache +
			hop + " hops, " +
			getDelay() + " ms, " +
			getResents() + " resents, " +
			size + " size" +
			(_toString() != null ? ", " + _toString() : ""); // Add any addition info;

	}

	public void setResend(boolean resend) {
		doresend = resend;
	}

	public boolean getResend() {
		return doresend;
	}

	/**
	 * @param
	 */
	public void setSize(int size) {
		//size is what you pass in, plus headers (128bit toID, fromID, + extra)
		payload_size = size;
		this.size = payload_size + (16 + 16 + 8);

		// If auth is being used, add some extra fields
		if (Global.auth_on) {
			if (authchain == null)
				this.size += 1; // byte to say no auth in this message
			else {
				// The AuthData size + signature
				for (AuthData auth: authchain)
					this.size += auth.getSize() + Global.auth_key_size / 8;
			}
		}
	}

	/* (non-Javadoc)
	 * @see sim.net.TrackableObject#getSize()
	 */
	@Override
	public int getSize() {
		return size;
	}

	/* (non-Javadoc)
	 * @see sim.net.Disposable#dispose()
	 */
	public void dispose() {
		if (hops != null)
			hops.clear();

		if (authchain != null)
			authchain.clear();

		oob = null;
	}

	/**
	 * @param auth
	 */
	public void addAuth(AuthData auth) {
		if (!Global.auth_on)
			throw new RuntimeException("Message.addToAuthChain() when Global.auth_on is off!");

		if (this.authchain == null)
			authchain = new ArrayList<AuthData>(1);
		else
			if (authchain.contains(auth))
				return;

		authchain.add(auth);

		setSize(payload_size);
		toStringCache = null;
	}

	public final List<AuthData> getAuthChain() {
		if (!Global.auth_on)
			throw new RuntimeException("Message.getAuthChain() when Global.auth_on is off!");

		return authchain;
	}

	/**
	 * Returns the AuthData for the owner of this message
	 * @return
	 */
	public final AuthData getAuth() {
		if (!Global.auth_on)
			throw new RuntimeException("Message.getAuth() when Global.auth_on is off!");

		if (authchain == null)
			return null;

		return authchain.get(0);
	}

	public void clearAuthChain() {
		if (!Global.auth_on)
			throw new RuntimeException("Message.clearAuthChain() when Global.auth_on is off!");

		if (authchain != null)
			authchain.clear();

		authchain = null;
		setSize(payload_size);
		toStringCache = null;
	}
}
