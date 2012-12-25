/**
 *
 */
package sim.net.overlay.cdn.workload;

import java.util.Iterator;

import sim.events.Event;
import sim.events.Events;
import sim.main.Global;
import sim.main.Helper;
import sim.math.Constant;
import sim.math.Distribution;
import sim.math.ForceRange;
import sim.math.LogNormal;
import sim.math.Normal;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.links.NormalLink;
import sim.net.overlay.cdn.Client;
import sim.net.overlay.cdn.Hotspot;
import sim.net.overlay.cdn.Media;
import sim.net.overlay.cdn.RequestEvent;
import sim.net.overlay.cdn.Server;
import sim.net.overlay.cdn.cache.LFUCache;
import sim.net.overlay.cdn.cache.UnlimitedCache;
import sim.net.router.EdgeRouter;
import sim.net.router.InteriorRouter;
import sim.net.router.Router;

/**
 * A simulation that mimics the WorldCup logs we have
 * @author Andrew Brampton
 *
 */
public class WorldCup {

	public final static boolean unknown_hotspot_starts = true;

	public static void setupNodes(int servers, int clients, long serverCacheSize) {
		// acquire list of all edge routers
		HostSet edgeRouters = Global.hosts.getType( EdgeRouter.class);
		HostSet interiorRouters = Global.hosts.getType( InteriorRouter.class);

		// First join the servers
		for (int i=0; i<servers; i++) {
			Router r = (Router)interiorRouters.getRandom();
			Server s = new Server(Global.lastAddress, new LFUCache(serverCacheSize));

			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;

			// connect peer to a random edge router
			new NormalLink(s, r, NormalLink.BANDWIDTH_100M, d);
		}

		// Now add some clients
		for (int i=0; i<clients; i++) {
			Router r = (Router)edgeRouters.getRandom();
			Client c = new Client(Global.lastAddress, new UnlimitedCache(), null);

			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;

			// connect peer to a random edge router
			new NormalLink(c, r, NormalLink.BANDWIDTH_1024k, d);
		}
	}

	public WorldCup(String[] arglist) throws Exception {
		int server = Integer.parseInt(arglist[0]);		// e.g. 10
		int client = Integer.parseInt(arglist[1]);		// e.g. 990
		int mediaCount = Integer.parseInt(arglist[2]);		// e.g. 990

		Distribution mediaLength, mediaPopularity, sessionDist, viewSessionDist;

		// 1 Megabit
		double mediaByterate = 1000000 / 8;

		 // Length of the media in bytes (zero and above)
		mediaLength = new ForceRange( new Normal(9300 * mediaByterate , 1500 * mediaByterate), 1500);

		 // Popularity of media (between 0 and mediaCount)
		mediaPopularity = new ForceRange( new Normal(mediaCount / 2, 0.21 * mediaCount), 0, mediaCount);

		 // How long a user stays for
		sessionDist = new LogNormal (4.8352, 1.7041);

		// How long a user stays before skipping
		viewSessionDist = new ForceRange( new LogNormal (1.4555, 2.2921), 1);

		// Create a bunch of media
		Media.generateMedia(mediaCount, mediaLength, new Constant(mediaByterate),
				// hotspot info - force hotspot to be between 30 and 600 seconds
				new Constant(5), null, new ForceRange(new LogNormal(1.4555, 2.2921), 30, 600));

		// Now setup the nodes and create routing tables
		setupNodes(server, client, (long) (9300 * mediaByterate / 10));
		Router.createRoutingTables();

		// Now give all the Nodes a selection of the servers to ping
		HostSet clients = Global.hosts.getType(Client.class);
		HostSet servers = Global.hosts.getType(Server.class);

		Iterator<Host> i = clients.iterator();
		while ( i.hasNext() ) {
			Client c = (Client) i.next();
			Host s = servers.getRandom();

			// Pick the media this user is going to view
			Media media = Media.getMedia( mediaPopularity.nextInt() );

			// Pick how long he will view it for
			int session = sessionDist.nextInt();

			// Now pick each viewing session and queue events up
			long time = 0;
			while (time < session) {
				int start; // Start time in seconds
				int length; // Length of this request in seconds

				// Decide if we go to a hotspot or not
				if (Global.rand.nextDouble() > 0.6) {
					// Pick hotspot 40% of the time
					Hotspot h = (Hotspot) Helper.pickFromList ( media.getHotspots() );

					start = (int)h.start;
					length = (int)h.length();

					// If we don't know 100% where the hotspot starts, tweedle it a bit
					if (unknown_hotspot_starts) {
						start += (int)(Global.rand.nextGaussian() * 0.5 * (double)length);

						if (start < 0)
							start = 0;
						else if (start > media.getLength())
							continue; // If the start is in a bad position, JUST loop around and try again
					}

					// Modify the length a little by a normally distributed number
					// The length will be +/- a Normal value (with sigma length / 2)
					length += (int)(Global.rand.nextGaussian() * 0.5 * (double)length);

					if (length <= 0)
						length = 5;
				} else {
					// Pick random location
					start = (int) (media.getLength() * Global.rand.nextDouble());
					length = viewSessionDist.nextInt();
				}

				Event e = RequestEvent.newEvent(c, s.getAddress(), media, media.getByteOffset(start) );

				Events.addFromNow(e, time * 1000L);

				time += length;
			}
		}
	}
}
