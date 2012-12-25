package sim.workload.puredht.slowfail;

import sim.events.Events;
import sim.workload.Default;

public class ConstMsgPerPeer100SlowFailTest extends Default {
	public ConstMsgPerPeer100SlowFailTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);
		double fail = Double.parseDouble(arglist[1]);

		setupNormal(count);
		sendRandomMessagesPerPeer(100, false);
		slowFailFromStart((int)(fail*count), Events.getLastTime());
	}
}