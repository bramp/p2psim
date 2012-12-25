/*
 * Created on Feb 8, 2005
 */
package sim.net.router;

import java.util.Iterator;
import java.util.List;

import sim.collections.IntVector;
import sim.main.Global;
import sim.net.BrokenLinkException;
import sim.net.ErrorPacket;
import sim.net.Host;
import sim.net.InvalidHostException;
import sim.net.Packet;
import sim.net.RoutingException;
import sim.net.UnreachablePacket;
import sim.net.links.Link;
import sim.net.multicast.LinkMulticastRecords;
import sim.net.multicast.MulticastJoinPacket;
import sim.net.multicast.MulticastManager;
import sim.net.multicast.MulticastPacket;
import sim.stats.StatsObject;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

/**
 * @author Andrew Brampton
 */
public class Router extends Host {

	/**
	 * Variables used to represent invalid items in the tables
	 */
	final static int INVALID_LINK = -1;
	final static int INVALID_COST = Integer.MAX_VALUE;
	final static int INVALID_HOPS = Integer.MAX_VALUE;

	/**
	 * The routing table, A list of ints representing the link to use for a certain destination
	 * ie use links.get(routingTable[nodeAddress]);
	 */
	int routingTable[] = null;

	/**
	 * A table with the Cost to each host
	 */
	int routingCostTable[] = null;

	/**
	 * A table with the number of hops to each host
	 */
	int routingHopsTable[] = null;

	/**
	 * Link & Multicast group membership records
	 * Contains group address to (link # & last refreshed) mapping
	 */
	LinkMulticastRecords multicastRecords = null;


	/**
	 * @param address
	 */
	public Router(int address) {
		super(address);
	}

	/**
	 * Create all the Router's routing tables, using Floyd-Warshall algorithm
	 * used http://en.wikipedia.org/wiki/Floyd-Warshall_algorithm
	 * @throws InvalidHostException
	 */
	/*
	public static void createRoutingTables() throws InvalidHostException {
		HostSet hosts = Global.hosts.getType(Host.class);

		// Create a NxN graph
		int[][] dist = new int[hosts.size()][hosts.size()];
		int[][] pred = new int[hosts.size()][hosts.size()];

		// Fill dist up with the current graph
		for (int i = 0; i < dist.length; i++) {
			for (int j = 0; j < dist[i].length; j++) {
				dist[i][j] = Integer.MAX_VALUE;
				pred[i][j] = 0;
			}
		}
		for (int i = 0; i < dist.length; i++)
			dist[i][i] = 0;

		Iterator<Host> h = hosts.iterator();
		while (h.hasNext()) {
			Iterator<Link> ii = h.next().getLinks().iterator();

			while (ii.hasNext()) {
				Link l = ii.next();
				int i = l.get(0).getAddress();
				int j = l.get(1).getAddress();

				dist [ i ][ j ] = l.getCost();
				dist [ j ][ i ] = l.getCost();

				pred[i][j] = i;
				pred[j][i] = j;
			}
		}

		// Main loop of the algorithm
		for (int k = 0; k < dist.length; k++) {
			for (int i = 0; i < dist.length; i++) {
				for (int j = 0; j < dist[i].length; j++) {
					if (dist[i][k] != Integer.MAX_VALUE && dist[k][j] != Integer.MAX_VALUE) {
						if (dist[i][j] > dist[i][k] + dist[k][j]) {
							dist[i][j] = dist[i][k] + dist[k][j];
							pred[i][j] = pred[k][j];
						}
					}
				}
			}
		}

		for (int i = 0; i < dist.length; i++) {
			String line = "";

			for (int j = 0; j < dist[i].length; j++) {
				line += dist[i][j] + " ";
			}
			Trace.getInstance().println(LogLevel.DEBUG, line);
		}
	}*/


	/**
	 * Create all the Router's routing tables, using Dijkstra's algorithm
	 * @throws InvalidHostException
	 */
	public static void createRoutingTables() throws InvalidHostException {
		Iterator<Host> routers = Global.hosts.getType(Router.class).iterator();
		while (routers.hasNext()) {
			Router r = (Router) routers.next();
			r.createRoutingTable( Global.hosts.size() );
		}

		/*
		// Prints out the full routing matrix (for debugging)
		for (int i = 0; i < Global.hosts.size() ; i++) {
			String line = "";

			for (int j = 0; j < Global.hosts.size(); j++) {
				line += ((Router)Global.hosts.get(i)).getDelay(j) + " ";
			}
			Trace.getInstance().println(LogLevel.DEBUG, line);
		}
		*/
	}

