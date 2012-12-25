/**
 *
 */
package sim.workload.authentication;

import sim.events.Events;
import sim.main.Global;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.overlay.dht.PeerData;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.StealthPeer;
import sim.net.overlay.dht.stealth.events.JoinAndPassEvent;
import sim.stats.StatsNewPeriodEvent;
import sim.workload.stealth.Default;

/**
 * @author Andrew Brampton
 *
 */
public class StealthNoAuthTest extends Default {

	/**
	 * @param arglist
	 * @throws Exception
	 */
	public StealthNoAuthTest(String[] arglist) throws Exception {
		super(arglist);

		Global.auth_on = false;

		int service = Integer.parseInt(arglist[0]);
		int stealth = Integer.parseInt(arglist[1]);

		Distribution joinDist = new Constant(100000);

		setupPeers(service, stealth);

		joinAllRouters();

		// Add the data into the network
		PeerData.quickHack();

		// Join the Service Nodes
		Global.stats.renameCurrentStatsPeriod("ServiceJoin");
		Events.addAfterLastEvent(JoinAndPassEvent.newEvent(Global.hosts.getType(ServicePeer.class), joinDist, service));

		// Join the Stealth Nodes
		Events.addAfterLastEvent(StatsNewPeriodEvent.newEvent("StealthJoin"));
		Events.addAfterLastEvent(JoinAndPassEvent.newEvent(joinDist, stealth));

		// Do the Gets
		Events.addAfterLastEvent(StatsNewPeriodEvent.newEvent("Get"));
		final int gets = stealth * 100; // Each node does 100 gets
		Events.addAfterLastEvent(GetAndPassEvent.newEvent(Global.hosts.getType(StealthPeer.class), new Constant((Events.getLastTime()) / gets), gets));
	}

}
