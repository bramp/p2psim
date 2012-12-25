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
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.events.UnfailAndJoinAndPassEvent;
import sim.stats.StatsObject;

/**
 * The normal nodes fail, with different factors
 * @author Andrew Brampton
 *
 */

public class KeysChurnTest3 extends Default {
	public KeysChurnTest3(String[] arglist) throws Exception {
		super(arglist);
		int normal = Integer.parseInt(arglist[0]);
		int stealth = Integer.parseInt(arglist[1]);
		int churnfactor = Integer.parseInt(arglist[2]);

		Distribution joinDist = new Constant(1000000 / churnfactor);

		setupPeers(normal, stealth);
		fastJoinAllNodes();

		/* start some of the peers off as failed */
		HostSet normalPeers = Global.hosts.getType(ServicePeer.class);
		Iterator<Host> peers = normalPeers.iterator();

		// Fail a half of them
		int startFailed = normalPeers.size() / 2;
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
		Events.addNow(UnfailAndJoinAndPassEvent.newEvent(joinDist, normalPeers, normal));

		// Also fail them at the same time
		Events.addNow(FailAndPassEvent.newEvent(joinDist, normalPeers, normal));

		// 10000 gets, exponentially distributed interval with 6 minute mean
		Distribution getDist = new Constant(10000);
		final int gets = (int)( (joinDist.getMean() * normal) / getDist.getMean() ); // Each node does 10 gets
		Events.add(GetAndPassEvent.newEvent(getDist, gets), putsEnd);
	}
}
