package sim.workload.puredht.simulfail;

import sim.math.Exponential;
import sim.workload.Default;

public class KeysReplicaSimulFailTest extends Default {
	public KeysReplicaSimulFailTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);
		double fail = Double.parseDouble(arglist[1]);

		setupSimulFail((int)(fail*count),count);
		// 1000 puts, k = 3
		generateRandomPuts(1000,3);
		// 10000 gets, exponentially distributed interval with 6 minute mean
		generateRandomGets(new Exponential(360000),10000);
	}
}
