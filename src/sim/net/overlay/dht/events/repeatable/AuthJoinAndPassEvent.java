/*
 * Created on 13-May-2005
 */
package sim.net.overlay.dht.events.repeatable;

import sim.events.Event;
import sim.events.Events;
import sim.main.Global;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.authentication.AuthData;
import sim.net.overlay.dht.events.AuthJoinEvent;
import sim.net.overlay.dht.events.JoinEvent;
import sim.net.overlay.dht.events.SetAuthEvent;

public class AuthJoinAndPassEvent extends RepeatableHelperEvent {

	static final int DEFAULT_DELAY = 10000; // The time between each event

	public int authservers;

	public void init(Distribution d, int joincount, int authservers) {
		init(d, joincount);
		this.authservers = authservers;
	}

	/**
	 *
	 * @param count
	 * @param authservers
	 * @return a AuthJoinAndPassEvent object
	 */
	public static AuthJoinAndPassEvent newEvent(int count, int authservers) {
		return newEvent( new Constant(DEFAULT_DELAY), count, 0 );
	}

	/**
	 * Joins and Auths nodes
	 * @param d The intra-join time of the nodes
	 * @param joincount The number of nodes to join
	 * @param authservers The first X nodes become auth servers
	 * @return A AuthJoinAndPassEvent object
	 */
	public static AuthJoinAndPassEvent newEvent(Distribution d, int joincount, int authservers) {

		if (!Global.auth_on)
			throw new RuntimeException("Auth is not on!");

		AuthJoinAndPassEvent e = (AuthJoinAndPassEvent) Event.newEvent(AuthJoinAndPassEvent.class);
		e.init(d, joincount, authservers);
		return e;
	}

	@Override
	public void run() throws Exception {

		HostSet joined = getAuthGateways();
		HostSet notjoined = getNotJoinedPeers();

		if (joined.size() < authservers ) {
			DHTInterface p = (DHTInterface) notjoined.getRandom();

			// Set some special auth data
			Events.addNow(SetAuthEvent.newEvent(p, new AuthData(p.getID(), AuthData.GLOBAL_KEY)) );

			if (joined.isEmpty()) {
				// Do special first join
				Events.addNow(JoinEvent.newEvent(p, Host.INVALID_ADDRESS));
			} else {
				// Do a normal join
				Host h = joined.getRandom();
				Events.addNow(JoinEvent.newEvent(p, h.getAddress()));
			}
		} else {
			if (!notjoined.isEmpty()) {
				Host h = joined.getRandom();
				DHTInterface p = (DHTInterface) notjoined.getRandom();

				Events.addNow(AuthJoinEvent.newEvent(p, h.getAddress()));
			}
		}

		reschedule();
	}
}
