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
import sim.net.overlay.dht.PeerData;
import sim.net.overlay.dht.authentication.AuthData;
import sim.net.overlay.dht.events.repeatable.GetAndPassEvent;
import sim.net.overlay.dht.pastry.Peer;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.net.overlay.dht.stealth.StealthPeer;
import sim.net.overlay.dht.stealth.events.AuthServerJoinAndPassEvent;
import sim.net.overlay.dht.stealth.events.JoinAuthAndPassEvent;
import sim.stats.StatsNewPeriodEvent;
import sim.workload.stealth.Default;

/**
 * @author Andrew Brampton
 *
 */
public class StealthAuthTest extends Default {

	/**
	 * @param arglist
	 * @throws Exception
	 */
	public StealthAuthTest(String[] arglist) throws Exception {
		super(arglist);

		Global.auth_on = true;

		int authservers = Integer.parseInt(arglist[0]);
		int service = Integer.parseInt(arglist[1]);
		int stealth = Integer.parseInt(arglist[2]);
		int type = Integer.parseInt(arglist[3]);
		boolean chain = Boolean.parseBoolean(arglist[4]);

		if (authservers > service)
			throw new RuntimeException( "Can't have more authservers than service nodes!" );

		if (type == 0) {
			Global.auth_per_hop = false;
			Global.auth_per_session = false;
		} else if (type == 1) {
			Global.auth_per_hop = true;
			Global.auth_per_session = false;
		} else if (type == 2) {
			Global.auth_per_hop = false;
			Global.auth_per_session = true;
		}

		Global.auth_add_chain = chain;

		Distribution joinDist = new Constant(100000);

		setupPeers(service, stealth);

		joinAllRouters();

		// Add the data into the network
		PeerData.quickHack();

		//  Join the AuthServers
		Global.stats.renameCurrentStatsPeriod("AuthJoin");
		Events.addNow(AuthServerJoinAndPassEvent.newEvent(joinDist, authservers));

		// Join the Service Nodes
		Events.addAfterLastEvent(StatsNewPeriodEvent.newEvent("ServiceJoin"));
		Events.addAfterLastEvent(JoinAuthAndPassEvent.newEvent(Global.hosts.getType(ServicePeer.class), joinDist, service));

		// Join the Stealth Nodes
		Events.addAfterLastEvent(StatsNewPeriodEvent.newEvent("StealthJoin"));
		Events.addAfterLastEvent(JoinAuthAndPassEvent.newEvent(joinDist, stealth));

		// Do the Gets
		Events.addAfterLastEvent(StatsNewPeriodEvent.newEvent("Get"));
		final int gets = stealth * 100; // Each node does 100 gets
		Events.addAfterLastEvent(GetAndPassEvent.newEvent(Global.hosts.getType(StealthPeer.class), new Constant((Events.getLastTime()) / gets), gets));
	}

	public void simulationFinished() {
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
