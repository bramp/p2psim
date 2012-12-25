/*
 * Created on 05-Mar-2005
 */
package sim.net.events;

import sim.events.Event;
import sim.net.Host;
import sim.net.Packet;
import sim.net.links.Link;

/**
 * A Packet is sent event
 * @author Andrew Brampton
 */
public class ReceiveEvent extends Event {
	public Host host;
	public Packet p;
	public Link l;

	public static ReceiveEvent newEvent(Host host, Link l, Packet p) {
		ReceiveEvent e = (ReceiveEvent) Event.newEvent(ReceiveEvent.class);
		e.host = host;
		e.l = l;
		e.p = p;

		e.required = p.critical;

		return e;
	}

	@Override
	public void run() {
		host.recv(l, p);
	}

	@Override
	public long getEstimatedRunTime() {
		return 1;
	}
}