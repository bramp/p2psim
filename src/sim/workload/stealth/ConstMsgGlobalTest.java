package sim.workload.stealth;

import sim.events.Events;
import sim.main.Global;
import sim.math.Distribution;
import sim.math.Exponential;
import sim.net.HostSet;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.StealthPeer;
import sim.net.overlay.dht.stealth.events.JoinAndPassEvent;

/**
 * Send messages between random pairs of Stealth and Service node
 * @author Andrew Brampton
 *
 */
public class ConstMsgGlobalTest extends Default {
	public ConstMsgGlobalTest(String[] arglist) throws Exception {
		super(arglist);

		int service_nodes = Integer.parseInt(arglist[0]);
		int stealth_nodes = Integer.parseInt(arglist[1]);
		int number_of_messages = Integer.parseInt(arglist[2]);

		boolean useQuickJoin = Boolean.parseBoolean(arglist[3]);
		boolean direct = Boolean.parseBoolean(arglist[4]);

		setupPeers(service_nodes, stealth_nodes);

		if (useQuickJoin) {
			fastJoinAllNodes();
		} else {
			joinAllRouters();
			Distribution joinDistribution = new Exponential(100000);

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

		sendRandomMessages(number_of_messages, direct);
	}
}
