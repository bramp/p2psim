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
public class JoinEvent extends Event {

	protected final static int JOINTIME = 10000;

	DHTInterface peer;
	int joinAddress;

	/**
	 * Joins a Peer
	 * @param peer The peer to be joining
	 * @param joinAddress The address of the node to join to
	 */
	public static JoinEvent newEvent(DHTInterface peer, int joinAddress) {
		JoinEvent e = (JoinEvent) Event.newEvent(JoinEvent.class);
		e.peer = peer;
		e.joinAddress = joinAddress;
		return e;
	}

	/* (non-Javadoc)
	 * @see net.bramp.p2psim.events.Event#run()
	 */
	@Override
	public void run() {
		Global.stats.logCount("DHT" + StatsObject.SEPARATOR + "JoinEvent");
		peer.join(joinAddress);
	}

	@Override
	public long getEstimatedRunTime() {
		return JOINTIME;
	}

}
