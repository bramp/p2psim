package sim.net.overlay.cdn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sim.collections.BiMap;
import sim.collections.Range;
import sim.events.Events;
import sim.main.Global;
import sim.net.overlay.cdn.AbstractClient;
import sim.net.overlay.cdn.cache.Cache;
import sim.net.overlay.cdn.prefetch.Prefetcher;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

import static sim.stats.StatsObject.SEPARATOR;

/**
 * @author Andrew Brampton
 *
 */
public class Client extends AbstractClient {

	/**
	 * The current default request
	 */
	ClientRequest defaultRequest = null;

	/**
	 * List of ranges currently being prefetched, with their associated ClientRequest
	 */
	BiMap<Range, ClientRequest> prefetchs;

	/**
	 * The prefetcher this client is using
	 */
	Prefetcher prefetcher;

	int requestsCount = 0;

	/**
	 * @param address The address of this node
	 * @param cache What cache to use
	 */
	public Client(int address, Cache cache, Prefetcher prefetcher) {
		super(address, cache);

		this.prefetcher = prefetcher;
		prefetcher.setClient(this);

		this.prefetchs = prefetcher == null ? null : new BiMap<Range, ClientRequest>();
	}


	protected void incomingMedia(MediaPacket p) {

		ClientRequest request = requests.get(p.request);

		if ( request != null) {

			if (request.isPrefetch()) {
				// Check how much duplicate prefetch data we download (if any)
				Range r = prefetchCache.get(p.media, p.start, p.end);

				prefetchCache.add ( p.media, p.start, p.end ) ;

				// Record how much we Prefetch
				Global.stats.logRunningTotal("CDN" + SEPARATOR + "Prefetch" + SEPARATOR + "Total", p.end - p.start);

				if ( r != null) {
					Global.stats.logRunningTotal("CDN" + SEPARATOR + "DuplicateData", r.length());
					Trace.println(LogLevel.DEBUG, this + ": Downloading duplicate prefetch data :( " + p + " " + request + " " + r);

					// Start a prefetch and hopefully pick something else to prefetch
					prefetcher.newRequest(defaultRequest, "Downloading duplicate prefetch data");

					startPrefetch(p.from, Media.getMedia( p.media ));
				}

			} else {
				// Check what we have in our cache
				checkCache(cache, p.media, p.start, p.end, "CDN" + SEPARATOR + "ClientCache" + SEPARATOR);
				checkCache(prefetchCache, p.media, p.start, p.end, "CDN" + SEPARATOR + "PrefetchCache" + SEPARATOR);

				// Record how long it took between the request, and now
				if ( !request.loggedLatency ) {
					Global.stats.logAverage("CDN" + SEPARATOR + "SeekLatency", Events.getTime() - request.startTime);
					request.loggedLatency = true;
				}

				// Record how much we have downloaded
				Global.stats.logRunningTotal("CDN" + SEPARATOR + "Data" + SEPARATOR + "Total", p.end - p.start);
			}
		}

		super.incomingMedia(p);
	}

	/**
	 * Generates a new request ID
	 */
	protected int newRequestID() {
		return requestsCount++;
	}

	/**
	 * Is called when a request finishes naturally
	 * @param server
	 * @param request
	 */
	protected void notifyRequestFinished(int server, ClientRequest request) {

		Trace.println(LogLevel.LOG1, this + ": " + request + " naturally finished after " + (Events.getTime() - request.startTime) + "ms" );

		if ( request == defaultRequest )
			defaultRequest = null;

		// Figure out if we should start prefetching something else
		if ( request.isPrefetch() ) {
			removePrefetch(request);

			startPrefetch(server, Media.getMedia( request.media ) );
		}
	}

	/**
	 * Causes the client to pause the default request
	 * @param server
	 */
	public void pauseRequest(final int server) {
		if (defaultRequest != null)
			pauseRequest( server, defaultRequest );
	};

