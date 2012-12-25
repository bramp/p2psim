package sim.workload.puredht.slowfail;

import sim.events.Events;
import sim.math.Exponential;
import sim.workload.Default;

public class KeysSlowFailTest extends Default {
	public KeysSlowFailTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);
		double fail = Double.parseDouble(arglist[1]);

		setupNormal(count);
		// 1000 puts, no replication
		generateRandomPuts(1000,1);
		// 10000 gets, exponentially distributed interval with 6 minute mean
		generateRandomGets(new Exponential(360000),10000);
		slowFailFromStart((int)(fail*count), Events.getLastTime());
	}
}
