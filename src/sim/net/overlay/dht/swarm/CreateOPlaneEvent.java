/*
 * Created on 15-Apr-2005
 */
package sim.net.overlay.dht.swarm;

import sim.events.Event;
import sim.events.EventException;

/**
 * @author Andrew Brampton
 */
public class CreateOPlaneEvent extends Event {

	SwarmPeer peer;
	int newOPlane;

	/**
	 * @throws EventException
	 */
	public CreateOPlaneEvent(SwarmPeer peer, int newOPlane) {
		this.peer = peer;
		this.newOPlane = newOPlane;
	}

	/* (non-Javadoc)
	 * @see net.bramp.p2psim.events.Event#run()
	 */
	@Override
	public void run() {
		peer.createOPlane(newOPlane);
	}

	@Override
	public long getEstimatedRunTime() {
		return 0;
	}

}
