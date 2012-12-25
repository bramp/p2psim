/**
 *
 */
package sim.net.events;

import sim.events.Event;
import sim.net.Host;
import sim.net.links.Link;
import sim.net.links.SharedLink;
import sim.net.router.MobileRouter;
import sim.net.router.Router;

/**
 * @author Andrew Brampton
 *
 */
public class AddLinkEvent extends Event {
	public Router r;
	public Host h;

	public Link link;

	/**
	 * Fails (or brings to life) a Host
	 * @param host The host to fail
	 * @param failed True - The host fails, False - The host lives
	 */
	public static AddLinkEvent newEvent(Router r, Host h, Link link) {
		AddLinkEvent e = (AddLinkEvent) Event.newEvent(AddLinkEvent.class);
		e.r = r;
		e.h = h;
		e.link = link;
		return e;
	}

	@Override
	public void run() {

		if (r instanceof MobileRouter) {
			if (link instanceof SharedLink) {
				// Update the bandwidth and delay for this link for the new AP
				link.setBandwidth(((MobileRouter)r).getSharedBandwidth());
				link.setDelay(((MobileRouter)r).getSharedDelay());

				// Now share with this AP
				((SharedLink)link).shareWith(((MobileRouter)r).getSharedBase());
			}
		}

		link.connect(r, h);

		// Now update all the routers with the new route
		Router.updateAllRoutingEntry(h.getAddress(), r.getAddress(), link.getCost());
	}

	@Override
	public long getEstimatedRunTime() {
		return 1;
	}
}
