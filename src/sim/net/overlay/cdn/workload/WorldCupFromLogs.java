/**
 *
 */
package sim.net.overlay.cdn.workload;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import sim.collections.ArrayRangeCounts;
import sim.events.Events;
import sim.main.Global;
import sim.math.Distribution;
import sim.math.Weibull;
import sim.net.HostSet;
import sim.net.links.NormalLink;
import static sim.net.links.NormalLink.BANDWIDTH_100M;
import static sim.net.links.NormalLink.BANDWIDTH_1024k;
import static sim.net.links.NormalLink.BANDWIDTH_2048k;
import static sim.stats.StatsObject.SEPARATOR;
import sim.net.overlay.cdn.Client;
import sim.net.overlay.cdn.Common;
import sim.net.overlay.cdn.Hotspot;
import sim.net.overlay.cdn.Media;
import sim.net.overlay.cdn.Server;
import sim.net.overlay.cdn.cache.Cache;
import sim.net.overlay.cdn.cache.LFUCache;
import sim.net.overlay.cdn.cache.UnlimitedCache;
import sim.net.overlay.cdn.prefetch.PrefetchScheme;
import sim.net.overlay.cdn.prefetch.Prefetcher;
import sim.net.router.EdgeRouter;
import sim.net.router.InteriorRouter;
import sim.net.router.Router;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;
import sim.workload.Workload;

/**
 * A simulation that uses the WorldCup logs we have
 * @author Andrew Brampton
 *
 */
public class WorldCupFromLogs implements Workload {

	public void setupNodes(int servers, int clients) {
		setupNodes(servers, clients, -1, -1);
	}

	public void setupNodes(int servers, int clients, long serverCacheSize, long clientCacheSize) {
		// acquire list of all edge routers
		HostSet edgeRouters = Global.hosts.getType( EdgeRouter.class);
		HostSet interiorRouters = Global.hosts.getType( InteriorRouter.class);

		if (interiorRouters.isEmpty()) {
			if (edgeRouters.isEmpty()) {
				throw new RuntimeException("No available routers to connect to");
			}

			// Hack to make the edge routers also interior routers
			interiorRouters = edgeRouters;
		}

		// First join the servers
		for (int i=0; i<servers; i++) {
			Cache cache;

			if (serverCacheSize == -1)
				cache = new UnlimitedCache();
			else
				cache = new LFUCache(serverCacheSize);

			Server s = new Server(Global.lastAddress, cache);

			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;

			// connect peer to a random edge router
			Router r = (Router)interiorRouters.getRandom();
			new NormalLink(s, r, BANDWIDTH_100M, d);
		}

		// Now add some clients
		for (int i=0; i<clients; i++) {

			Cache cache;
			if (clientCacheSize == -1)
				cache = new UnlimitedCache();
			else
				cache = new LFUCache(clientCacheSize);


			Client c = new Client(Global.lastAddress, cache, prefetchscheme.constructPrefetcher());

			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;

			// connect peer to a random edge router
			Router r = (Router)edgeRouters.getRandom();
			new NormalLink(c, r, BANDWIDTH_2048k, d);
		}
	}

	protected double findValue(String[] parts, String name) {
		for (int i = 0; i < parts.length; i++) {
			if ( parts[i].trim().startsWith(name) )
				return Double.parseDouble( parts[i+1].trim() );
		}

		return Double.NaN;
	}

	protected Distribution loadDistributionFile(String filename) throws IOException {
		try {
			BufferedReader in = new BufferedReader ( new FileReader(filename) );

			String line;
			while ((line = in.readLine()) != null) {
				//Log-Normal	R_SQUARE =	0.971268	OLS =	0.082484	MU =	3.511458	SIGMA =	1.280886
				String parts[] = line.split("\t");

				// The first word is the model name
				if ( parts[0].equals( "Log-Normal") ) {

				} else if ( parts[0].equals( "Weibull") ) {
					double lamda = findValue( parts, "l =");
					double k = findValue( parts, "k =");

					return new Weibull(lamda, k);
				}

			}

		} catch (FileNotFoundException e) {
			return null;
		}

		return null;
	}

	public static WorkloadReader r;
	PrefetchScheme prefetchscheme;

