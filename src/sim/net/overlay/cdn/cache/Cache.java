package sim.net.overlay.cdn.cache;

import sim.collections.Range;
import sim.collections.Ranges;


public abstract class Cache {

	/**
	 * Adds a segment of media into the cache
	 * @param mediaID
	 * @param range
	 * @return
	 */
	public boolean add( int mediaID, long start, long end ) {
		return add(mediaID, new Range(start, end));
	}

	/**
	 * Removes a segment of media from the cache
	 * @param mediaID
	 * @param range
	 * @return
	 */
	public boolean remove(int mediaID, long start, long end ) {
		return remove(mediaID, new Range(start, end));
	}

	/**
	 * Gets a segment of media from the cache
	 * @param mediaID
	 * @param start
	 * @param end
	 * @return
	 */
	public Range get(int mediaID, long start, long end ) {
		return get(mediaID, new Range(start, end));
	}

	/**
	 * Do we have this full range?
	 * @param mediaID
	 * @param start
	 * @param end
	 * @return
	 */
	public boolean has(int mediaID, long start, long end ) {
		return has(mediaID, new Range(start, end));
	}

	/**
	 * Do we have this full range?
	 * @param mediaID
	 * @param r
	 * @return
	 */
	public abstract boolean has(int mediaID, Range r );

	/**
	 * Do we have this full ranges?
	 * @param mediaID
	 * @param r
	 * @return
	 */
	public abstract boolean has(int mediaID, Ranges r );

	/**
	 * Adds a segment of media into the cache
	 * @param mediaID
	 * @param range
	 * @return
	 */
	public abstract boolean add( int mediaID, Range range );

	/**
	 * Removes a segment of media from the cache
	 * @param mediaID
	 * @param range
	 * @return
	 */
	public abstract boolean remove(int mediaID, Range range);

	/**
	 * Gets a segment of media from the cache
	 * @param mediaID
	 * @param range
	 * @return
	 */
	public abstract Range get(int mediaID, Range range );

	/**
	 * Gets how many bytes are in the cache
	 * @return
	 */
	public abstract long getSize();

	/**
	 * Gets how many bytes are in the cache
	 * @return
	 */
	public abstract long getMaxSize();

	/**
	 * When the cache is too large this method is called to evict something
	 */
	protected abstract void evict();

	/**
	 * Empty the cache
	 */
	public abstract void clear();

}
