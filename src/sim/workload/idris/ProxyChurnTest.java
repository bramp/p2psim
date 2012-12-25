package sim.workload.idris;

import sim.events.Events;
import sim.main.Global;
import sim.math.Distribution;
import sim.math.Exponential;
import sim.net.HostSet;
import sim.net.links.NormalLink;
import sim.net.overlay.dht.events.repeatable.FailAndPassEvent;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.stealth.ProxyClient;
import sim.net.overlay.dht.stealth.ProxyServer;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.events.UnfailAndJoinAndPassEvent;
import sim.net.router.EdgeRouter;
import sim.workload.stealth.Default;

public class ProxyChurnTest extends Default {
	public ProxyChurnTest(String[] arglist) throws Exception {
		super(arglist);

		int proxyServers = Integer.parseInt(arglist[0]);
		int proxyClients = Integer.parseInt(arglist[1]);

		Distribution joinDistribution = new Exponential(1000000);
		Distribution getDistribution = new Exponential(50000);

		int churnNumber =  (int) ((getDistribution.getMean() * 10 * proxyClients) / ( joinDistribution.getMean() ));

		//int count = proxyServers + proxyClients;

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

		/* start some of the peers off as failed */
		HostSet failablePeers = Global.hosts.getType(ServicePeer.class);

		/*
		Iterator<Host> peers = failablePeers.iterator();
		// Fail a half of them
		int startFailed = failablePeers.size() / 3;
		while(peers.hasNext() && startFailed > 0) {
			Peer p = (Peer)peers.next();
			p.setFailed(true);
			startFailed--;
		}*/


		addPeerData(10, 1);

		// Join them due to this distribution
		Events.addNow(UnfailAndJoinAndPassEvent.newEvent(joinDistribution, churnNumber));

		// Also fail them at the same time
		Events.addNow(FailAndPassEvent.newEvent(joinDistribution, failablePeers, churnNumber));

		Events.addNow(
			GetAndPassEvent.newEvent(Global.hosts.getType(ProxyClient.class), getDistribution, proxyClients * 10)
		);

	}
}
