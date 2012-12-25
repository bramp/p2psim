package sim.workload.puredht.lifetime;

import sim.net.overlay.dht.DHTInterface;
import sim.workload.Default;

public class ConstMsgPerPeer50LifetimeTest extends Default {
	public ConstMsgPerPeer50LifetimeTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);
		double percentage = Double.parseDouble(arglist[1]);
		int lifecount = (int)(count * percentage);

		setupNormal(Integer.parseInt(arglist[0]));
		setupLifetimes(DHTInterface.class,lifecount);
		sendRandomMessagesPerPeer(50, false, 0);
	}
}
