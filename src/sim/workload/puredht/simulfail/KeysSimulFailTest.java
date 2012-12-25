package sim.workload.puredht.simulfail;

import sim.math.Exponential;
import sim.workload.Default;

public class KeysSimulFailTest extends Default {
	public KeysSimulFailTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);
		double fail = Double.parseDouble(arglist[1]);

		setupSimulFail((int)(fail*count),count);
		// 1000 puts, no replication
		generateRandomPuts(1000,1);
		// 10000 gets, exponentially distributed interval with 6 minute mean
		generateRandomGets(new Exponential(360000),10000);
	}
}
