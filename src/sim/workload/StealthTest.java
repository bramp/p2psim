package sim.workload;

import java.util.Iterator;

import sim.events.Events;
import sim.main.Global;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.InvalidHostException;
import sim.net.links.NormalLink;
import sim.net.overlay.dht.events.JoinEvent;
import sim.net.overlay.dht.pastry.Peer;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.StealthPeer;
import sim.net.overlay.dht.stealth.events.RandomRecvCountPerPeer;
import sim.net.router.EdgeRouter;
import sim.stats.StatsObject;

/**
 * @author Andrew Brampton
 *
 */
public class StealthTest extends Default {
	private static int PEER_COUNT;
	private final static int MAX_PEER_LATENCY = 4;
	private final static int PEER_BANDWIDTH = NormalLink.BANDWIDTH_1024k;

	public static void sendRandomMessagesPerPeer(int count, boolean direct) {
		Events.addAfterLastEvent(RandomRecvCountPerPeer.newEvent(count, direct));
	}

	public static void joinAllNodes() throws InvalidHostException {
		boolean firstPeer = true;

		joinAllRouters();

		HostSet joinedPeers = new HostSet();
		HostSet peers = Global.hosts.getType(ServicePeer.class);
		Iterator<Host> i = peers.iterator();
		// Join all NormalPeers
		while (i.hasNext()) {
			Peer p = (Peer)i.next();
			int joinAddress;

			if (firstPeer) {
				// Do a join to the special INVALID_ADDRESS
				// Basically don't join anyone ;)
				firstPeer = false;
				joinAddress = Host.INVALID_ADDRESS;
			} else {
				//Random joins
				joinAddress = joinedPeers.getRandom().getAddress();
			}

			Events.addAfterLastEvent(JoinEvent.newEvent(p, joinAddress));
			joinedPeers.add(p);
		}

		peers = Global.hosts.getType(StealthPeer.class);
		i = peers.iterator();
		// Now join the StealthPeers to the NormalPeers
		while (i.hasNext()) {
			Peer p = (Peer)i.next();
			Events.addAfterLastEvent(JoinEvent.newEvent(p, joinedPeers.getRandom().getAddress()));
		}

	}

	public StealthTest(String[] arglist) throws Exception {
		super(arglist);
		PEER_COUNT = Integer.parseInt(arglist[0]);

		// acquire list of all edge routers
		HostSet edgeRouters = Global.hosts.getType(EdgeRouter.class);

		for (int i=0; i<PEER_COUNT / 2; i++) {
			Peer p = new ServicePeer(Global.lastAddress);
			EdgeRouter e = (EdgeRouter)edgeRouters.getRandom();
			// links should have a latency of at least 1
			int d = Global.rand.nextInt(MAX_PEER_LATENCY) + 1;
			Global.stats.logCount("Node" + StatsObject.SEPARATOR + "Peer");
			Global.stats.logCount("Node");

			// connect peer to a random edge router
			new NormalLink(p,e,PEER_BANDWIDTH,d);
		}

		for (int i=0; i < PEER_COUNT / 2; i++) {
			Peer p = new StealthPeer(Global.lastAddress);
			EdgeRouter e = (EdgeRouter)edgeRouters.getRandom();
			// links should have a latency of at least 1
			int d = Global.rand.nextInt(MAX_PEER_LATENCY) + 1;
			Global.stats.logCount("Node"+StatsObject.SEPARATOR+"Peer");
			Global.stats.logCount("Node");

			// connect peer to a random edge router
			new NormalLink(p,e,PEER_BANDWIDTH,d);
		}

		//fastJoinAllNodes();
		joinAllNodes();

		//Events.addAfterLastEvent(new RandomFailCount(new Constant(1), PEER_COUNT / 2));

		// This invokes the Stealth sendRandomMessagesPerPeer
		sendRandomMessagesPerPeer(10, false);
	}
}
