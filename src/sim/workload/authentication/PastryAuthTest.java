/**
 *
 */
package sim.workload.authentication;

import static sim.stats.StatsObject.SEPARATOR;

import java.util.Iterator;

import sim.events.Events;
import sim.main.Global;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.Host;
import sim.net.overlay.dht.Message;
import sim.net.overlay.dht.authentication.AuthData;
import sim.net.overlay.dht.events.repeatable.AuthJoinAndPassEvent;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.pastry.Peer;
import sim.stats.StatsNewPeriodEvent;
import sim.workload.Default;

/**
 * @author Andrew Brampton
 *
 */
public class PastryAuthTest extends Default {

	/**
	 * @param arglist
	 * @throws Exception
	 */
	public PastryAuthTest(String[] arglist) throws Exception {
		super(arglist);

		Global.auth_on = true;

		Global.stats.renameCurrentStatsPeriod("Join");

		int peers = Integer.parseInt(arglist[0]);
		int authservers = Integer.parseInt(arglist[1]);
		int getarg = Integer.parseInt(arglist[2]);

		Distribution joinDist = new Constant(100000);

		setupPeers(peers);

		joinAllRouters();

		// Now join the rest
		Events.addNow(AuthJoinAndPassEvent.newEvent(joinDist, peers, authservers));

		Events.addAfterLastEvent(StatsNewPeriodEvent.newEvent("Gets"));

		final int gets = peers * getarg; // Each node does getarg gets
		Events.addAfterLastEvent(GetAndPassEvent.newEvent(new Constant((Events.getLastTime()) / gets), gets));
	}

	public void simulationFinished() {
		if (Global.auth_on) {
			// Quick debug check to see if any AuthDatas or Messages were waited for forever
			Iterator<Host> i = Global.hosts.getType(Peer.class).iterator();
			int waitingAuths = 0;

			while (i.hasNext()) {
				Peer p = (Peer) i.next();

				Iterator<AuthData> ii = p.authQueue.iterator();
				while (ii.hasNext())
					System.out.println("AUTHERROR " + p + " " + ii.next());

				Iterator<Message> iii = p.msgQueue.iterator();
				while (iii.hasNext())
					System.out.println("AUTHERROR " + p + " " + iii.next());

				waitingAuths += p.authQueue.size() + p.msgQueue.size();
			}

			Global.stats.logValue("Sim" + SEPARATOR + "Auth" + SEPARATOR + "AuthsWaiting", waitingAuths);
		}
	}

}
