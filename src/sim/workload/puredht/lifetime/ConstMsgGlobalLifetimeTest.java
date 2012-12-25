package sim.workload.puredht.lifetime;

import sim.net.overlay.dht.DHTInterface;
import sim.workload.Default;

public class ConstMsgGlobalLifetimeTest extends Default {
	public ConstMsgGlobalLifetimeTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);
		double percentage = Double.parseDouble(arglist[1]);
		int lifecount = (int)(count * percentage);

		setupNormal(count);
		setupLifetimes(DHTInterface.class,lifecount);
		sendRandomMessages(100000, false, 0);
	}
}
