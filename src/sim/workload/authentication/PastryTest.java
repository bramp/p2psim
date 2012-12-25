/**
 *
 */
package sim.workload.authentication;

import sim.events.Events;
import sim.main.Global;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.overlay.dht.authentication.JoinAndPassEvent;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.stats.StatsNewPeriodEvent;
import sim.workload.Default;

/**
 * @author Andrew Brampton
 *
 */
public class PastryTest extends Default {

	/**
	 * @param arglist
	 * @throws Exception
	 */
	public PastryTest(String[] arglist) throws Exception {
		super(arglist);

		// This is meant to test normal Pastry, so no auth please!
		Global.auth_on = false;

		Global.stats.renameCurrentStatsPeriod("Join");

		int peers = Integer.parseInt(arglist[0]);
		int gateways = Integer.parseInt(arglist[1]);
		int getarg = Integer.parseInt(arglist[2]);

		Distribution joinDist = new Constant(100000);

		setupPeers(peers);

		joinAllRouters();

		// Now join the rest
		Events.addNow(JoinAndPassEvent.newEvent(joinDist, peers, gateways));

		Events.addAfterLastEvent(StatsNewPeriodEvent.newEvent("Gets"));

		final int gets = peers * getarg; // Each node does getarg gets
		Events.addAfterLastEvent(GetAndPassEvent.newEvent(new Constant((Events.getLastTime()) / gets), gets));
	}

}
