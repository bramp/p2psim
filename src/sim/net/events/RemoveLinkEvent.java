/**
 *
 */
package sim.net.events;

import java.util.Iterator;

import sim.events.Event;
import sim.main.Global;
import sim.net.Host;
import sim.net.links.Link;
import sim.net.router.Router;

/**
 * @author Andrew Brampton
 *
 */
public class RemoveLinkEvent extends Event {
	public Link link;

	/**
	 * Fails (or brings to life) a Host
	 * @param host The host to fail
	 * @param failed True - The host fails, False - The host lives
	 */
	public static RemoveLinkEvent newEvent(Link link) {
		RemoveLinkEvent e = (RemoveLinkEvent) Event.newEvent(RemoveLinkEvent.class);
		e.link = link;
		return e;
	}

	@Override
	public void run() {
		int h;

		if (link.get(0) instanceof Router)
			h = link.get(1).getAddress();
		else
			h = link.get(0).getAddress();

		// This should remove the link from the hosts
		link.disconnect();
		link.dispose(); // and dispose it

		// Now remove the non router end from routing tables
		Iterator<Host> i = Global.hosts.getType(Router.class).iterator();

		while (i.hasNext()) {
			((Router)i.next()).remoteRoutingEntry(h);
		}
	}

	@Override
	public long getEstimatedRunTime() {
		return 1;
	}
}
