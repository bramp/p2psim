package sim.workload.stealth.slowfail;

import sim.events.Events;
import sim.math.Exponential;
import sim.workload.stealth.Default;

public class KeysSlowFailTest extends Default {
	public KeysSlowFailTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);
		double ratio = Double.parseDouble(arglist[1]);
		double fail = Double.parseDouble(arglist[2]);

		int normal = (int) (count * ratio);
		if (normal == 0)
			return;

		// The / Normal * Normal is needed due to Int rounding
		int stealth = ((count - normal) / normal) * normal;

		setupStealth(count, ratio);

		// 1000 puts, no replication
		generateRandomPuts(1000,1);

		// 10000 gets, exponentially distributed interval with 6 minute mean
		generateRandomGets(new Exponential(360000),10000);

		slowFailNormalFromStart((int)(fail*normal), Events.getLastTime());
		slowFailStealthFromStart((int)(fail*stealth), Events.getLastTime());
	}
}
