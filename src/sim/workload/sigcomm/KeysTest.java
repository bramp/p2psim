package sim.workload.sigcomm;

import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import sim.events.Event;
import sim.events.Events;
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
import sim.net.overlay.dht.stealth.events.GroupAndPassEvent;
import sim.net.overlay.dht.stealth.events.UnfailAndJoinAndPassEvent;

public class KeysTest extends Default {
	public KeysTest(String[] arglist) throws Exception {
		String failType = arglist[0];					// e.g. "Stealth"
		int service = Integer.parseInt(arglist[1]);		// e.g. 10
		int stealth = Integer.parseInt(arglist[2]);		// e.g. 990
		long churnTime = Long.parseLong(arglist[3]); 	// e.g. 100000
		int keys = Integer.parseInt(arglist[4]); 		// e.g. 10000
		int k = Integer.parseInt(arglist[5]);			// e.g. 1
		int gets = Integer.parseInt(arglist[6]); 		// e.g. 10
		int getTime = Integer.parseInt(arglist[7]); 	// e.g. 360000
		// percentage of stealth nodes is logged as arglist[8]
		int recoveryType = 0;
		if (arglist.length > 9) {
			recoveryType = Integer.parseInt(arglist[9]);
		}
		/* no recovery by default */
		sim.net.overlay.dht.stealth.Peer.USE_RECOVERY_PIGGYBACK = false;
		sim.net.overlay.dht.stealth.Peer.USE_RECOVERY_POLLING = false;
		sim.net.overlay.dht.stealth.Peer.USE_RECOVERY_REJOIN = false;

		switch(recoveryType) {
		case 1:
			sim.net.overlay.dht.stealth.Peer.USE_RECOVERY_PIGGYBACK = true;
			break;
		case 2:
			sim.net.overlay.dht.stealth.Peer.USE_RECOVERY_POLLING = true;
			break;
		case 3:
			sim.net.overlay.dht.stealth.Peer.USE_RECOVERY_REJOIN = true;
			break;
		}

		int count = service + stealth;

		int timeToDoGets = (getTime * gets);
		long betweenGlobalChurns = churnTime / count;
		long betweenGlobalGets = getTime / count;

		int numberOfGets = gets * count;
		int numberOfChurns = (int)(timeToDoGets / betweenGlobalChurns);
		Distribution joinDistribution = new Exponential(betweenGlobalChurns);
		Distribution getDistribution = new Exponential(betweenGlobalGets);

		// create and join service and stealth nodes
		setupPeers(service,stealth);

		//Iterator iii = Global.hosts.getType(Peer.class).iterator();
		//while (iii.hasNext()) {
		//	System.out.println( iii.next() );
		//}

		// determine which set, if any, of peers to fail
		Host[] failable = null;
		HostSet failablePeers1 = null;
		HostSet failablePeers2 = null;

		if (failType.equals("Stealth")) {
			failablePeers1 = Global.hosts.getType(StealthPeer.class);
			failable = failablePeers1.toArray(new Host[failablePeers1.size()]);
		} else if (failType.equals("Service")) {
			failablePeers1 = Global.hosts.getType(ServicePeer.class);
			failable = failablePeers1.toArray(new Host[failablePeers1.size()]);
		} else if (failType.equals("Both")) {
			failablePeers1 = Global.hosts.getType(ServicePeer.class);
			failablePeers2 = Global.hosts.getType(StealthPeer.class);

			failable = new Host[failablePeers1.size() + failablePeers2.size()];
			System.arraycopy(failablePeers1.toArray(), 0, failable, 0, failablePeers1.size());
			System.arraycopy(failablePeers2.toArray(), 0, failable, failablePeers1.size(), failablePeers2.size());
		}

		int startFailed = 0;

		if (failable != null) {
			Collections.shuffle(Arrays.asList( failable ), Global.rand);
			startFailed = failable.length / 2;
		}

		// fail half of the entire set at the beginning of the simulation
		for (int i = 0; i < startFailed; i++) {
			Peer p = (Peer)failable[i];
			p.setFailed(true);
		}

		fastJoinAll();
		// quickly add real keys to the active service nodes, replication factor k
		addPeerData(keys,k);

		// set up a vector of rejoins/failures to pass into the GroupAndPassEvent
		// according to the joinDistribution
		if (failablePeers1 != null) {
			List<Event> events = new ArrayList<Event>() ;
			events.add(UnfailAndJoinAndPassEvent.newEvent(null, failablePeers1, 0));
			events.add(FailAndPassEvent.newEvent(null, failablePeers1, 0));
			if (failablePeers2 != null) {
				events.add(UnfailAndJoinAndPassEvent.newEvent(null, failablePeers2, 0));
				events.add(FailAndPassEvent.newEvent(null, failablePeers2, 0));

				joinDistribution = new Exponential(betweenGlobalChurns * 2);
			}

			// set up node churning
			Events.addNow(GroupAndPassEvent.newEvent(joinDistribution, numberOfChurns, events));
		}
		// set up get operations
		Events.addNow(GetAndPassEvent.newEvent(getDistribution, numberOfGets));

		long simDuation = Events.getLastTime();
		final int RTDiffEvents = 5;
		for (int ii = 0; ii < RTDiffEvents; ii++) {
			Events.add(RoutingTableStalenessEvent.newEvent(), (ii * simDuation) / RTDiffEvents);
		}
	}
}