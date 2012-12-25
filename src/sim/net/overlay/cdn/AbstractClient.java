package sim.net.overlay.cdn;

import static sim.stats.StatsObject.SEPARATOR;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import sim.collections.Range;
import sim.events.Events;
import sim.main.Global;
import sim.net.ErrorPacket;
import sim.net.Packet;
import sim.net.RoutingException;
import sim.net.SimpleHost;
import sim.net.links.Link;
import sim.net.overlay.cdn.cache.Cache;
import sim.net.overlay.cdn.cache.UnlimitedCache;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

/**
 * @author Andrew Brampton
 *
 */
public abstract class AbstractClient extends SimpleHost {

	/**
	 * The cache that this node uses to store the media
	 */
	final Cache cache;

	/**
	 * This cache contains just the media which was prefetched
	 * It is only used as a way to track which data was prefetched and which wasn't
	 */
	final Cache prefetchCache = new UnlimitedCache();

	/**
	 * Are we currently paused?
	 */
	boolean isPaused = false;

	/**
	 * A map of each active request
	 */
	final Map<Integer, ClientRequest> requests = new TreeMap<Integer, ClientRequest>();

	/**
	 * A list of old requests so we can keep track of any late arriving packets
	 * TODO associate the time with this, so we remove the entry after X seconds, OR after X newer requests
	 */
	final Set<Integer> oldRequests = new TreeSet<Integer>();

	/**
	 * @param address
	 */
	public AbstractClient(int address, Cache cache) {
		super(address);
		this.cache = cache;
	}

	/**
	 * Checks the cache and returns the number of bytes missing
	 * Also logs hit/miss ratios
	 * @param media The
	 * @param start
	 * @param end
	 * @param cacheName
	 * @return the number of bytes missing
	 */
	protected long checkCache(final Cache cache, final int media, final long start, final long end, final String cacheName) {

		assert start >= 0;
		assert end > start;

		// Check we have that in our cache
		Range data = cache.get(media, start, end);
		final long len = end - start;

		final long miss;
		if (data == null) {
			miss = len;
		} else {
			miss = len - data.length();
		}

		long hit = len - miss;

		assert miss >= 0;
		assert hit >= 0;
		assert miss + hit == len;

		//Trace.println(LogLevel.LOG2, this + ": tick " + hit + " " + miss);

		Global.stats.logRunningTotal(cacheName + "ByteHit", hit);
		Global.stats.logRunningTotal(cacheName + "ByteMiss", miss);

		if (miss == 0)
			Global.stats.logCount(cacheName + "Hit");
		else
			Global.stats.logCount(cacheName + "Miss");

		// If this is set, then log which seconds were hit/miss
		if ( Common.logMissHit && cacheName.equals( "CDN" + SEPARATOR + "ClientCache" + SEPARATOR ) ) {
			final Media m = Media.getMedia ( media );

			final int startS = m.getSecondOffset( start );
			final int endS = m.getSecondOffset( end );

			if ( data == null ) {
				//System.out.println(start + " " + end + " " + startS + " " + endS);
				Common.miss.add( startS, endS, 1 );

			} else {
				int dataStartS = m.getSecondOffset( data.start );
				int dataEndS =  m.getSecondOffset( data.end );

				Common.hits.add( dataStartS, dataEndS, 1 );

				if ( data.start > start )
					Common.miss.add( startS, dataStartS, 1 );

				if ( data.end < end )
					Common.miss.add( dataEndS, endS, 1 );
			}
		}

		return miss;
	}

	/**
	 * Deals with an incoming segment of media
	 * @param p
	 */
	protected void incomingMedia(MediaPacket p) {

		// Store this media in the cache
		cache.add(p.media, p.start, p.end);

		ClientRequest c = requests.get( p.request );
		if ( c != null) {
			c.position = p.end;

			// We have finished this request
			if ( c.position >= c.end ) {
				notifyRequestFinished(p.from, c);

				// Remove this request
				requests.remove(c.requestID);

				oldRequests.add(c.requestID);
			}
		} else {
			// Sometimes the request has been stopped, and we receive a couple remaining packets
			if ( !oldRequests.contains( p.request ) )
				Trace.println(LogLevel.ERR, this + ": ERROR MediaPacket received for unknown request " + p.request);
		}
	}

	/**
	 * @param from
	 * @param c
	 */
	protected void notifyRequestFinished(int from, ClientRequest c) {}


	/**
	 * Causes the client to pause this request,
	 * Pause acts like a stop
	 * @param server The server to send this pause
	 * @param request The request to pause
	 */
	public void pauseRequest(final int server, ClientRequest c) {
		assert c != null;

		// TODO Should pause act like a stop, OR continue to buffer?
		//send ( PausePacket.newPacket(this.address, server, request) );
		stopRequest(server, c);
	}

	@Override
	public void recv(Link link, Packet p) {
		super.recv(link, p);

		if (p instanceof MediaPacket) {
			incomingMedia((MediaPacket) p);

		} else if (p instanceof ErrorPacket) {
			throw new RuntimeException("Opps, we shouldn't have to deal with errors! (" + p + ")");
		}
	}

	@Override
	public void send(Packet p) {
		try {
			super.send(p);
		} catch (RoutingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Issues a request for a stream
	 * @param server
	 * @param c
	 */
	protected void startRequest(final int server, ClientRequest c) {

		// Log any old request
		//if ( requests.containsKey(c.requestID) )
		//	logRequest( c.requestID, requests.get(c.requestID) );

		// Store this new request
		requests.put(c.requestID, c);

		// Echo a request has started
		Trace.println(LogLevel.LOG1, this + ": " + c + " started ");

		send ( RequestPacket.newPacket(this.address, server, c) );

		isPaused = false;
	}

	/**
	 * Requests that a server stops sending a stream
	 * @param server
	 * @param m
	 */
	public void stopRequest(final int server, ClientRequest c) {

		assert c != null;

		Trace.println(LogLevel.LOG1, this + ": " + c + " stopped after " + ( Events.getTime() - c.startTime ) + "ms");

		send ( StopPacket.newPacket(this.address, server, c.requestID) );

		requests.remove(c.requestID);
		oldRequests.add(c.requestID);

		isPaused = false;
	}

	/**
	 * Causes the client to stop all requests
	 * @param server
	 */
	public void stopRequests(final int server) {
		// Make sure all requests are stopped
		while ( !requests.isEmpty() ) {
			// Get the first request
			Iterator<ClientRequest> i = requests.values().iterator();
			stopRequest(server, i.next() );
		}
	}

	/**
	 * Returns the cache of data this client has
	 * @return
	 */
	public Cache getCache() {
		return cache;
	}

	/**
	 * Returns the cache of data this client has prefetched
	 * @return
	 */
	public Cache getPrefetchCache() {
		return prefetchCache;
	}

	/* (non-Javadoc)
	 * @see sim.net.Host#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();

		cache.clear();
		prefetchCache.clear();

		requests.clear();
		oldRequests.clear();
	}
}
