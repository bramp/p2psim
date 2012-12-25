package sim.workload.idris;


import sim.events.Events;
import sim.main.Global;
import sim.math.Distribution;
import sim.math.Exponential;
import sim.net.HostSet;
import sim.net.links.NormalLink;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.StealthPeer;
import sim.net.router.EdgeRouter;
import sim.workload.stealth.Default;

public class KeysTest extends Default {
	public KeysTest(String[] arglist) throws Exception {
		super(arglist);
		int service = Integer.parseInt(arglist[0]);
		int stealth = Integer.parseInt(arglist[1]);
		Distribution getDistribution = new Exponential(120000);

		//int count = service + stealth;

		// acquire list of all edge routers
		HostSet edgeRouters = Global.hosts.getType(EdgeRouter.class);

		// First join proxy Servers
		for (int i=0; i<service; i++) {
			EdgeRouter e = (EdgeRouter)edgeRouters.getRandom();
			ServicePeer p = new ServicePeer(Global.lastAddress);

			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;

			// connect peer to a random edge router
			new NormalLink(p, e, NormalLink.BANDWIDTH_1024k, d);
		}

		// Now add some proxy Clients
		for (int ii=0; ii<stealth; ii++) {
			EdgeRouter e = (EdgeRouter)edgeRouters.getRandom();
			StealthPeer pc = new StealthPeer(Global.lastAddress);

			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;

			// connect peer to a random edge router
			new NormalLink(pc, e, NormalLink.BANDWIDTH_1024k, d);
		}

		fastJoinAllNodes();

		addPeerData(1000, 1);

		// 10000 gets, exponentially distributed interval with 6 minute mean
		Events.addNow(GetAndPassEvent.newEvent(Global.hosts.getType(StealthPeer.class), getDistribution, 10000));
	}
}
