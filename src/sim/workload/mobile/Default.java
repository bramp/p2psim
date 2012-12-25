package sim.workload.mobile;

import java.util.List;

import sim.main.Global;
import sim.net.HostSet;
import sim.net.links.NormalLink;
import sim.net.links.SharedLink;
import sim.net.overlay.dht.pastry.Peer;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.StealthPeer;
import sim.net.router.EdgeRouter;
import sim.net.router.MobileRouter;
import static sim.stats.StatsObject.SEPARATOR;

/*
 * Created on Feb 8, 2005
 */

/**
 * @author Andrew Brampton
 */
public abstract class Default extends sim.workload.Default {

	/**
	 * @param arglist
	 * @throws Exception
	 */
	public Default(String[] arglist) throws Exception {
		super(arglist);
	}

	public static List<Peer> setupWiredPeers(int count)  {
		return setupPeers(count);
	}

	public static HostSet setupWifiPeers(int count) {
		// acquire list of all edge routers
		HostSet edgeRouters = Global.hosts.getType(MobileRouter.class);
		HostSet peers = new HostSet();

		if (edgeRouters.isEmpty())
			throw new RuntimeException("There are no mobile routers!");

		for (int i=0;i<count;i++) {
			Peer p = new Peer(Global.lastAddress);
			MobileRouter e = (MobileRouter)edgeRouters.getRandom();

			new SharedLink(e, p);
			//new NormalLink(e, p, Link.BANDWIDTH_14_4k, 200);

			peers.add(p);

			Global.stats.logCount("Host" + SEPARATOR + "Wifi");
		}

		return peers;
	}

	public static HostSet setupWifiStealth(int count) {
		// acquire list of all edge routers
		HostSet edgeRouters = Global.hosts.getType(MobileRouter.class);
		HostSet peers = new HostSet();

		if (edgeRouters.isEmpty())
			throw new RuntimeException("There are no mobile routers!");

		for (int i=0;i<count;i++) {
			Peer p = new StealthPeer(Global.lastAddress);
			MobileRouter e = (MobileRouter)edgeRouters.getRandom();

			new SharedLink(e, p);

			peers.add(p);

			Global.stats.logCount("Host" + SEPARATOR + "Wifi");
		}

		return peers;
	}

	public static HostSet setupWiredStealth(int stealth_count) {
		// acquire list of all edge routers
		HostSet edgeRouters = Global.hosts.getType(EdgeRouter.class);
		HostSet peers = new HostSet();

		// First join normal peers
		for (int i=0; i<stealth_count; i++) {
			EdgeRouter e = (EdgeRouter)edgeRouters.getRandom();
			StealthPeer p = new StealthPeer(Global.lastAddress);

			peers.add(p);

			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;

			// connect peer to a random edge router
			new NormalLink(p, e, NormalLink.BANDWIDTH_1024k, d);

			Global.stats.logCount("Host" + SEPARATOR + "Wired");
		}

		return peers;
	}


	public static HostSet setupWifiService(int service_count) {
		// acquire list of all edge routers
		HostSet edgeRouters = Global.hosts.getType(MobileRouter.class);
		HostSet peers = new HostSet();

		if (edgeRouters.isEmpty())
			throw new RuntimeException("There are no mobile routers!");

		for (int i=0;i<service_count;i++) {
			Peer p = new ServicePeer(Global.lastAddress);
			MobileRouter e = (MobileRouter)edgeRouters.getRandom();

			new SharedLink(e, p);

			peers.add(p);

			Global.stats.logCount("Host" + SEPARATOR + "Wifi");
		}

		return peers;
	}

	public static HostSet setupWiredService(int service_count) {
		// acquire list of all edge routers
		HostSet edgeRouters = Global.hosts.getType(EdgeRouter.class);
		HostSet peers = new HostSet();

		// First join normal peers
		for (int i=0; i<service_count; i++) {
			EdgeRouter e = (EdgeRouter)edgeRouters.getRandom();
			ServicePeer p = new ServicePeer(Global.lastAddress);

			peers.add(p);

			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;

			// connect peer to a random edge router
			new NormalLink(p, e, NormalLink.BANDWIDTH_1024k, d);

			Global.stats.logCount("Host" + SEPARATOR + "Wired");
		}

		return peers;
	}

}
