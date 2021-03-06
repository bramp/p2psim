package sim.workload.stealth.lifetime;

import sim.math.Exponential;
import sim.net.overlay.dht.stealth.StealthPeer;
import sim.workload.stealth.Default;

public class KeysLifetimeTest extends Default {
	public KeysLifetimeTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);
		double ratio = Double.parseDouble(arglist[1]);

		int normal = (int) (count * ratio);
		if (normal == 0)
			return;

		setupStealth(count, ratio);
		setupLifetimes(StealthPeer.class,-1);

		// 1000 puts, no replication
		generateRandomPuts(1000,1);

		// 10000 gets, exponentially distributed interval with 6 minute mean
		generateRandomGets(new Exponential(360000),10000);
	}
}
