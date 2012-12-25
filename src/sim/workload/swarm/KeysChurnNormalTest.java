package sim.workload.swarm;

import java.util.Iterator;

import sim.events.Events;
import sim.main.Global;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.PeerData;
import sim.net.overlay.dht.events.repeatable.FailAndPassEvent;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.events.repeatable.UnfailAndJoinAndPassEvent;
import sim.net.overlay.dht.swarm.SwarmPeer;

public class KeysChurnNormalTest extends Default {
	public KeysChurnNormalTest(String[] arglist) throws Exception {
		super(arglist);
		int highcap = Integer.parseInt(arglist[0]);
		int lowcap = Integer.parseInt(arglist[1]);
		int count = highcap + lowcap;

		Distribution lowFailureDist = new Constant(100000);
		Distribution highFailureDist = new Constant(1000000);

		setupPeers(highcap, lowcap, 1.0, 1.0);

		/* start some of the peers off as failed */
		HostSet peerList = Global.hosts.getType(DHTInterface.class);
		HostSet highPeers = new HostSet();
		HostSet lowPeers = new HostSet();

		// Fail a half of them
		int startFailed = count / 2;

		Iterator<Host> peers = peerList.iterator();
		while(peers.hasNext() ) {
			SwarmPeer p = (SwarmPeer)peers.next();

			// Fail half of them
			if (startFailed > 0) {
				p.setFailed(true);
			}

			// Sort them into their capability
			if (p.getCapability() > 0.5)
				lowPeers.add(p);
			else
				highPeers.add(p);

			startFailed--;
		}

		fastJoinAllNodes();

		/*
		// 1000 puts, no replication
		final int puts = count; // Each node does 1 put
		final Distribution putDistribution = new Constant(1);
		final int putsEnd = (int) (puts * putDistribution.getMean());
		final int replication = 1;
		Global.stats.logValue("DHT" + StatsObject.SEPARATOR + "Replication", replication);

		Events.addNow(PutAndPassEvent.newEvent(putDistribution, puts, replication));
		*/

		PeerData.quickHack();

		// Join them due to this distribution
		Events.addNow(UnfailAndJoinAndPassEvent.newEvent(lowFailureDist, lowcap));

		// Also fail them at the same time
		Events.addNow(FailAndPassEvent.newEvent(lowFailureDist, lowPeers, lowcap));

		// 10000 gets, exponentially distributed interval with 6 minute mean
		final int gets = count * 10; // Each node does 10 gets
		Events.addNow(GetAndPassEvent.newEvent(new Constant((Events.getLastTime()) / gets), gets));

		// Now fail the high cap nodes
		Events.addNow(UnfailAndJoinAndPassEvent.newEvent(highFailureDist, highcap));
		Events.addNow(FailAndPassEvent.newEvent(highFailureDist, highPeers, highcap));
	}
}
