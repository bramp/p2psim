package sim.workload.stealth.slowfail;

import sim.events.Events;
import sim.workload.stealth.Default;

public class ConstMsgPerPeer100SlowFailTest extends Default {
	public ConstMsgPerPeer100SlowFailTest(String[] arglist) throws Exception {
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
		sendRandomMessagesPerPeer(100, false);

		slowFailNormalFromStart((int)(fail*normal), Events.getLastTime());
		slowFailStealthFromStart((int)(fail*stealth), Events.getLastTime());
	}
}