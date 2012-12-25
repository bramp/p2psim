package sim.workload.stealth;

import sim.math.Exponential;
import sim.net.overlay.dht.PeerData;

public class KeysTest extends Default {
	public KeysTest(String[] arglist) throws Exception {
		super(arglist);

		int count = Integer.parseInt(arglist[0]);

		setupStealth(count, Double.parseDouble(arglist[1]));

		PeerData.quickHack();

		// count * 10 gets, exponentially distributed interval with 6 minute mean
		generateRandomGets(new Exponential(360000), count * 10);
	}
}
