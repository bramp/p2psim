/**
 *
 */
package sim.workload.swarm.mobile;

import sim.events.Event;
import sim.events.Events;
import sim.math.Distribution;
import sim.math.Exponential;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.events.repeatable.PutAndPassEvent;
import sim.workload.swarm.RoutingTableLatencyEvent;

/**
 * @author Andrew Brampton
 *
 */
public class StaticPastryTest extends Default {
	public StaticPastryTest(String[] arglist) throws Exception {
		super(arglist);

		final int highcap = Integer.parseInt(arglist[0]);
		final int lowcap = Integer.parseInt(arglist[1]);
		final int total = highcap + lowcap;

		setupPeers(highcap, 1.0);
		setupPeers(lowcap, 1.0);

		fastJoinAllNodes();

		// 1000 puts, no replication
		Event e = Events.addAfterLastEvent( PutAndPassEvent.newEvent(1000, 1) );

		long startTime = Events.getLastTime();

		// 10000 gets, exponentially distributed interval with 6 minute mean
		// / (wifi + fixed) so that each peer has a 6min mean
		Distribution d = new Exponential(360000 / total);
		Events.addAfterEvent(e, GetAndPassEvent.newEvent(d, total * 10) );

		// Take routing table snapshots
		long simDuation = Events.getLastTime() - startTime;
		final int RTDiffEvents = 15;
		for (int ii = 0; ii < RTDiffEvents; ii++) {
			Events.add(RoutingTableLatencyEvent.newEvent(), (ii * simDuation) / RTDiffEvents + startTime - 1);
		}
	}
}
