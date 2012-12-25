package sim.workload.idris;


import sim.events.Events;
import sim.main.Global;
import sim.math.Distribution;
import sim.math.Exponential;
import sim.net.HostSet;
import sim.net.links.NormalLink;
import sim.net.overlay.dht.events.repeatable.FailAndPassEvent;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.StealthPeer;
import sim.net.overlay.dht.stealth.events.UnfailAndJoinAndPassEvent;
import sim.net.router.EdgeRouter;
import sim.workload.stealth.Default;

public class KeysChurnTest extends Default {
	public KeysChurnTest(String[] arglist) throws Exception {
		super(arglist);
		int service = Integer.parseInt(arglist[0]);
		int stealth = Integer.parseInt(arglist[1]);
		Distribution joinDistribution = new Exponential(1000000);
		Distribution getDistribution = new Exponential(50000);

		//10 * 2 * 100 = 2000
		//5 * 10 * 100 = 5000
		//int count = service + stealth;

		int churnNumber = (int) ((getDistribution.getMean() * 10 * stealth) / ( joinDistribution.getMean() ));

		//System.out.println(churnNumber);

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

		/* start some of the peers off as failed */
		HostSet failablePeers = Global.hosts.getType(ServicePeer.class);

		// Fail a half of them
		/*
		Iterator<Host> peers = failablePeers.iterator();
		int startFailed = failablePeers.size() / 3;
		while(peers.hasNext() && startFailed > 0) {
			Peer p = (Peer)peers.next();
			p.setFailed(true);
			startFailed--;
		}
		*/

		addPeerData(10, 1);

		// Join them due to this distribution
		Events.addNow(UnfailAndJoinAndPassEvent.newEvent(joinDistribution, churnNumber));

		// Also fail them at the same time
		Events.addNow(FailAndPassEvent.newEvent(joinDistribution, failablePeers, churnNumber));

		// 10000 gets, exponentially distributed interval with 6 minute mean
		Events.addNow(GetAndPassEvent.newEvent(Global.hosts.getType(StealthPeer.class), getDistribution, stealth * 10));
	}
}
