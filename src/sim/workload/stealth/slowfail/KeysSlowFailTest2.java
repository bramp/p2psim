package sim.workload.stealth.slowfail;

import sim.events.Events;
import sim.math.Distribution;
import sim.math.Exponential;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.events.repeatable.PutAndPassEvent;
import sim.workload.stealth.Default;

public class KeysSlowFailTest2 extends Default {
	public KeysSlowFailTest2(String[] arglist) throws Exception {
		super(arglist);
		int normal = Integer.parseInt(arglist[0]);
		int stealth = Integer.parseInt(arglist[1]);
		double fail = Double.parseDouble(arglist[2]);

		setupStealth(normal, stealth);

		// 1000 puts, no replication
		Events.addAfterLastEvent(PutAndPassEvent.newEvent(1000, 1));

		// 10000 gets, exponentially distributed interval with 6 minute mean
		Distribution d = new Exponential(360000);
		Events.addAfterLastEvent(GetAndPassEvent.newEvent(d, 10000));

		slowFailNormalFromStart((int)(normal * fail), Events.getLastTime());
		slowFailStealthFromStart((int)(stealth * fail), Events.getLastTime());
	}
}