	public WorldCupFromLogs(String[] arglist) throws Exception {
		int server = Integer.parseInt(arglist[0]);		// e.g. 10
		String actions = arglist[1];
		String metadir = arglist[2];
		int prefetch = Integer.parseInt( arglist[3] );

		prefetchscheme = PrefetchScheme.values()[ prefetch ];
		Prefetcher.prefetchRate = Double.parseDouble( arglist[4] );
		Prefetcher.prefetchSizeInSec = Integer.parseInt( arglist[5] );

		if ( arglist.length > 6 )
			Prefetcher.prefetchSizePercent = Double.parseDouble( arglist[6] );

		r = new WorkloadReader( actions, metadir );
		Trace.println(LogLevel.INFO, "Loaded " + actions );

		int client = r.userCount();

		// 1 Megabit
		double mediaByterate = BANDWIDTH_1024k;

		// Length of the media in bytes (zero and above)
		//Distribution mediaLength = new ForceRange( new Normal(9300 * mediaByterate , 1500 * mediaByterate), 1500);

		// So create some media
		//Media.generateMedia(mediaCount, mediaLength, new Constant(mediaByterate));

		// Do this once for each object (in the workload)
		Iterator<Integer> i = r.objectLength.iterator();
		int object = 0;
		while (i.hasNext()) {

			// Create a set of hotspots
			List<Hotspot> hotspots = new ArrayList<Hotspot>();

			Iterator<Entry<String, Integer>> i2 = r.objectBookmarks.get(object).entrySet().iterator();
			while( i2.hasNext() ) {
				// This contains the bookmark name & start position
				final Entry<String, Integer> e = i2.next();
				final String bookmark = e.getKey();
				int start = e.getValue();

				// Move the start based on the offset
				start = Math.max(start + Prefetcher.prefetchBookmarkOffset , 0);

				Hotspot h = null;

				// The length of this bookmark
				long length = Prefetcher.prefetchSizeInSec;

				// The distribution that modelled this length
				Distribution d = null;

				if ( Prefetcher.prefetchSizePercent > 0.0 ) {
					// Try and load the hotspot model metadata
					String metafile = metadir + "/";
					metafile += actions.substring(0, actions.indexOf(".actions")) + "_" + bookmark;
					metafile += "_models";

					d = loadDistributionFile ( metafile );

					if ( d != null ) {
						length = (long) d.getCDF( Prefetcher.prefetchSizePercent );
						length = Math.max(length, 1);

						Trace.println(LogLevel.LOG1, "Loaded bookmark model " + bookmark + " " + d);
					} else {
						final String msg = "WARNING Unable to load bookmark model " + bookmark;
						Trace.println(LogLevel.WARN, msg + " " + metafile);

						// Print to screen
						System.err.println(msg);
					}
				}

				h = new Hotspot (null, bookmark, start, length);
				h.length = d;

				// Record the total hotspot lengths
				Global.stats.logRunningTotal("CDN" + SEPARATOR + "Hotspot" + SEPARATOR + "Lengths", h.length());

				// If available work out which bookmark is visited next
				Map<String, Integer> sequences = r.objectBookmarkSequence.get(object);
				if ( sequences != null && sequences.size() > 0 ) {
					h.sequence = new TreeMap<String, Integer>();

					Iterator<Entry<String, Integer>> sequence = sequences.entrySet().iterator();
					while (sequence.hasNext()) {
						Entry<String, Integer> s = sequence.next();

						if ( s.getKey().startsWith( h.name + "\t" )  ) {
							h.sequence.put( s.getKey().substring( h.name.length() + 1 ) , s.getValue() );
						}
					}
				}

				h.hits = r.objectBookmarkPopularity.get(object).get( bookmark );

				// Also add the hotspots length

				// Now we are done, so add this to our list of hotspots
				hotspots.add ( h );
			}

			new Media ( i.next(), (int)mediaByterate, hotspots);
			object++;
		}

		// Now setup the nodes and create routing tables
		setupNodes(server, client);
		Router.createRoutingTables();

		Events.addNow( WorkloadReaderEvent.newEvent(r) );
	}

