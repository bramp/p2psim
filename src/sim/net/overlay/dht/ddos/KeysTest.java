package sim.net.overlay.dht.ddos;

import sim.main.Global;
import sim.math.Exponential;
import sim.net.HostSet;
import sim.net.links.NormalLink;
import sim.net.overlay.dht.PeerData;
import sim.net.overlay.dht.pastry.Peer;
import sim.net.router.EdgeRouter;
import sim.workload.Default;

public class KeysTest extends Default {
	public KeysTest(String[] arglist) throws Exception {
		super(arglist);

		// Command line arg for how many peers
		int count = Integer.parseInt(arglist[0]);

		HostSet edgeRouters = Global.hosts.getType(EdgeRouter.class);

		// Loops creating count number of Peers
		for (int i=0;i<count;i++) {
			// Create a peer
			Peer p = new Peer(Global.lastAddress);

			// Picks a random router to connect this peer to
			EdgeRouter e = (EdgeRouter)edgeRouters.getRandom();

			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;

			// connect peer to a random edge router
			new NormalLink(p,e,NormalLink.BANDWIDTH_1024k,d);
		}

		// Ensures all the routers and peers are joined together
		fastJoinAllNodes();

		// Places 1 million keys in the network
		PeerData.quickHack();

		// 10000 gets, exponentially distributed interval with 6 minute mean
		generateRandomGets(new Exponential(360000), count * 10);
	}
}
