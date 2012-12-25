package sim.workload.stealth;

import java.util.Iterator;

import sim.events.Events;
import sim.main.Global;
import sim.math.Distribution;
import sim.math.Exponential;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.NodeAddressPairs;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.events.JoinAndPassEvent;

/**
 * Send messages between random pairs of Stealth and Service node
 * @author Andrew Brampton
 *
 */
public class RoutingTableTest extends Default {
	public RoutingTableTest(String[] arglist) throws Exception {
		super(arglist);

		int service_nodes = Integer.parseInt(arglist[0]);

		// log_2^b(N) (h rows, 2b col - 1 empty expected per row)
		double h = Math.ceil(Math.log(service_nodes)/Math.log(16));

		boolean useQuickJoin = Boolean.parseBoolean(arglist[1]);

		setupPeers(service_nodes, 0);

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
		}

		// investigate routing table 'fullness'
		HostSet testpeers = Global.hosts.getType(ServicePeer.class);

		Iterator<Host> i = testpeers.iterator();

		while(i.hasNext()) {
			int empty = 0;
			int full = 0;

			for (int r = 0; r < h; r++) {

				NodeAddressPairs[] row = ((ServicePeer)i.next()).routingTable.getRow(r);

				// only check h rows
				for (int y = 0; y < row.length; y++) {
					if (row[y] == null) {
						empty++;
					}
					else {
						full++;
					}
				}
			}

			//System.out.println(h + " " + full + " " + empty + " " + (full + empty));

			// remove the expected h empty entries
			empty -= h;

			// how empty are the tables?
			double emptyness = empty / (double)(empty + full);

			Global.stats.logAverage("TableEmptiness",emptyness);
		}
	}
}
