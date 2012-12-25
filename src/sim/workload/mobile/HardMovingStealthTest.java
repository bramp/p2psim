/**
 *
 */
package sim.workload.mobile;

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

/**
 * @author Andrew Brampton
 *
 */
public class HardMovingStealthTest extends Default {
	public HardMovingStealthTest(String[] arglist) throws Exception {
		super(arglist);

		final int wifistealth = Integer.parseInt(arglist[0]);
		final int fixedstealth = Integer.parseInt(arglist[1]);
		final int wifiservice = Integer.parseInt(arglist[2]);
		final int fixedservice = Integer.parseInt(arglist[3]);
		final int total = wifistealth + fixedstealth + wifiservice + fixedservice;

		HostSet wifipeers;

		wifipeers = setupWifiService(wifiservice);
		setupWiredService(fixedservice);

		wifipeers.addAll( setupWifiStealth(wifistealth) );
		setupWiredStealth(fixedstealth);

		fastJoinAllNodes();

		// 1000 puts, no replication
		Event e = Events.addAfterLastEvent( PutAndPassEvent.newEvent(1000, 1) );

		// Each peer moves this many times
		int movements = 10;

		// Setup waypoints for each node
		Iterator<Host> i = wifipeers.iterator();

		// Distribution d = new Exponential(3600000); // Move every 60min
		Distribution d = new Exponential(600000); // Move every 10min
		while (i.hasNext()) {
			long addIn = d.nextLong() / 3; // Set the waypoints to start within 60/3 min
			Events.addAfterEvent(e, RandomWaypointEvent.newEvent(i.next(), movements, d, true), addIn );
		}

		// 10000 gets, exponentially distributed interval with 6 minute mean
		// / (wifi + fixed) so that each peer has a 6min mean
		d = new Exponential(360000 / total);
		Events.addAfterEvent(e, GetAndPassEvent.newEvent(d, total * 10) );
	}
}
