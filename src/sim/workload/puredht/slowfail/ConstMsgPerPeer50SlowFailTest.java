package sim.workload.puredht.slowfail;

import sim.events.Events;
import sim.workload.Default;

public class ConstMsgPerPeer50SlowFailTest extends Default {
	public ConstMsgPerPeer50SlowFailTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);
		double fail = Double.parseDouble(arglist[1]);

		setupNormal(count);
		sendRandomMessagesPerPeer(50, false);
		slowFailFromStart((int)(fail*count), Events.getLastTime());
	}
}
