package sim.workload.swarm;

import sim.math.Exponential;
import sim.net.overlay.dht.PeerData;

public class KeysTestNormal extends Default {
	public KeysTestNormal(String[] arglist) throws Exception {
		super(arglist);
		int highcap = Integer.parseInt(arglist[0]);
		int lowcap = Integer.parseInt(arglist[1]);
		int count = highcap + lowcap;

		setupPeers(highcap, lowcap, 1.0, 1.0);

		// 1 million puts
		PeerData.quickHack();

		// 10000 gets, exponentially distributed interval with 6 minute mean
		generateRandomGets(new Exponential(360000), count * 10);
	}
}
