package sim.workload.stealth;

import sim.events.Events;
import sim.main.Global;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.HostSet;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.StealthPeer;
import sim.net.overlay.dht.stealth.events.JoinAndPassEvent;

public class JoinsTest extends Default {
	public JoinsTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);
		double ratio = Double.parseDouble(arglist[1]);
		Distribution joinDistribution = new Constant(100000);

		setupPeers(count, ratio);

		joinAllRouters();

		HostSet peers = Global.hosts.getType(ServicePeer.class);
		// Randomly join the first peer
		ServicePeer p = (ServicePeer) peers.getRandom();
		p.join(ServicePeer.INVALID_ADDRESS);

		Events.addNow(JoinAndPassEvent.newEvent(peers, joinDistribution, peers.size() - 1 ));

		peers = Global.hosts.getType(StealthPeer.class);

		// Join them due to this distribution
		Events.addAfterLastEvent(JoinAndPassEvent.newEvent(joinDistribution, peers.size() ) );
		workOutAverageTableSize();
	}
}
