package sim.workload.authentication;

import sim.events.Events;
import sim.main.Global;
import sim.math.Distribution;
import sim.math.Exponential;
import sim.net.HostSet;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.stealth.Peer;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.StealthPeer;
import sim.net.overlay.dht.stealth.events.JoinAndPassEvent;
import sim.stats.StatsObject;

public class MaliciousTest extends sim.workload.sigcomm.Default {
	public MaliciousTest(String[] arglist) throws Exception {
		int service = Integer.parseInt(arglist[0]);		// e.g. 10
		int stealth = Integer.parseInt(arglist[1]);		// e.g. 990
		int keys = Integer.parseInt(arglist[2]); 		// e.g. 10000
		int k = Integer.parseInt(arglist[3]);			// e.g. 1
		int gets = Integer.parseInt(arglist[4]); 		// e.g. 10
		int getTime = Integer.parseInt(arglist[5]); 	// e.g. 360000
		int malicious = Integer.parseInt(arglist[6]);   // e.g. 5
		boolean fastjoin = Boolean.parseBoolean(arglist[7]); // e.g. true for fastjoin on
		// percentage of stealth nodes is logged as arglist[8]
		// percentage of malicious nodes is logged as arglist[9]

		int count = service + stealth;

		long betweenGlobalGets = getTime / count;

		int numberOfGets = gets * count;
		Distribution getDistribution = new Exponential(betweenGlobalGets);

		// create and link service and stealth nodes to the network
		setupPeers(service,stealth);

		// make some malicious peers, ruining it for everyone
		HostSet peers = Global.hosts.getType(Peer.class);
		while(malicious > 0 && !peers.isEmpty()) {
			Peer curr = (Peer)peers.getRandom();

			if (!curr.malicious) {
				Global.stats.logCount("Host" + StatsObject.SEPARATOR + "MaliciousPeer");
				if (curr instanceof StealthPeer) {
					Global.stats.logCount("Host" + StatsObject.SEPARATOR + "MaliciousStealthPeer");
				}
				else {
					Global.stats.logCount("Host" + StatsObject.SEPARATOR + "MaliciousServicePeer");
				}
				curr.malicious = true;
				malicious--;
			}
			// ignore this peer from now on
			peers.remove(curr);
		}

		// join up
		if (fastjoin) {
			fastJoinAll();
		}
		else {
			joinAllRouters();
			Distribution joinDistribution = new Exponential(100000);

			peers = Global.hosts.getType(ServicePeer.class);

			// a randomly selected service node serves as the bootstrap
			ServicePeer p = (ServicePeer) peers.getRandom();
			p.join(ServicePeer.INVALID_ADDRESS);

			// join the remaining service nodes
			Events.addNow(JoinAndPassEvent.newEvent(peers, joinDistribution, peers.size()-1));

			// join the stealth nodes
			peers = Global.hosts.getType(StealthPeer.class);
			if (peers.size() > 0) {
				Events.addAfterLastEvent(JoinAndPassEvent.newEvent(joinDistribution, peers.size()));
			}
		}
		// quickly add real keys to the active service nodes, replication factor k
		addPeerData(keys,k);
		// set up get operations
		Events.addNow(GetAndPassEvent.newEvent(getDistribution, numberOfGets));
	}
}
