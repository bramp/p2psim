package sim.workload.stealth.lifetime;

import sim.net.overlay.dht.stealth.StealthPeer;
import sim.workload.stealth.Default;

public class ConstMsgPerPeer100LifetimeTest extends Default {
	public ConstMsgPerPeer100LifetimeTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);
		double ratio = Double.parseDouble(arglist[1]);

		int normal = (int) (count * ratio);
		if (normal == 0)
			return;

		setupStealth(count, ratio);
		setupLifetimes(StealthPeer.class, -1);
		sendRandomMessagesPerPeer(100, false);
	}
}