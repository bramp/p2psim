package sim.workload.puredht;

import sim.events.Events;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.overlay.dht.events.repeatable.JoinAndPassEvent;
import sim.workload.Default;

public class JoinsTest extends Default {
	public JoinsTest(String[] arglist) throws Exception {
		super(arglist);

		int count = Integer.parseInt(arglist[0]);
		Distribution joinDistribution = new Constant(100000);

		setupPeers(count);

		joinAllRouters();

		// Now join the rest
		Events.addNow(JoinAndPassEvent.newEvent(joinDistribution, count));
	}
}