	@Override
	public void simulationFinished() {
		// Work out some more stats
		double hits, miss, total;

		// Client Cache Ratios
		hits = Global.stats.getValue("CDN" + SEPARATOR + "ClientCache" + SEPARATOR + "ByteHit");
		miss = Global.stats.getValue("CDN" + SEPARATOR + "ClientCache" + SEPARATOR + "ByteMiss");
		Global.stats.logValue("CDN" + SEPARATOR + "ClientCache" + SEPARATOR + "ByteRatio", hits / (hits + miss));

		hits = Global.stats.getValue("CDN" + SEPARATOR + "ClientCache" + SEPARATOR + "Hit" + SEPARATOR + "Count");
		miss = Global.stats.getValue("CDN" + SEPARATOR + "ClientCache" + SEPARATOR + "Miss" + SEPARATOR + "Count");
		Global.stats.logValue("CDN" + SEPARATOR + "ClientCache" + SEPARATOR + "Ratio", hits / (hits + miss));

		// Server Cache Ratios
		hits = Global.stats.getValue("CDN" + SEPARATOR + "ServerCache" + SEPARATOR + "ByteHit");
		miss = Global.stats.getValue("CDN" + SEPARATOR + "ServerCache" + SEPARATOR + "ByteMiss");
		Global.stats.logValue("CDN" + SEPARATOR + "ServerCache" + SEPARATOR + "ByteRatio", hits / (hits + miss));

		hits = Global.stats.getValue("CDN" + SEPARATOR + "ServerCache" + SEPARATOR + "Hit" + SEPARATOR + "Count");
		miss = Global.stats.getValue("CDN" + SEPARATOR + "ServerCache" + SEPARATOR + "Miss" + SEPARATOR + "Count");
		Global.stats.logValue("CDN" + SEPARATOR + "ServerCache" + SEPARATOR + "Ratio", hits / (hits + miss));

		// Prefetch Cache Ratios (how often something was in the prefetch cache we wanted
		hits = Global.stats.getValue("CDN" + SEPARATOR + "PrefetchCache" + SEPARATOR + "ByteHit");
		miss = Global.stats.getValue("CDN" + SEPARATOR + "PrefetchCache" + SEPARATOR + "ByteMiss");
		Global.stats.logValue("CDN" + SEPARATOR + "PrefetchCache" + SEPARATOR + "ByteRatio", hits / (hits + miss));

		hits = Global.stats.getValue("CDN" + SEPARATOR + "PrefetchCache" + SEPARATOR + "Hit" + SEPARATOR + "Count");
		miss = Global.stats.getValue("CDN" + SEPARATOR + "PrefetchCache" + SEPARATOR + "Miss" + SEPARATOR + "Count");
		Global.stats.logValue("CDN" + SEPARATOR + "PrefetchCache" + SEPARATOR + "Ratio", hits / (hits + miss));

		// Prefetch Cache Usage (how much of the prefetched data was used)
		hits = Global.stats.getValue("CDN" + SEPARATOR + "PrefetchCache" + SEPARATOR + "ByteHit");
		total = Global.stats.getValue("CDN" + SEPARATOR + "Prefetch" + SEPARATOR + "Total");
		Global.stats.logValue("CDN" + SEPARATOR + "PrefetchUsage" + SEPARATOR + "Ratio", hits / total);

		// Instant Seeks
		hits = Global.stats.getValue("CDN" + SEPARATOR + "InstantSeek" + SEPARATOR + "Hit" + SEPARATOR + "Count");
		miss = Global.stats.getValue("CDN" + SEPARATOR + "InstantSeek" + SEPARATOR + "Miss" + SEPARATOR + "Count");
		Global.stats.logValue("CDN" + SEPARATOR + "InstantSeek" + SEPARATOR + "Ratio", hits / (hits + miss));

		if ( Common.logMissHit ) {
			Trace.println(LogLevel.LOG1, "Hits " + Common.hits);
			Trace.println(LogLevel.LOG1, "Miss " + Common.miss);
		}
	}

	@Override
	public void simulationStart() {
		if ( Common.logMissHit ) {
			Common.hits = new ArrayRangeCounts(10000);
			Common.miss = new ArrayRangeCounts(10000);
		}
		
		Global.stats.logValue("Sim" + SEPARATOR + "PrefetchScheme", prefetchscheme.ordinal() );
		Global.stats.logValue("Sim" + SEPARATOR + "PrefetchSizeInSec", Prefetcher.prefetchSizeInSec );
		Global.stats.logValue("Sim" + SEPARATOR + "PrefetchSizePercent", Prefetcher.prefetchSizePercent );
		Global.stats.logValue("Sim" + SEPARATOR + "PrefetchRate", Prefetcher.prefetchRate );
		Global.stats.logValue("Sim" + SEPARATOR + "PrefetchBookmarkOffset", Prefetcher.prefetchBookmarkOffset );
	}
}

