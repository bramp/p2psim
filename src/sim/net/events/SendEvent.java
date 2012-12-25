/*
 * Created on 07-Mar-2005
 */
package sim.net.events;

import sim.events.Event;
import sim.net.Host;
import sim.net.Packet;
import sim.net.RoutingException;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

/**
 * @author Andrew Brampton
 */
public class SendEvent extends Event {

	Host to;
	Host from;
	Packet packet;

	public static SendEvent newEvent(Host from, Packet packet) {
		SendEvent e = (SendEvent) Event.newEvent(SendEvent.class);
		e.to = from;
		e.packet = packet;
		e.required = packet.critical;
		return e;
	}

	/* (non-Javadoc)
	 * @see net.bramp.p2psim.events.Event#run()
	 */
	@Override
	public void run() {
		try {
			to.send(packet);
		} catch (RoutingException e) {
			Trace.println(LogLevel.WARN, this + ": WARNING " + e + " (" + packet + ")");
		}
	}

	@Override
	public long getEstimatedRunTime() {
		return 1;
	}

}
