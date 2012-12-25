package sim.workload.idris;

import sim.events.Events;
import sim.main.Global;
import sim.math.Distribution;
import sim.math.Exponential;
import sim.net.HostSet;
import sim.net.links.NormalLink;
import sim.net.overlay.dht.PeerData;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.stealth.ProxyClient;
import sim.net.overlay.dht.stealth.ProxyServer;
import sim.net.router.EdgeRouter;
import sim.workload.stealth.Default;

public class ProxyTest extends Default {
	public ProxyTest(String[] arglist) throws Exception {
		super(arglist);

		int proxyServers = Integer.parseInt(arglist[0]);
		int proxyClients = Integer.parseInt(arglist[1]);

		Distribution getDistribution = new Exponential(120000);

		// acquire list of all edge routers
		HostSet edgeRouters = Global.hosts.getType(EdgeRouter.class);

		// First join proxy Servers
		for (int i=0; i<proxyServers; i++) {
			EdgeRouter e = (EdgeRouter)edgeRouters.getRandom();
			ProxyServer p = new ProxyServer(Global.lastAddress);

			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;

			// connect peer to a random edge router
			new NormalLink(p, e, NormalLink.BANDWIDTH_1024k, d);
		}

		// Now add some proxy Clients
		for (int ii=0; ii<proxyClients; ii++) {
			EdgeRouter e = (EdgeRouter)edgeRouters.getRandom();
			ProxyClient pc = new ProxyClient(Global.lastAddress);

			// links should have a latency of 1-5ms
			int d = Global.rand.nextInt(4) + 1;

			// connect peer to a random edge router
			new NormalLink(pc, e, NormalLink.BANDWIDTH_1024k, d);
		}


		fastJoinAllNodes();

		PeerData.quickHack();

		//System.out.println("numbver of client:" + Global.hosts.getType(ProxyClient.class).size());

		// proxyClients * 10 gets, exponentially distributed interval with 6 minute mean
		GetAndPassEvent e = GetAndPassEvent.newEvent(
				Global.hosts.getType(ProxyClient.class), getDistribution, proxyClients * 10
		);

		Events.addAfterLastEvent( e );

	}
}