	/**
	 * Figures out what to pre-fetch next
	 * @return
	 */
	void startPrefetch(final int server, Media m) {

		// Check if pre-fetching is turned on or not
		if (prefetcher == null)
			return;

		// A List of Ranges to pre-fetch
		List<Range> toPrefetch = new ArrayList<Range>();

		Iterator<Range> i = prefetcher.getPrefetchOrder().iterator();
		
		// Loop through each prefetchOrder, until we find the which things we don't already have
		while( toPrefetch.size() < 1 && i.hasNext() ) {

			Range prefetch = i.next();

			// Get what range of the pre-fetch we already have
			Range r = cache.get(m.getID(), prefetch.start, prefetch.end);

			if (r != null) {

				// Check if we have the full range
				if ( r.start == prefetch.start && r.end == prefetch.end ) {
					// As a optimisation, remove this from the prefetchOrder so we don't consider it next time
					i.remove();

					continue;

				// If we already have the beginning we can start later
				} else if (r.start <= prefetch.start) {
					prefetch.start = r.end;

				// If the cached starts after the prefetch start, then we can end early
				} else if (r.start > prefetch.start) {
					prefetch.end = r.start;
				}
			}

			toPrefetch.add( prefetch );
		}

		// Remove prefetchs we are currently doing, and stop old prefetch requests that we
		// don't need anymore
		i = prefetchs.keySet().iterator();
		while (i.hasNext() ) {
			Range prefetch = i.next();

			// Check if we are already prefetching this request
			if ( toPrefetch.contains( prefetch ) ) {
				// No need to prefetch this again
				toPrefetch.remove( prefetch );
			} else {
				// We aren't going to be needing this prefetch anymore :)
				stopRequest(server, prefetchs.get(prefetch) );

				i = prefetchs.keySet().iterator();
			}
		}

		// Check if we have anything to prefetch, if not bail out
		if ( toPrefetch.isEmpty() ) {
			Trace.println(LogLevel.LOG1, this + ": Nothing more to prefetch" );
			Global.stats.logCount("CDN" + SEPARATOR + "PrefetchStale");

			return;
		}

		Trace.println(LogLevel.LOG1, this + ": Prefetch List " + prefetcher.getPrefetchOrder() );

		// Loop each Prefetch we need to start
		i = toPrefetch.iterator();

		while ( i.hasNext() ) {
			Range prefetch = i.next();

			// Start prefetching
			Trace.println(LogLevel.LOG1, this + ": Prefetching " + prefetch );

			// This is a prefetch request, so tweak the prefetchRate (if needed)
			int byterate = m.getByterate();
			byterate *= Prefetcher.prefetchRate;

			// Start the new prefetch
			ClientRequest c =
				new ClientRequest(newRequestID(), m.getID(), prefetch.start, prefetch.end, byterate, true);

			startRequest(server, c);

			prefetchs.put( prefetch, c  );
		}
	}

	protected void startRequest(int server, ClientRequest c) {

		if ( ! c.isPrefetch() )
			defaultRequest = c;

		super.startRequest(server, c);
	}

	/**
	 * Makes a request for some media, from a server
	 * @param server
	 * @param m
	 * @param start In bytes
	 * @param end In bytes
	 * @param why
	 */
	public void startRequest(final int server, final Media m, final long start, long end, String why) {

		assert m != null;
		assert start >= 0;
		assert start != end;
		assert start < end || end == -1;

		// Stop the old request
		if (defaultRequest != null)
			stopRequest(server, defaultRequest);

		ClientRequest c = new ClientRequest(newRequestID(), m.getID(), start, end);

		startRequest(server, c);
		
		// On a new request we need to change the predictive list
		prefetcher.newRequest(c, why);

		// Now start the prefetch for this media
		startPrefetch(server, m);
		
		// If we have the first second of media we can switch over instantly
		final long start1 = m.getByteOffset( m.getSecondOffset(start) + 1 );
		if ( cache.has(m.getID(), start, start1 ) ) {

			Global.stats.logAverage("CDN" + SEPARATOR + "SeekLatency", 0);
			Global.stats.logCount("CDN" + SEPARATOR + "InstantSeek" + SEPARATOR + "Hit");

			c.loggedLatency = true;

		} else {
			Global.stats.logCount("CDN" + SEPARATOR + "InstantSeek" + SEPARATOR + "Miss");
		}		
	}

	public void stopRequest(final int server, ClientRequest request) {
		assert request != null;

		super.stopRequest(server, request);

		if ( request == defaultRequest )
			defaultRequest = null;

		// Check if this request is one of the prefetch
		if ( request.isPrefetch() )
			removePrefetch(request);
	}

	/**
	 * When a Request finishes this makes sure the prefetch state (if any) is removed
	 * @param r
	 */
	private void removePrefetch(ClientRequest r) {
		assert r.isPrefetch();

		Range range = prefetchs.getKey(r);
		if ( range != null )
			prefetchs.remove(range);
	}
}
