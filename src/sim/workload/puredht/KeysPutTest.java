package sim.workload.puredht;

import sim.workload.Default;

public class KeysPutTest extends Default {
	public KeysPutTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);

		setupNormal(count);

		// 1000 puts, no replication
		generateRandomPuts(1000000, 1);
	}
}
