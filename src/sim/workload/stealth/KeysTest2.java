package sim.workload.stealth;

import sim.math.Exponential;
import sim.net.overlay.dht.PeerData;

public class KeysTest2 extends Default {
	public KeysTest2(String[] arglist) throws Exception {
		super(arglist);

		final int service = Integer.parseInt(arglist[0]);
		final int stealth = Integer.parseInt(arglist[1]);
		final int count = service + stealth;

		setupStealth(service, stealth);

		// 1000 puts, no replication
		PeerData.quickHack();

		// count * 10 gets, exponentially distributed interval with 6 minute mean
		generateRandomGets( new Exponential(360000), count * 10);
	}
}
