/*
 * Created on 05-Mar-2005
 */
package sim.net.overlay.dht.events;

import sim.events.Event;
import sim.main.Global;
import sim.net.overlay.dht.DHTInterface;
import sim.stats.StatsObject;

/**
 * A event for a peer joining the network
 * @author Andrew Brampton
 */
public class AuthJoinEvent extends Event {

	protected final static int JOINTIME = 10000;

	DHTInterface peer;
	int authAddress;

	/**
	 * Joins a Peer
	 * @param peer The peer to be joining
	 * @param joinAddress The address of the node to join to
	 */
	public static AuthJoinEvent newEvent(DHTInterface peer, int authAddress) {
		AuthJoinEvent e = (AuthJoinEvent) Event.newEvent(AuthJoinEvent.class);
		e.peer = peer;
		e.authAddress = authAddress;
		return e;
	}

	/* (non-Javadoc)
	 * @see net.bramp.p2psim.events.Event#run()
	 */
	@Override
	public void run() {
		Global.stats.logCount("DHT" + StatsObject.SEPARATOR + "AuthJoinEvent");
		peer.auth(authAddress);
	}

	@Override
	public long getEstimatedRunTime() {
		return JOINTIME;
	}

}
