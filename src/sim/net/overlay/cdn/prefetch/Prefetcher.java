package sim.net.overlay.cdn.prefetch;

import java.util.ArrayList;
import java.util.List;

import sim.collections.Range;
import sim.net.overlay.cdn.Client;
import sim.net.overlay.cdn.Media;
import sim.net.overlay.cdn.Request;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

public abstract class Prefetcher {

	/**
	 * This is a offset to move the bookmark starts for prefetching.
	 * -30 means prefetch 30 seconds early, whereas +60 would mean start prefetching 60 seconds after the bookmark
	 */
	public final static int prefetchBookmarkOffset = 0;

	/**
	 * The amount of data a client should prefetch (in seconds)
	 * This value is only used if prefetchSizePercent fails (due to being zero, OR no hotspot length)
	 */
	public static int prefetchSizeInSec = 30;

	/**
	 * The amount of data a client should prefetch, compared to the
	 * length of the hotspot.
	 */
	public static double prefetchSizePercent = 0;

	/**
	 * Ratio of how quickly to prefetch, 0.5 means half video rate, 2 means twice video rate
	 */
	public static double prefetchRate = 1;


	/**
	 * The ranges (in bytes) to pre-fetch, in the order they should be requested
	 */
	final List<Range> prefetchOrder = new ArrayList<Range>( );

	/**
	 * The client using this pre-fetcher
	 * This is needed by some pre-fetchers that tend to cheat
	 */
	Client client;

	/**
	 * The media we are currently pre-fetching
	 */
	Media media = null;

	/**
	 * Clear the pre-fetch range
	 */
	void clearPrefetchRange() {
		prefetchOrder.clear();
	}

	/**
	 * Add a range to be pre-fetched at a particular index
	 * @param index
	 * @param start
	 * @param end
	 */
	void addPrefetchRange(final int index, final int start, final int end) {
		Range r = new Range ( media.getByteOffset(start), media.getByteOffset(end) );
		prefetchOrder.add(index, r);
	}	
	
	/**
	 * Adds a range to be prefetched
	 * @param m
	 * @param start Start time (in seconds)
	 * @param end End time (in seconds)
	 */
	void addPrefetchRange(final int start, final int end) {
		Range r = new Range ( media.getByteOffset(start), media.getByteOffset(end) );
		prefetchOrder.add(r);
	}

	/**
	 * Adds a range to be pre-fetched
	 * @param r The range in seconds (from the media)
	 */
	void addPrefetchRange(final Range r) {
		Range r2 = new Range ( media.getByteOffset( (int)r.start ), media.getByteOffset( (int)r.end ) );
		prefetchOrder.add(r2);		
	}

	/**
	 * Adds a ranges to be pre-fetched
	 * @param ranges The ranges in seconds (from the media)
	 */
	void addPrefetchRanges(final List<? extends Range> ranges) {
		for ( Range r : ranges )
			addPrefetchRange((int)r.start, (int)r.end);
	}

	/**
	 * Get the list of ranges to prefetch
	 * @return
	 */
	public List<Range> getPrefetchOrder() {
		return prefetchOrder;
	}

	/**
	 * Sets the media this pre-fetcher is for
	 * @param m
	 * @return Returns true if the media changes
	 */
	protected boolean setMedia( int m ) {
		
		final boolean changed = media == null ? true : media.getID() != m;
		media = Media.getMedia( m );

		if (changed)
			Trace.println(LogLevel.DEBUG, client + ": The media has changed " + m);

		return changed;
	}

	/**
	 * Set the client using this prefetcher
	 * @param c
	 */
	public void setClient(Client c) {
		assert c != null;
		this.client = c;
	}

	/**
	 * The client has made a new request, this may affect what to pre-fetch next
	 * @param r
	 * @param why
	 */
	public abstract void newRequest( final Request r, final String why );

}
