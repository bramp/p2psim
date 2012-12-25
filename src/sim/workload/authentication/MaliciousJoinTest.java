package sim.workload.authentication;

import java.util.Iterator;

import sim.events.Events;
import sim.main.Global;
import sim.math.Distribution;
import sim.math.Exponential;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.stealth.Peer;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.events.JoinAndPassEvent;
import sim.stats.StatsObject;

public class MaliciousJoinTest extends sim.workload.sigcomm.Default {
	public MaliciousJoinTest(String[] arglist) throws Exception {
		int service = Integer.parseInt(arglist[0]);		// e.g. 1000
		int malicious = Integer.parseInt(arglist[1]);   // e.g. 5
		// percentage of malicious nodes is logged as arglist[2]

		// invariants
		int keys = 10000;
		int k =  1;
		int gets = 10;
		int getTime = 360000;

		int half = service / 2;
		// create and link steady service nodes to the network
		setupPeers(half,0);
		HostSet firstHalf = Global.hosts.getType(ServicePeer.class);

		// quickly add real keys to the active service nodes, replication factor k
		addPeerData(keys,k);
		// create and link joining service nodes to the network
		setupPeers(half,0);

		joinAllRouters();

		// make some malicious peers
		HostSet peers = Global.hosts.getType(ServicePeer.class);
		while(malicious > 0 && !peers.isEmpty()) {
			Peer curr = (ServicePeer)peers.getRandom();

			if (!curr.malicious) {
				Global.stats.logCount("Host" + StatsObject.SEPARATOR + "MaliciousPeer");
				curr.malicious = true;
				malicious--;
			}
			// ignore this peer from now on
			peers.remove(curr);
		}

		// fastjoin for half the nodes
		Iterator<Host> i = firstHalf.iterator();
		while (i.hasNext()) {
			ServicePeer p = (ServicePeer)i.next();
			p.fastJoin(firstHalf);
		}

		// join the remainder
		Distribution joinDistribution = new Exponential(100000);
		Events.addNow(JoinAndPassEvent.newEvent(peers, joinDistribution, peers.size()));

		// set up get operations
		long betweenGlobalGets = getTime / service;
		int numberOfGets = gets * service;
		Distribution getDistribution = new Exponential(betweenGlobalGets);
		Events.addAfterLastEvent(GetAndPassEvent.newEvent(getDistribution, numberOfGets));
	}
}
