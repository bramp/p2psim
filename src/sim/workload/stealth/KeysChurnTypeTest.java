package sim.workload.stealth;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import sim.events.Event;
import sim.events.Events;
import sim.events.GroupAndPassEvent;
import sim.main.Global;
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

public class KeysChurnTypeTest extends Default {
	public KeysChurnTypeTest(String[] arglist) throws Exception {
		super(arglist);

		int count = Integer.parseInt(arglist[1]);
		double ratio = Double.parseDouble(arglist[2]);

		long churnTime = Long.parseLong(arglist[3]);

		Distribution joinDistribution = new Exponential(churnTime);

		setupPeers(count, ratio);

		fastJoinAllNodes();

		// 1,000,000 key put
		addPeerData(10000, 3);

		/* start some of the peers off as failed */
		HostSet failablePeers1; // = Global.hosts.getType(failableType)
		HostSet failablePeers2; // = Global.hosts.getType(failableType)

		if (arglist[0].equals("Stealth")) {
			failablePeers1 = Global.hosts.getType(StealthPeer.class);
			failablePeers2 = null;
		} else if (arglist[0].equals("Service")) {
			failablePeers1 = Global.hosts.getType(ServicePeer.class);
			failablePeers2 = null;
		} else {
			failablePeers1 = Global.hosts.getType(ServicePeer.class);
			failablePeers2 = Global.hosts.getType(StealthPeer.class);
		}

		HostSet[] failable = {failablePeers1, failablePeers2};

		// Fail a half of them
		for (int i = 0; i < failable.length; i++) {
			HostSet failablePeers = failable[i];
			if (failablePeers != null) {
				int startFailed = failablePeers.size() / 2;
				Iterator<Host> ii = failablePeers.iterator();
				while(ii.hasNext() && startFailed > 0) {
					Peer p = (Peer)ii.next();
					p.setFailed(true);
					startFailed--;
				}
			}
		}


		int numberOfChurns = (int) (6000000 / churnTime);

		List<Event> events = new ArrayList<Event>() ;

		// Join them due to this distribution
		events.add(UnfailAndJoinAndPassEvent.newEvent(null, failablePeers1, 0));

		// Also fail them at the same time
		events.add(FailAndPassEvent.newEvent(null, failablePeers1, 0));

		if (failablePeers2 != null) {
			events.add(UnfailAndJoinAndPassEvent.newEvent(null, failablePeers2, 0));
			events.add(FailAndPassEvent.newEvent(null, failablePeers2, 0));
		}

		Events.addNow(GroupAndPassEvent.newEvent(joinDistribution, numberOfChurns, events));

		final int gets = 100; // Each node does 10 gets
		Events.addNow(GetAndPassEvent.newEvent(new Exponential(60000 / gets), gets * count));
	}
}
