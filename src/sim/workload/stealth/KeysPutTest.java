package sim.workload.stealth;

public class KeysPutTest extends Default {
	public KeysPutTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);

		setupStealth(count, Double.parseDouble(arglist[1]));

		// 1 million puts, no replication
		generateRandomPuts(1000000, 1);
	}
}
