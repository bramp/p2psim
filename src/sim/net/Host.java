/*
 * Created on Feb 8, 2005
 */
package sim.net;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import sim.main.Global;
import sim.main.Helper;
import sim.net.links.Link;
import sim.net.multicast.MulticastManager;
import sim.stats.StatsObject;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;


/**
 * @author Andrew Brampton
 */
public abstract class Host implements Serializable, Comparable<Host>, Disposable {

	/** The IP address of this host **/
	protected int address;
	public static final int INVALID_ADDRESS = -1;

	protected transient List<Link> links = new ArrayList<Link>(3);

	protected boolean failed = false;

	/**
	 * Constructs a new host with this network address
	 * @param address
	 */
	public Host(final int address) {
		this.address = address;
		Global.hosts.add(this);
		Global.lastAddress++;
		Global.stats.logCount("Host" + StatsObject.SEPARATOR + Helper.getShortName(this));
	}

	/**
	 * This is called on the host whenever a packet is received
	 * @param link The link the packet came in on
	 * @param p The packet :)
	 */
	public abstract void recv(final Link link, final Packet p);

	public abstract void send(final Packet p) throws RoutingException;

	public void addLink(final Link c) throws InvalidHostException {
		if (links.size() >= Short.MAX_VALUE)
			throw new RuntimeException("Too many links");

		// This just checks if we are at one end
		c.getOtherHost(this);

		links.add(c);
	}

	public void removeLink(final Link c) {
		if (!links.remove(c))
			throw new RuntimeException("Removing a link not connected to this host");
	}

	public List<Link> getLinks() {
		return links;
	}

	public void printLinks() {
		Iterator<Link> i = links.iterator();
		while (i.hasNext()) {
			Link l = i.next();
			Trace.println(LogLevel.INFO, l.toString());
		}
	}

	public int getAddress() {
		return address;
	}

	// Map to cache the strings of these addresses
	private static String[] toStringCache = new String[0];
	public static String toString(final int address) {
		String ret;
		// check for multicast addresses
		if (MulticastManager.isMulticast(address)) {
			ret = MulticastManager.getGroupString(address);
		}
		else {
			// Check if more hosts have been added, (if so flush and expand the cache)
			if (toStringCache.length < Global.lastAddress)
				toStringCache = new String[Global.lastAddress];

			// Check the cache
			ret = toStringCache[address];
			if (ret == null) { // If no entry, make one :)
				ret = Integer.toHexString(address).toUpperCase();
				while (ret.length() < 4)
					ret = "0" + ret;

				//toStringCache.put(address, ret);
				toStringCache[address] = ret;
			}
		}

		return ret;
	}

	@Override
	public String toString() {
		return toString(address);
	}

	/**
	 * @return true if this host is now dead
	 */
	public boolean hasFailed() {
		return failed;
	}

	/**
	 * Set if this host has failed
	 * @param failed
	 */
	public void setFailed(final boolean failed) {

		if (failed == this.failed)
			return;

		this.failed = failed;

		if (failed) {
			Global.stats.logCount("Host" + StatsObject.SEPARATOR + "Failure");
		} else {
			Global.stats.logCount("Host" + StatsObject.SEPARATOR + "Recovery");
		}

		//Make the links dead or alive
		Iterator<Link> i = links.iterator();
		while (i.hasNext()) {
			Link l = i.next();

			// TODO If routers can fail, check the other end is also alive, before
			// bringing this back
			l.setFailed(failed);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(final Host o) {
		return address - o.address;
	}

	/**
	 * Cleans up all the links, to help with the GC
	 */
	public void dispose() {
		Iterator<Link> i = links.iterator();
		while (i.hasNext()) {
			i.next().dispose();
		}
		links.clear();
	}

	/**
	 * @param newAddress
	 */
	public void setAddress(int newAddress) {

		if (newAddress != address) {
			Global.stats.logCount("Net" + StatsObject.SEPARATOR + "AddressChange");
			Trace.println(LogLevel.LOG1, this + ": address old: " + Host.toString(address) + " new: " + Host.toString(newAddress));

			address = newAddress;
		}
	}

}
