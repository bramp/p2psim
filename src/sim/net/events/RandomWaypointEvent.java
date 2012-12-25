/**
 *
 */
package sim.net.events;

import sim.events.Event;
import sim.events.Events;
import sim.main.Global;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.Host;
import sim.net.links.Link;
import sim.net.overlay.dht.events.repeatable.RepeatableHelperEvent;
import sim.net.router.MobileRouter;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

/**
 * Really simple model
 * Random moves from one mobile router to another
 * With a wait time at each one
 * @author Andrew Brampton
 */
public class RandomWaypointEvent extends RepeatableHelperEvent {

	Host h;
	boolean changeIP = false;
	long handovertime = 150; //150ms

	public static RandomWaypointEvent newEvent(Host h, int points, Distribution waitDist) {
		// default is not to change IP
		return newEvent(h, points, waitDist, false);
	}

	public static RandomWaypointEvent newEvent(Host h, int points, Distribution waitDist, boolean changeIP) {
		RandomWaypointEvent e = (RandomWaypointEvent) Event.newEvent(RandomWaypointEvent.class);
		e.init(waitDist, points);
		e.h = h;
		e.required = false;
		e.changeIP = changeIP;
		return e;
	}

	public static RandomWaypointEvent newEvent(Host h, int points) {
		// Wait 10seconds at each point
		return newEvent(h, points, new Constant(10000));
	}

	/* (non-Javadoc)
	 * @see sim.events.Event#run()
	 */
	@Override
	public void run() throws Exception {

		// First check if we are connected to something
		if (h.getLinks().isEmpty()) {
			Trace.println(LogLevel.WARN, h + ": WARNING trying to move a host not linked to anything");
			return;
		}

		Link l = h.getLinks().get(0);

		// Now move this host
		Event e = RemoveLinkEvent.newEvent(l);
		e.run();

		// Pick a new router
		MobileRouter r = (MobileRouter) Global.hosts.getType(MobileRouter.class).getRandom();

		if (changeIP) {
			e = ChangeAddressEvent.newEvent(h, Global.lastAddress);
			Global.lastAddress++;
			e.run();
		}

		// Now reconnect him
		e = AddLinkEvent.newEvent(r, h, l);

		if (handovertime > 0) {
			Events.addFromNow(e, handovertime);
		} else {
			// If we have a 0 handover time, cheat and don't bother using the Events queue
			e.run();
		}


		reschedule();
	}

}