package sim.workload.stealth;

import sim.math.Exponential;

public class KeysReplicaTest extends Default {
	public KeysReplicaTest(String[] arglist) throws Exception {
		super(arglist);
		setupStealth(Integer.parseInt(arglist[0]), Double.parseDouble(arglist[1]));
		// 1000 puts, k = 3
		generateRandomPuts(1000,3);
		// 10000 gets, exponentially distributed interval with 6 minute mean
		generateRandomGets(new Exponential(360000),10000);
	}
}
