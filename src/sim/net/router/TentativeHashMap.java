/*
 * Created on Feb 11, 2005
 */
package sim.net.router;

import java.util.SortedSet;
import java.util.TreeSet;

import sim.net.Host;


/**
 * @author Andrew Brampton
 */
class TentativeHashMap {
	final Entry[] map;
	int count;

	final SortedSet<Entry> set;

	public TentativeHashMap( int hosts ) {
		map = new Entry[hosts];
		count = 0;
		set = new TreeSet<Entry>();
	}

	public Entry get(Host key) {
		return map[key.getAddress()];
	}

	public Entry put(Host key, Entry data) {
		data.host = key;
		set.add(data);

		Entry old = map[key.getAddress()];
		map[key.getAddress()] = data;

		if (old == null) {
			count++;
		}

		return old;
	}

	public Entry removeSmallest() {
		// Find the smallest entry
		Entry e = set.first();

		// Check if there is a entry to remove from the map
		if (map[e.host.getAddress()] != null) {
			count--;
			map[e.host.getAddress()] = null;
		}

		// Now remove from the sorted set
		set.remove(e);
		return e;
	}

	public void update(Entry data, int cost, int nextLink, int hops) {
		set.remove(data);

		data.cost = cost;
		data.link = nextLink;
		data.hops = hops;

		set.add(data);
	}

	public boolean isEmpty() {
		return count == 0;
	}
}
