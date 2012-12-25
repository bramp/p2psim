/**
 *
 */
package sim.workload.swarm;

import java.util.Iterator;

import sim.events.Event;
import sim.events.Events;
import sim.main.Global;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.NodeAddressPair;
import sim.net.overlay.dht.NodeAddressPairs;
import sim.net.overlay.dht.pastry.Peer;
import sim.net.overlay.dht.swarm.RoutingTable;

/**
 * This event looks at every Peer's routing table and compares the proximity
 * metric they know to the real proximity metric. This allows us to judge how
 * well the metric works over time
 *
 * @author Andrew Brampton
 *
 */
public class RoutingTableLatencyEvent extends Event {

	public static RoutingTableLatencyEvent newEvent() {
		RoutingTableLatencyEvent e = (RoutingTableLatencyEvent) Event.newEvent(RoutingTableLatencyEvent.class);
		return e;
	}

	/* (non-Javadoc)
	 * @see sim.events.Event#getEstimatedRunTime()
	 */
	@Override
	public long getEstimatedRunTime() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see sim.events.Event#run()
	 */
	@Override
	public void run() throws Exception {
		HostSet hosts = Global.hosts.getType(Peer.class);

		final String stat = "RTDiff(" + Events.getTime() + ")";
		final String stat2 = "RTDiffAbs(" + Events.getTime() + ")";
		final String stat3 = "RTBestChoice(" + Events.getTime() + ")";

		//Global.trace.println(LogLevel.DEBUG, "RoutingTableLatencyEvent");

		Iterator<Host> i = hosts.iterator();
		while (i.hasNext()) {
			Peer p = (Peer)i.next();

			for (int x = 0; x < p.routingTable.getRows(); x++) {
				NodeAddressPairs[] row = p.routingTable.getRow(x);
				if (row != null) {
					for (int y = 0; y < row.length; y++) {
						NodeAddressPairs cell = row[y];
						if (cell != null) {
							NodeAddressPair pair;
							if (RoutingTable.USECAPABILITY) {
								pair = RoutingTable.findCapClosest(cell);
							} else if (RoutingTable.USELOCALITY) {
								pair = cell.findProxClosest();
							} else {
								//pair = cell.findNumClosest(ID);
								pair = cell.first();
								//throw new RuntimeException("Can't find best routing table match");
							}

							if (pair.rtt != Integer.MAX_VALUE) {

								// Figure out the difference between the real and false value
								int difference = pair.rtt - p.getUnicastDelay(pair.address) * 2;

								Global.stats.logAverage(stat, difference);
								Global.stats.logAverage(stat2, Math.abs(difference));

								// Now work out if the person you would send to is the best choice or not
								int best = pair.rtt;
								for (Iterator<NodeAddressPair> ii = cell.iterator(); ii.hasNext(); ) {
									int delay = p.getUnicastDelay(ii.next().address) * 2;
									if (delay < best)
										best = delay;
								}
								// If the we haven't chosen the best
								if (best != pair.rtt)
									Global.stats.logAverage(stat3, 0);
								else
									Global.stats.logAverage(stat3, 1);
							}
						}
					}
				}
			}
		}

	}

}
