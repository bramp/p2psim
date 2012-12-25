/**
 *
 */
package sim.workload.mobile;

import sim.events.Event;
import sim.events.Events;
import sim.math.Distribution;
import sim.math.Exponential;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.events.repeatable.PutAndPassEvent;

/**
 * @author Andrew Brampton
 *
 */
public class StaticPastryTest extends Default {
	public StaticPastryTest(String[] arglist) throws Exception {
		super(arglist);

		final int wifistealth = Integer.parseInt(arglist[0]);
		final int fixedstealth = Integer.parseInt(arglist[1]);
		final int wifiservice = Integer.parseInt(arglist[2]);
		final int fixedservice = Integer.parseInt(arglist[3]);
		final int total = wifistealth + fixedstealth + wifiservice + fixedservice;

		setupWifiPeers(wifistealth + wifiservice);
		setupPeers(fixedstealth + fixedservice);

		fastJoinAllNodes();

		// 1000 puts, no replication
		Event e = Events.addAfterLastEvent( PutAndPassEvent.newEvent(1000, 1) );

		// 10000 gets, exponentially distributed interval with 6 minute mean
		// / (wifi + fixed) so that each peer has a 6min mean
		Distribution d = new Exponential(360000 / total);
		Events.addAfterEvent(e, GetAndPassEvent.newEvent(d, total * 10) );
	}
}
