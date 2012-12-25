package sim.workload.stealth;

import java.util.Iterator;

import sim.events.Events;
import sim.main.Global;
import sim.math.Constant;
import sim.math.Distribution;
import sim.math.Exponential;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.events.repeatable.FailAndPassEvent;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.pastry.Peer;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.StealthPeer;
import sim.net.overlay.dht.stealth.events.UnfailAndJoinAndPassEvent;

public class KeysChurnTest extends Default {
	public KeysChurnTest(String[] arglist) throws Exception {
		super(arglist);
		int service = Integer.parseInt(arglist[0]);
		int stealth = Integer.parseInt(arglist[1]);
		Distribution joinDistribution = new Exponential(120000);

		int count = service + stealth;

		setupPeers(service, stealth);
		fastJoinAllNodes();

		/* start some of the peers off as failed */
		HostSet failablePeers = Global.hosts.getType(ServicePeer.class);
		Iterator<Host> peers = failablePeers.iterator();

		// Fail a half of them
		int startFailed = failablePeers.size() / 2;
		while(peers.hasNext() && startFailed > 0) {
			Peer p = (Peer)peers.next();
			p.setFailed(true);
			startFailed--;
		}

		addPeerData(10, 1);

		// Join them due to this distribution
		Events.addNow(UnfailAndJoinAndPassEvent.newEvent(joinDistribution, count));

		// Also fail them at the same time
		Events.addNow(FailAndPassEvent.newEvent(joinDistribution, failablePeers, count));

		// 10000 gets, exponentially distributed interval with 6 minute mean
		final int gets = count * 10; // Each node does 10 gets
		Events.addNow(GetAndPassEvent.newEvent(Global.hosts.getType(StealthPeer.class), new Constant((Events.getLastTime()) / gets), gets));
	}
}