	/**
	 * Creates the routing table for this Router (using Dijkstra's algorithm)
	 * @param maxNodes
	 * @throws InvalidHostException
	 */
	protected void createRoutingTable(final int maxNodes) throws InvalidHostException {
		// initialise multicast records
		multicastRecords = new LinkMulticastRecords(links.size());

		// begin dijkstra
		TentativeHashMap tentative = new TentativeHashMap(maxNodes);

		Host next;

		Entry nextEntry;
		boolean first = true;

		routingTable = new int[maxNodes];
		routingCostTable = new int[maxNodes];
		routingHopsTable = new int[maxNodes];

		for (int i =0; i < routingTable.length; i++) {
			routingTable[i] = INVALID_LINK;
			routingCostTable[i] = INVALID_COST;
			routingHopsTable[i] = INVALID_HOPS;
		}

		// Create a entry for ourself
		next = this;
		nextEntry = new Entry(0, INVALID_LINK, 0);
		nextEntry.host = this;
		routingHopsTable[next.getAddress()] = 0; // Hops to ourself is always 0

		// TODO Consider adding all the SimpleHosts (connected by links
		// to me) to the confirmed list here. This might speed up things
		// This would also mean changing what to do on finish. Loop confirmed
		// adding to the table, instead of adding to the table when confirmed.

		int nextCost;
		int nextLink;
		int nextHops;
		List<Link> nextLinks;

		do {
			nextCost = nextEntry.cost;
			nextLink = nextEntry.link;
			nextHops = nextEntry.hops;
			nextLinks = next.getLinks();

			for (int i = 0; i < nextLinks.size(); i++) {

				Link l = nextLinks.get(i);
				Host neighbour = l.getOtherHost(next);
				int cost = nextCost + l.getCost();
				int hops = nextHops + 1;

				if (first)
					nextLink = i;

				Entry oldNeigh = tentative.get(neighbour);

				if (oldNeigh != null) {
					//Step b
					if (cost < oldNeigh.cost) {
						//tentative.remove(oldNeigh);
						//tentative.put(neighbour, new Entry(cost, nextLink, hops));
						tentative.update(oldNeigh, cost, nextLink, hops);
					}

				// Check if we have found a solution already or not
				} else if (routingHopsTable[neighbour.getAddress()] == INVALID_HOPS) {
					//Step a
					tentative.put(neighbour, new Entry(cost, nextLink, hops));
				}
			}
			first = false;

			if (!tentative.isEmpty()) {
				nextEntry = tentative.removeSmallest();
				next = nextEntry.host;
				final int nextAddress = next.getAddress();
				routingTable[nextAddress] = nextEntry.link;
				routingCostTable[nextAddress] = nextEntry.cost;
				routingHopsTable[nextAddress] = nextEntry.hops;
			} else {
				 //Return only when tentative is empty
				//Cleanup a little
				tentative = null;

				return;
			}
		} while (true);
	}

	public void printRoutingTable() {
		for (int i = 0; i<routingTable.length; i++) {
			int l = routingTable[i];
			if (l == INVALID_LINK)
				Trace.println(LogLevel.INFO, new Router(i).toString() + " null");
			else
				Trace.println(LogLevel.INFO, new Router(i).toString() + " " + links.get(l));
		}
	}

