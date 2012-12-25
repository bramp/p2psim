package sim.workload.puredht.slowfail;

import sim.events.Events;
import sim.workload.Default;

public class ConstMsgGlobalSlowFailTest extends Default {
	public ConstMsgGlobalSlowFailTest(String[] arglist) throws Exception {
		super(arglist);
		int count = Integer.parseInt(arglist[0]);
		double fail = Double.parseDouble(arglist[1]);

		setupNormal(count);
		sendRandomMessages(100000, false);
		slowFailFromStart((int)(fail*count), Events.getLastTime());
	}
}
