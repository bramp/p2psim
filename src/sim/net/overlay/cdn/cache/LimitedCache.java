package sim.net.overlay.cdn.cache;

import sim.collections.Range;
import sim.collections.Ranges;

/**
 * A cache of Media
 * Stores which ranges of which media the cache has
 * @author Andrew Brampton
 *
 */
public abstract class LimitedCache extends UnlimitedCache {

	long maxSize;

	/**
	 * Creates a new cache
	 * @param maxSize Max size of the cache in bytes
	 */
	public LimitedCache (long maxSize) {
		this.maxSize = maxSize;
	}

	@Override
	public boolean add(int mediaID, Range range) {
		// Issue a request for this, so we can keep track of it
		request( mediaID, range );

		if ( super.add(mediaID, range) ) {
			if (getSize() > maxSize)
				evict();

			return true;
		}

		return false;
	}

	@Override
	public Range get(int mediaID, Range range) {
		Range ret = super.get(mediaID, range);

		// Now log that this range was recently used
		request(mediaID, range);

		return ret;
	}

	protected abstract void request(int mediaID, Range range);
	protected abstract void evict();

	protected void evicted(int mediaID, final Range range) {
		//System.out.println("Evicted " + range + " " + this);
	}

	protected void evicted(int mediaID, final Ranges ranges) {
		for (int i = 0; i < ranges.size(); i++)
			evicted (mediaID, ranges.get(i));
	}

	protected long evictMinimum( int mediaID, final Range r , long over) {
		// If over is smaller than this range, move the start
		if (r.length() > over)
			r.start = r.end - over;

		// Check we have this full range (otherwise over-= would be wrong)
		assert( has(mediaID, r) );

		//r.remove(r2);
		remove(mediaID, r);
		over -= r.length();

		evicted(mediaID, r);

		return over;
	}

	/**
	 *  Evicts the bare minimum from the ranges given
	 */
	protected long evictMinimum( int mediaID, final Ranges r , long over) {

		assert (over > 0);
		assert (r.length() >= over);

		// Loop around keep getting the end of this range,
		// and removing as much as we can each time
		while ( over > 0 ) {
			assert (!r.isEmpty());

			Range del = r.last();

			over = evictMinimum ( mediaID, del, over);
			r.remove( del );
		}
		return over;
	}

	public long getMaxSize() {
		return maxSize;
	}

}
