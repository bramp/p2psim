package sim.workload.stealth;

import java.util.Set;

import sim.events.Events;
import sim.main.Global;
import sim.math.Constant;
import sim.net.HostSet;
import sim.net.links.NormalLink;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.StealthPeer;
import sim.net.overlay.dht.stealth.events.RandomFailNormalCount;
import sim.net.overlay.dht.stealth.events.RandomFailStealthCount;
import sim.net.overlay.dht.stealth.events.RandomRecvCountPerPeer;
import sim.net.overlay.dht.stealth.events.RandomRecvCountToAll;
import sim.net.router.EdgeRouter;

public class Default extends sim.workload.Default {

	/**
	 * @param arglist
	 * @throws Exception
	 */
	public Default(String[] arglist) throws Exception {
		super(arglist);
	}

	public static void setupStealth(int count, double service_ratio) throws Exception {
		setupPeers(count, service_ratio);
		fastJoinAllNodes();
	}

	public static void setupStealth(int service_count, int stealth_count) throws Exception {
		setupPeers(service_count, stealth_count);
		fastJoinAllNodes();
	}

	public static void sendRandomMessagesPerPeer(int count, boolean direct) {
		Events.addAfterLastEvent(RandomRecvCountPerPeer.newEvent(count, direct));
	}

	/**
	 * Sends messages between nodes
	 * @param count Total number of messages
	 * @param direct Should the message go directly
	 */
	public static void sendRandomMessages(int count, boolean direct) {
		Events.addAfterLastEvent(RandomRecvCountToAll.newEvent(count, direct));
	}

	public static Set<DHTInterface> addPeerData(int count, int replication) {
		return sim.workload.sigcomm.Default.addPeerData( Global.hosts.getType(ServicePeer.class), count, replication );
	}

	/**
	 * Connects the Normal and Stealth peers to the networks
	 * @param count
	 * @param normal_ratio The percentage of Normal peers
	 */
	public static void setupPeers(int count, double normal_ratio) {
		// acquire list of all edge routers
		HostSet edgeRouters = Global.hosts.getType(EdgeRouter.class);

		int normal = (int) (count * normal_ratio);

		if (normal == 0)
			return;

		int stealth = (count - normal) / normal;
		int remain = count - (stealth * normal) - normal;

		// First join normal peers
		for (int i=normal; i>0; i--) {
			EdgeRouter e = (EdgeRouter)edgeRouters.getRandom();

			ServicePeer p = new ServicePeer(Global.lastAddress);

			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;
			// connect peer to a random edge router
			new NormalLink(p, e, NormalLink.BANDWIDTH_1024k, d);

			// Now add some Stealth peers to the same EdgeRouter
			for (int ii=0; ii<stealth; ii++) {
				StealthPeer sp = new StealthPeer(Global.lastAddress);

				// links should have a latency of 1-5ms
				d = Global.rand.nextInt(4) + 1;
				// connect peer to a random edge router
				new NormalLink(sp, e, NormalLink.BANDWIDTH_1024k, d);
			}

		}

		// Now add the remaining StealthPeer
		for (int ii=0; ii<remain; ii++) {
			EdgeRouter e = (EdgeRouter)edgeRouters.getRandom();

			StealthPeer sp = new StealthPeer(Global.lastAddress);

			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;
			// connect peer to a random edge router
			new NormalLink(sp, e, NormalLink.BANDWIDTH_1024k, d);
		}
	}

	public static void setupPeers(int service, int stealth) {
		// acquire list of all edge routers
		HostSet edgeRouters = Global.hosts.getType(EdgeRouter.class);

		// First join normal peers
		for (int i=0; i<service; i++) {
			EdgeRouter e = (EdgeRouter)edgeRouters.getRandom();
			ServicePeer p = new ServicePeer(Global.lastAddress);

			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;

			// connect peer to a random edge router
			new NormalLink(p, e, NormalLink.BANDWIDTH_1024k, d);
		}


		// Now add some Stealth peers
		for (int ii=0; ii<stealth; ii++) {
			EdgeRouter e = (EdgeRouter)edgeRouters.getRandom();
			StealthPeer sp = new StealthPeer(Global.lastAddress);

			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;
			// connect peer to a random edge router
			new NormalLink(sp, e, NormalLink.BANDWIDTH_1024k, d);
		}

	}

	public static void slowFailStealthFromStart(int fail, long lasttime) throws Exception {
		long interval = 0;
		if (fail > 0) {interval = lasttime / fail;}
		// failures start at time 0
		Events.add(RandomFailStealthCount.newEvent(new Constant(interval),fail),0);
	}

	public static void slowFailNormalFromStart(int fail, long lasttime) throws Exception {
		long interval = 0;
		if (fail > 0) {interval = lasttime / fail;}
		// failures start at time 0
		Events.add(RandomFailNormalCount.newEvent(new Constant(interval),fail),0);
	}
}
