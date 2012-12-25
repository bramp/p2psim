package sim.workload.puredht.lifetime;

import sim.math.Exponential;
import sim.net.overlay.dht.DHTInterface;
import sim.workload.Default;

public class KeysLifetimeTest extends Default {
	public KeysLifetimeTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);
		double percentage = Double.parseDouble(arglist[1]);
		int lifecount = (int)(count * percentage);

		setupNormal(Integer.parseInt(arglist[0]));
		setupLifetimes(DHTInterface.class,lifecount);
		// 1000 puts, no replication
		generateRandomPuts(1000,1,0);
		// 10000 gets, exponentially distributed interval with 6 minute mean
		generateRandomGets(new Exponential(360000),10000,0);
	}
}