	@Override
	public void send(final Packet p) throws RoutingException {

		try {
			int lnumber = INVALID_LINK;

			// check for multicast packets
			if (MulticastManager.isMulticast(p.to)) {
				// find the root using global lookup
				int root = -1;
				try {
					root = Global.groups.getRootAddress(p.to);
				}
				catch(Exception e) {
					throw new RoutingException(e.getMessage());
				}

				if (p.getClass().equals(MulticastJoinPacket.class)) {
					// send join packets towards the root only
					lnumber = routingTable[root];
					try {
						Trace.println(LogLevel.DEBUG, this.address + " added link " + lnumber);
						multicastRecords.addRecord(p.to, lnumber);
						Trace.println(LogLevel.DEBUG, "Current records " + multicastRecords.getLinkNumbers(p.to));
					}
					catch (Exception e) {
						throw new RoutingException(e.getMessage());
					}
				} else {
					// duplicate the packet while avoiding the origin link
					// NB assumes link symmetry
					IntVector lnumbers = multicastRecords.getLinkNumbers(p.to);
					Trace.println(LogLevel.DEBUG, "Multicast links at " + this.address + " : " + lnumbers);
					Trace.println(LogLevel.DEBUG, "Ignoring origin link " + routingTable[p.from]);
					lnumbers.removeElement(routingTable[p.from]);
					if (!lnumbers.isEmpty()) {
						Trace.println(LogLevel.DEBUG, lnumbers + " is not empty");
						// use the last link as the path for the original packet
						lnumber = lnumbers.lastElement();
						Trace.println(LogLevel.DEBUG, lnumber + " will be the default path");
						lnumbers.remove(lnumbers.size()-1);

						// duplicate as required
						if (!lnumbers.isEmpty()) {
							Trace.println(LogLevel.DEBUG, lnumbers + " is still not empty");
							for (int i=0;i < lnumbers.size();i++) {
								int dupeLinkNumber = lnumbers.elementAt(i);
								Link dupeLink = links.get(dupeLinkNumber);

								Trace.println(LogLevel.DEBUG, "Duplicated on link " + dupeLinkNumber + " at " + this.address);
								//try{Thread.sleep(1000);}catch(Exception e){}
								MulticastPacket dupe = ((MulticastPacket)p).duplicate();
								dupeLink.send(this, dupe);
							}
						}
					}
				}
			}
			// otherwise handle as normal
			else {
				lnumber = routingTable[p.to];
			}

			if (lnumber == INVALID_LINK) {
				throw new BrokenLinkException("Can't route to " + Host.toString( p.to ));
			}

			Link link = links.get(lnumber);

			link.send(this, p);


		} catch (BrokenLinkException e) {
			// Send a "unreachable" packet back faking the destinations IP
			// Only if this wasn't a ErrorPacket to begin with
			if (!(p instanceof ErrorPacket))
				send(UnreachablePacket.newPacket(p.to, p.from, p));
		} catch (InvalidHostException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see net.bramp.p2psim.Host#recv(net.bramp.p2psim.Link, net.bramp.p2psim.Packet)
	 */
	@Override
	public void recv(final Link link, final Packet p) {
		// if the packet is a multicast packet, update our records accordingly
		if (MulticastManager.isMulticast(p.to)) {
			try {
				Trace.println(LogLevel.DEBUG, this.address + " added link " + links.indexOf(link));
				multicastRecords.addRecord(p.to, links.indexOf(link));
				Trace.println(LogLevel.DEBUG, "Current records " + multicastRecords.getLinkNumbers(p.to));
			}
			catch(Exception e) {
				Trace.println(LogLevel.ERR, "ERROR: " + e.getMessage());
			}
		}

		//If the packet is for me, look at it
		if (p.to == this.address) {
			Trace.println(LogLevel.ERR, "ERROR: Router recv:" + this + " P(" + p + ")");
		} else { //ELSE Forward it on to the most sensisble link
			try {
				send(p);
			} catch (RoutingException e) {
				Trace.println(LogLevel.WARN, this + ": WARNING " + e + " (" + p + ")");
			}
		}
	}

	@Override
	public void dispose() {
		routingTable = null;
		routingCostTable = null;
		routingHopsTable = null;
		super.dispose();
	}

	/**
	 * Returns the delay to this address
	 * @param address
	 */
	public int getDelay(final int address) {
		return routingCostTable[address];
	}

	/**
	 * Returns the hops to this address
	 * @param address
	 */
	public int getHops(final int address) {
		return routingHopsTable[address];
	}


	/**
	 * This remove this Host from the routing table
	 * @param host
	 */
	public void remoteRoutingEntry(final int host) {
		routingTable[host] = INVALID_LINK;
		routingCostTable[host] = INVALID_COST;
		routingHopsTable[host] = INVALID_HOPS;
	}

	protected void growTables() {
		// If not lets increase its size
		int newsize = (int) (routingTable.length * 1.5 + 1);

		// Alloc new memory
		int newTable[] = new int[newsize];
		int newCostTable[] = new int[newsize];
		int newHopsTable[] = new int[newsize];

		// Copy old table
		System.arraycopy(routingTable,     0, newTable,     0, routingTable.length);
		System.arraycopy(routingCostTable, 0, newCostTable, 0, routingTable.length);
		System.arraycopy(routingHopsTable, 0, newHopsTable, 0, routingTable.length);

		// Fill all the empty areas with blanks
		for (int i = routingTable.length; i < newsize; i++) {
			newTable[i] = INVALID_LINK;
			newCostTable[i] = INVALID_COST;
			newHopsTable[i] = INVALID_HOPS;
		}

		// Update vars
		routingTable = null; // Nulls are to try and help GC
		routingTable = newTable;
		routingCostTable = null;
		routingCostTable = newCostTable;
		routingHopsTable = null;
		routingHopsTable = newHopsTable;

		// Log this so we can see how many times it gets called
		if (Global.debug)
			Global.stats.logCount("Debug" + StatsObject.SEPARATOR + "RoutingTableGrow");
	}

	/**
	 * Updates the routing entry for a host.
	 * It attachs the host to the new router, with a added cost between
	 * the router and the host
	 * @param host
	 * @param newrouter
	 */
	public void updateRoutingEntry(final int host, final int newrouter, final int cost) {

		// Check the routing table is large enough
		while (host >= routingTable.length) {
			growTables();
		}

		// If I'm not the newrouter do the update
		if (newrouter != address) {
			routingTable[host] = routingTable[newrouter];
			routingCostTable[host] = routingCostTable[newrouter] + cost;
			routingHopsTable[host] = routingHopsTable[newrouter] + 1;
		}
	}

	/**
	 * Updates the routing entry on ALL routers for a host.
	 * It attachs the host to the new router, with a added cost between
	 * the router and the host
	 * @param host
	 * @param newrouter
	 */
	public static void updateAllRoutingEntry(final int host, final int newrouter, final int cost) {
		Iterator<Host> i = Global.hosts.getType(Router.class).iterator();
		while (i.hasNext()) {
			((Router)i.next()).updateRoutingEntry(host, newrouter, cost);
		}
	}

	/* (non-Javadoc)
	 * @see sim.net.Host#addLink(sim.net.links.Link)
	 */
	@Override
	public void addLink(final Link c) throws InvalidHostException {
		//if (routingTable != null)
		//	throw new RuntimeException("Can't add link once routingtable is made");

		super.addLink(c);

		if (routingTable != null) {
			int host = c.getOtherHost(this).getAddress();
			int link = getLinks().indexOf(c);

			// Check the routing table is large enough
			while (host >= routingTable.length) {
				growTables();
			}

			// I now know I'm directly connected to this guy, so lets add him to
			// the routing table
			routingTable[host] = link;
			routingCostTable[host] = c.getCost();
			routingHopsTable[host] = 1;
		}
	}

	/* (non-Javadoc)
	 * @see sim.net.Host#removeLink(sim.net.links.Link)
	 */
	@Override
	public void removeLink(final Link c) {
		//if (routingTable != null)
		//	throw new RuntimeException("Can't add link once routingtable is made");

		// When this happens the routingtable becomes out of date because we used
		// fixed number positions into the links array. ie routingTable[host] returns
		// 1, and then we do getLinks().get(1). Thus if we remove link 0, 1 will be
		// invalid :(. Thus we must do some shuffling

		int oldidx = links.indexOf(c);
		super.removeLink(c);

		// Remove the other end from our routing table
		try {
			remoteRoutingEntry(c.getOtherHost(this).getAddress());
		} catch (InvalidHostException e) {
			e.printStackTrace();
		}

		// Check that any links got moved down
		if (oldidx >= links.size())
			return;

		for (int i = 0; i < routingTable.length; i++) {
			if (routingTable[i] > oldidx)
				routingTable[i]--;
			else if (routingTable[i] == oldidx) {
				// This line shouldn't be called if we never remove routers
				// Infact I'm going to throw a error just to be sure
				// This can obviously be removed when/if we allow routers to be
				// removed
				throw new RuntimeException("We are removing a link to a router!");
			}
		}

	}
}
