package sim.workload.big;

import sim.events.Events;
import sim.math.Distribution;
import sim.math.Exponential;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.events.repeatable.PutAndPassEvent;
import sim.workload.stealth.Default;

public class KeysTest extends Default {
	public KeysTest(String[] arglist) throws Exception {
		super(arglist);

		int normal = Integer.parseInt(arglist[0]);
		int stealth = Integer.parseInt(arglist[1]);

		setupPeers(normal, stealth);
		fastJoinAllNodes();

		// 1000 puts, no replication
		Events.addAfterLastEvent( PutAndPassEvent.newEvent(1000, 1) );

		// 10000 gets, exponentially distributed interval with 6 minute mean
		Distribution d = new Exponential(360000);
		Events.addAfterLastEvent(GetAndPassEvent.newEvent(d, stealth * 3));
	}
}
