package sim.workload.puredht;

import sim.math.Exponential;
import sim.net.overlay.dht.PeerData;
import sim.workload.Default;

public class KeysTest extends Default {
	public KeysTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);

		setupNormal(count);

		// 1000 puts, no replication
		//generateRandomPuts(1000,1);
		PeerData.quickHack();

		// 10000 gets, exponentially distributed interval with 6 minute mean
		generateRandomGets(new Exponential(360000), count * 10);
	}
}
