package sim.workload.stealth.slowfail;

import sim.events.Events;
import sim.workload.stealth.Default;

/**
 *
 * @author Andrew Brampton
 *
 */
public class ConstMsgGlobalSlowFailTest extends Default {

	/**
	 *
	 * @param arglist [0] Total Peers, [1] Ratio of Normal Peers, [2] Percentage of failed Peers
	 * @throws Exception
	 */
	public ConstMsgGlobalSlowFailTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);
		double ratio = Double.parseDouble(arglist[1]);
		double fail = Double.parseDouble(arglist[2]);

		int normal = (int) (count * ratio);
		if (normal == 0)
			return;

		// The / Normal * Normal is needed due to Int rounding
		int stealth = ((count - normal) / normal) * normal;

		setupStealth(count, ratio);
		sendRandomMessages(100000, false);

		slowFailNormalFromStart((int)(fail*normal), Events.getLastTime());
		slowFailStealthFromStart((int)(fail*stealth), Events.getLastTime());
	}
}
