/**
 *
 */
package sim.workload.swarm.mobile;

import java.util.Iterator;

import sim.events.Event;
import sim.events.Events;
import sim.math.Distribution;
import sim.math.Exponential;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.events.RandomWaypointEvent;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.events.repeatable.PutAndPassEvent;
import sim.workload.swarm.RoutingTableLatencyEvent;

/**
 * @author Andrew Brampton
 *
 */
public class MovingSwarmTest extends Default {

	public MovingSwarmTest(String[] arglist) throws Exception {
		super(arglist);

		final int highcap = Integer.parseInt(arglist[0]);
		final int lowcap = Integer.parseInt(arglist[1]);
		final int total = highcap + lowcap;

		setupPeers(highcap, 0.1);
		HostSet low = setupPeers(lowcap, 0.9);

		fastJoinAllNodes();

		// 1000 puts, no replication
		Event e = Events.addAfterLastEvent( PutAndPassEvent.newEvent(1000, 1) );

		long startTime = Events.getLastTime();

		// Each peer moves this many times
		int movements = 10;

		// Setup waypoints for each node
		Iterator<Host> i = low.iterator();

		Distribution d = new Exponential(600000); // Move every 10min
		while (i.hasNext()) {
			long addIn = d.nextLong() / 3; // Set the waypoints to start within 60/3 min
			Events.addAfterEvent(e, RandomWaypointEvent.newEvent(i.next(), movements, d, false), addIn );
		}

		// 10000 gets, exponentially distributed interval with 6 minute mean
		// / (wifi + fixed) so that each peer has a 6min mean
		d = new Exponential(360000 / total);
		Events.addAfterEvent(e, GetAndPassEvent.newEvent(d, total * 10) );

		// Take routing table snapshots
		long simDuation = Events.getLastTime() - startTime;
		final int RTDiffEvents = 15;
		for (int ii = 0; ii < RTDiffEvents; ii++) {
			Events.add(RoutingTableLatencyEvent.newEvent(), (ii * simDuation) / RTDiffEvents + startTime - 1);
		}
	}
}
