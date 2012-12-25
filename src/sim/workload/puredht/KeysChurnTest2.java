package sim.workload.puredht;

import java.util.Iterator;

import sim.events.Events;
import sim.main.Global;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.events.repeatable.FailAndPassEvent;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.events.repeatable.PutAndPassEvent;
import sim.net.overlay.dht.events.repeatable.UnfailAndJoinAndPassEvent;
import sim.net.overlay.dht.pastry.Peer;
import sim.stats.StatsObject;
import sim.workload.Default;

public class KeysChurnTest2 extends Default {
	public KeysChurnTest2(String[] arglist) throws Exception {
		super(arglist);
		int normal = Integer.parseInt(arglist[0]);
		int stealth = Integer.parseInt(arglist[1]);
		int count = normal + stealth;

		Distribution joinDistribution = new Constant(100000);

		setupPeers(count);
		fastJoinAllNodes();

		/* start some of the peers off as failed */
		HostSet peerList = Global.hosts.getType(DHTInterface.class);
		HostSet failablePeers = new HostSet();

		// Fail a half of them
		int startFailed = (count) / 2;
		int neverFailed = stealth;

		Iterator<Host> peers = peerList.iterator();
		while(peers.hasNext() ) {
			Peer p = (Peer)peers.next();
			if (startFailed > 0) {
				p.setFailed(true);
				failablePeers.add(p);
			} else if (startFailed > -neverFailed) {
				failablePeers.add(p);
			}

			startFailed--;
		}

		// 1000 puts, no replication
		final int puts = 1000; // Each node does 1 put
		final Distribution putDistribution = new Constant(1);
		final int putsEnd = (int) (puts * putDistribution.getMean());
		final int replication = 1;
		Global.stats.logValue("DHT" + StatsObject.SEPARATOR + "Replication", replication);

		Events.addNow(PutAndPassEvent.newEvent(putDistribution, puts, replication));

		// Join them due to this distribution
		Events.addNow(UnfailAndJoinAndPassEvent.newEvent(joinDistribution, count));

		// Also fail them at the same time
		Events.addNow(FailAndPassEvent.newEvent(joinDistribution, failablePeers, count));

		// 10000 gets, exponentially distributed interval with 6 minute mean
		final int gets = count * 10; // Each node does 10 gets
		Events.add(GetAndPassEvent.newEvent(new Constant((Events.getLastTime() - putsEnd) / gets), gets), putsEnd);
	}
}
