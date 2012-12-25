package sim.workload.sigcomm;

import sim.events.Events;
import sim.main.Global;
import sim.math.Distribution;
import sim.math.Exponential;
import sim.net.HostSet;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.StealthPeer;
import sim.net.overlay.dht.stealth.events.JoinAndPassEvent;

public class JoinsTest extends Default {
	public JoinsTest(String[] arglist) throws Exception {
		int service = Integer.parseInt(arglist[0]);		// e.g. 10
		int stealth = Integer.parseInt(arglist[1]);		// e.g. 990
		long joinTime = Long.parseLong(arglist[2]); 	// e.g. 100000

		Distribution joinDistribution = new Exponential(joinTime);

		setupPeers(service, stealth);
		joinAllRouters();

		HostSet peers = Global.hosts.getType(ServicePeer.class);

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
}
