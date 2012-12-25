package sim.workload.puredht;

import sim.math.Exponential;
import sim.workload.Default;

public class KeysReplicaTest extends Default {
	public KeysReplicaTest(String[] arglist) throws Exception {
		super(arglist);
		setupNormal(Integer.parseInt(arglist[0]));
		// 1000 puts, k = 3
		generateRandomPuts(1000,3);
		// 10000 gets, exponentially distributed interval with 6 minute mean
		generateRandomGets(new Exponential(360000),10000);
	}
}
