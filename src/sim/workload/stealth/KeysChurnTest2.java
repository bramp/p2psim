package sim.workload.stealth;

import java.util.Iterator;

import sim.events.Events;
import sim.main.Global;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.events.repeatable.FailAndPassEvent;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.events.repeatable.PutAndPassEvent;
import sim.net.overlay.dht.pastry.Peer;
import sim.net.overlay.dht.stealth.StealthPeer;
import sim.net.overlay.dht.stealth.events.UnfailAndJoinAndPassEvent;
import sim.stats.StatsObject;

public class KeysChurnTest2 extends Default {
	public KeysChurnTest2(String[] arglist) throws Exception {
		super(arglist);
		int normal = Integer.parseInt(arglist[0]);
		int stealth = Integer.parseInt(arglist[1]);

		Distribution joinDistribution = new Constant(100000);

		setupPeers(normal, stealth);
		fastJoinAllNodes();

		/* start some of the peers off as failed */
		HostSet stealthPeers = Global.hosts.getType(StealthPeer.class);
		Iterator<Host> peers = stealthPeers.iterator();

		// Fail a half of them
		int startFailed = stealthPeers.size() / 2;
		while(peers.hasNext() && startFailed > 0) {
			Peer p = (Peer)peers.next();
			p.setFailed(true);
			startFailed--;
		}

		// 1000 puts, no replication
		final int puts = 1000;
		final Distribution putDistribution = new Constant(1);
		final int putsEnd = (int) (puts * putDistribution.getMean());
		final int replication = 1;
		Global.stats.logValue("DHT" + StatsObject.SEPARATOR + "Replication", replication);

		Events.addNow(PutAndPassEvent.newEvent(putDistribution, puts, replication));

		// Join them due to this distribution
		Events.addNow(UnfailAndJoinAndPassEvent.newEvent(joinDistribution, stealth));

		// Also fail them at the same time
		Events.addNow(FailAndPassEvent.newEvent(joinDistribution, stealthPeers, stealth));

		// 10000 gets, exponentially distributed interval with 6 minute mean
		final int gets = stealth * 10; // Each node does 10 gets
		Events.add(GetAndPassEvent.newEvent(new Constant((Events.getLastTime() - putsEnd) / gets), gets), putsEnd);
	}
}
