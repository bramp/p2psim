/**
 *
 */
package sim.workload.sigcomm;

import java.util.Iterator;

import sim.events.Event;
import sim.events.Events;
import sim.main.Global;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.NodeAddressPair;
import sim.net.overlay.dht.NodeAddressPairs;
import sim.net.overlay.dht.stealth.StealthPeer;
import sim.stats.StatsObject;

/**
 * This event looks at every StealthPeer's routing table and compares the proximity
 * metric they know to the real proximity metric. This allows us to judge how
 * well the metric works over time
 *
 * @author Andrew Brampton
 *
 */
public class RoutingTableStalenessEvent extends Event {

	public static RoutingTableStalenessEvent newEvent() {
		RoutingTableStalenessEvent e = (RoutingTableStalenessEvent) Event.newEvent(RoutingTableStalenessEvent.class);
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
		HostSet hosts = Global.hosts.getType(StealthPeer.class);

		final String stat = "RTStale(" + Events.getTime() + ")";
		final String stat2 = "RTValid(" + Events.getTime() + ")";

		final String stat3 = "RTFrac(" + Events.getTime() + ")";

		Iterator<Host> i = hosts.iterator();
		while (i.hasNext()) {
			StealthPeer p = (StealthPeer)i.next();

			int stale = 0;
			int valid = 0;

			for (int x = 0; x < p.routingTable.getRows(); x++) {
				NodeAddressPairs[] row = p.routingTable.getRow(x);
				if (row != null) {
					for (int y = 0; y < row.length; y++) {
						NodeAddressPairs cell = row[y];
						if (cell != null) {
							Iterator<NodeAddressPair> c = cell.iterator();
							while (c.hasNext()) {
								NodeAddressPair pair = c.next();

								if ( Global.hosts.get(pair.address).hasFailed() ) {
									Global.stats.logCount(stat);
									stale++;
								} else {
									Global.stats.logCount(stat2);
									valid++;
								}
							}
						}
					}
				}
			}

			if ((valid + stale) > 0)
				Global.stats.logAverage(stat3, (double)stale / (double)(valid + stale));
		}

		Global.stats.logAverage("RTStale_Total", Global.stats.getValue(stat + StatsObject.COUNT) );
		Global.stats.logAverage("RTValid_Total", Global.stats.getValue(stat2 + StatsObject.COUNT) );
		Global.stats.logAverage("RTFrac_Total", Global.stats.getValue(stat3 + StatsObject.AVERAGE) );
	}
}
