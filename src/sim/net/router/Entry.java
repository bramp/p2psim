/*
 * Created on Feb 11, 2005
 */
package sim.net.router;

import sim.net.Host;

class Entry implements Comparable<Entry> {
	public int cost;
	public int link;
	public int hops;
	public Host host;

	public Entry(int cost, int link, int hops) {
		this.cost = cost;
		this.link = link;
		this.hops = hops;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Entry e) {
		//If the costs are the same, then compare on names
		if (cost == e.cost)
			return host.getAddress() - e.host.getAddress();

		return cost - e.cost;
	}
}