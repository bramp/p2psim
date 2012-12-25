/*
 * Created on 13-May-2005
 */
package sim.net.overlay.dht.stealth.events;

import sim.events.Event;
import sim.events.Events;
import sim.main.Global;
import sim.math.Distribution;
import sim.net.Host;
import sim.net.HostSet;
import sim.net.events.FailEvent;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.events.JoinEvent;
import sim.net.overlay.dht.stealth.ServicePeer;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

public class UnfailAndJoinAndPassEvent extends  sim.net.overlay.dht.events.repeatable.UnfailAndJoinAndPassEvent {

	public static UnfailAndJoinAndPassEvent newEvent(Distribution d, HostSet hosts, int count) {
		UnfailAndJoinAndPassEvent e = (UnfailAndJoinAndPassEvent) Event.newEvent(UnfailAndJoinAndPassEvent.class);
		e.init(d, count);
		e.hosts = hosts;
		return e;
	}

	public static UnfailAndJoinAndPassEvent newEvent(Distribution d, int count) {
		return newEvent(d, Global.hosts.getType(DHTInterface.class), count);
	}

	@Override
	public void run() throws Exception {

		HostSet joined = getJoinedPeers(Global.hosts.getType(ServicePeer.class).iterator());
		HostSet notjoined = getFailedPeers();

		if (!joined.isEmpty() && !notjoined.isEmpty()) {
			Host h0 = joined.getRandom();
			Host h1 = notjoined.getRandom();
			DHTInterface p1 = (DHTInterface) h1;

			Events.addNow(FailEvent.newEvent(h1, false));
			Events.addNow(JoinEvent.newEvent(p1, h0.getAddress()));

		} else {

			// Ok so there is no nodes to join to, so lets try joining ourself!
			//notjoined = notjoined.getType(ServicePeer.class);

			if (joined.isEmpty() && ! notjoined.getType(ServicePeer.class).isEmpty() ){
				Host h1 = notjoined.getRandom();
				DHTInterface p1 = (DHTInterface) h1;

				// Special join
				Events.addNow(FailEvent.newEvent(h1, false));
				Events.addNow(JoinEvent.newEvent(p1, Host.INVALID_ADDRESS));
			} else {
				// if there are no unfailed peers left, just stop
				Trace.println(LogLevel.WARN, "Could not cause requested number of unfailures + joins! (" + joined.size() + " joined and " + notjoined.size() + " failed)");
			}
		}

		reschedule();
	}

}
