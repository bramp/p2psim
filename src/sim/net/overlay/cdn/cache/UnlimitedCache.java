package sim.net.overlay.cdn.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import sim.collections.Range;
import sim.collections.Ranges;


/**
 * A unlimited sized cache
 * @author Andrew Brampton
 *
 */
public class UnlimitedCache extends Cache {

	/**
	 * How big the cache is (in bytes)
	 * -1 indicates a invalid value (and thus must be computed)
	 */
	protected long size;

	Map<Integer, Ranges> cache = new TreeMap<Integer, Ranges>();

	public UnlimitedCache () {}

	@Override
	public boolean add(int mediaID, Range range) {

		// Get the cache for thid media, otherwise make one
		Ranges ranges = cache.get(mediaID);
		if (ranges == null) {
			ranges = new Ranges();
			cache.put(mediaID, ranges);
		}

		// Add the range
		if ( ranges.add(range) ) {
			// Invalidate the size
			size = -1;

			return true;
		}

		return false;
	}

	@Override
	public boolean remove(int mediaID, Range range) {
		Ranges list = cache.get(mediaID);
		if (list == null)
			return false;

		if ( list.remove(range) ) {
			size = -1;
			return true;
		}

		return false;
	}

	public boolean remove(int mediaID, Ranges ranges) {
		boolean changed = false;

		for ( int i = 0; i < ranges.size(); i++ )
			changed |= remove(mediaID, ranges.get(i));

		return changed;
	}

	@Override
	public Range get(int mediaID, Range range) {
		Ranges list = cache.get(mediaID);
		if (list == null)
			return null;

		return list.overlap(range).first();
	}

	/**
	 * How much space is occupying this cache
	 */
	public long getSize() {
		// Check if the size is currently invalidated
		if (size == -1) {
			size = 0;
			Iterator<Entry<Integer, Ranges>> i = cache.entrySet().iterator();

			while (i.hasNext()) {
				size += i.next().getValue().length();
			}
		}
		return size;
	}

	public String toString() {
		String s = "";

		for ( Map.Entry<Integer, Ranges> m : cache.entrySet() ) {
			s += m.getKey() + ":";
			s += m.getValue() + " ";
		}

		return s;
	}

	protected void evict() {
		// Nothing ever gets evicted
		assert false;
	}

	public boolean has(int mediaID, Range r ) {
		// Get the list of ranges for this media
		Ranges list = cache.get(mediaID);
		if (list == null)
			return false;

		Ranges overlap = list.overlap(r);

		if (overlap == null || overlap.isEmpty())
			return false;

		return overlap.first().equals( r );
	}

	public boolean has(int mediaID, Ranges r ) {
		for (int i = 0; i < r.size(); i++)
			if (has(mediaID, r.get(i)) == false)
				return false;
		return true;
	}

	@Override
	public long getMaxSize() {
		return Long.MAX_VALUE;
	}

	/* (non-Javadoc)
	 * @see sim.net.overlay.cdn.cache.Cache#clear()
	 */
	@Override
	public void clear() {
		cache.clear();
		size = -1;
	}

}
