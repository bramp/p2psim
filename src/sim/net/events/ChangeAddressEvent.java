/**
 *
 */
package sim.net.events;

import java.util.Iterator;

import sim.events.Event;
import sim.main.Global;
import sim.net.Host;
import sim.net.router.Router;


/**
 * All this event does is change IP address.
 * It makes no attempt to fix router entries
 * @author Andrew Brampton
 *
 */
public class ChangeAddressEvent extends Event {

	Host h;
	int newAddress;

	public static Event newEvent(final Host h, final int newAddress) {

		if (h instanceof Router)
			throw new RuntimeException(h + " is a router, its address can't change");

		ChangeAddressEvent e = (ChangeAddressEvent) Event.newEvent(ChangeAddressEvent.class);
		e.init();
		e.h = h;
		e.newAddress = newAddress;
		return e;
	}

	@Override
	public long getEstimatedRunTime() {
		return 1;
	}

	@Override
	public void run() throws Exception {

		// Check no one else
		if (Global.debug_extra_sanity) {
			Iterator<Host> i = Global.hosts.iterator();
			while (i.hasNext())
				if (i.next().getAddress() == newAddress)
					throw new RuntimeException("Trying to change to a existing address!!");
		}

		h.setAddress(newAddress);
	}

}