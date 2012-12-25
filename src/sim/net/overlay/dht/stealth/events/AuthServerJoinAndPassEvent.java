/*
 * Created on 13-May-2005
 */
package sim.net.overlay.dht.stealth.events;

import sim.events.Event;
import sim.events.Events;
import sim.main.Global;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.authentication.AuthData;
import sim.net.overlay.dht.events.JoinEvent;
import sim.net.overlay.dht.events.repeatable.RepeatableHelperEvent;
import sim.net.overlay.dht.stealth.ServicePeer;

/**
 * Joins AuthServers only, they will join via other AuthServers
 * Each AuthServer will have a key signed by the GlobalKey
 * @author Andrew Brampton
 *
 */
public class AuthServerJoinAndPassEvent extends RepeatableHelperEvent {

	static final int DEFAULT_DELAY = 10000; // The time between each event

	public void init(Distribution d, int authservers) {
		super.init(d, authservers);
	}

	/**
	 *
	 * @param count
	 * @return
	 */
	public static AuthServerJoinAndPassEvent newEvent(int authservers) {
		return newEvent( new Constant(DEFAULT_DELAY), authservers);
	}

	/**
	 * Joins and Auths nodes
	 * @param d The intra-join time of the nodes
	 * @param authservers The first X nodes become auth servers
	 * @return
	 */
	public static AuthServerJoinAndPassEvent newEvent(Distribution d, int authservers) {

		if (!Global.auth_on)
			throw new RuntimeException("Auth is not on!");

		AuthServerJoinAndPassEvent e = (AuthServerJoinAndPassEvent) Event.newEvent(AuthServerJoinAndPassEvent.class);
		e.init(d, authservers);
		return e;
	}

	@Override
	public void run() throws Exception {

		HostSet joined = getAuthGateways();
		HostSet notjoined = getNotJoinedPeers().getType(ServicePeer.class);

		DHTInterface p = (DHTInterface) notjoined.getRandom();

		// Set some special auth data
		//Events.addNow(SetAuthEvent.newEvent(p, new AuthData(p.getID(), AuthData.GLOBAL_KEY)) );
		p.setAuth(new AuthData(p.getID(), AuthData.GLOBAL_KEY));

		if (joined.isEmpty()) {
			// Do special first join
			Events.addNow(JoinEvent.newEvent(p, Host.INVALID_ADDRESS));
		} else {
			// Do a normal join
			Host h = joined.getRandom();
			Events.addNow(JoinEvent.newEvent(p, h.getAddress()));
		}

		reschedule();
	}
}
