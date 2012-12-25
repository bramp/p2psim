package sim.workload.stealth.lifetime;

import sim.net.overlay.dht.stealth.StealthPeer;
import sim.workload.stealth.Default;

/**
 *
 * @author Andrew Brampton
 *
 */
public class ConstMsgGlobalLifetimeTest extends Default {

	/**
	 *
	 * @param arglist [0] Total Peers, [1] Ratio of Normal Peers, [2] Percentage of failed Peers
	 * @throws Exception
	 */
	public ConstMsgGlobalLifetimeTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);
		double ratio = Double.parseDouble(arglist[1]);

		int normal = (int) (count * ratio);
		if (normal == 0)
			return;

		setupStealth(count, ratio);
		setupLifetimes(StealthPeer.class, -1);
		sendRandomMessages(100000, false);
	}
}
