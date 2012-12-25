/*
 * Created on 22-Mar-2005
 */
package sim.net.overlay.tree.events;

import sim.events.Event;
import sim.events.EventException;
import sim.net.overlay.tree.tbcp.Node;

/**
 * @author Andrew Brampton
 */
public class JoinEvent extends Event {

	Node n;
	int rendezvousAddress;

	/**
	 * @throws EventException
	 */
	public JoinEvent(Node n, int rendezvousAddress) {
		this.n = n;
		this.rendezvousAddress = rendezvousAddress;
	}

	/* (non-Javadoc)
	 * @see net.bramp.p2psim.events.Event#run()
	 */
	@Override
	public void run() {
		n.join(rendezvousAddress);
	}

	@Override
	public long getEstimatedRunTime() {
		return 10000;
	}

}
