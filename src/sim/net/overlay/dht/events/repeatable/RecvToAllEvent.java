/*
 * Created on 13-May-2005
 */
package sim.net.overlay.dht.events.repeatable;

import java.util.Iterator;

import sim.events.Event;
import sim.events.Events;
import sim.events.RepeatableDistributionEvent;
import sim.main.Global;
import sim.math.Constant;
import sim.math.Distribution;
import sim.net.Host;
import sim.net.overlay.dht.DHTInterface;
import sim.net.overlay.dht.TestMessage;
import sim.net.overlay.dht.events.RecvEvent;


public class RecvToAllEvent extends RepeatableDistributionEvent {

	Iterator<Host> i;
	Iterator<Host> j;
	Host h;
	static final int DEFAULT_DELAY = 10000; // The time between each event

	public static RecvToAllEvent newEvent() {
		return newEvent( new Constant(DEFAULT_DELAY) );
	}

	public static RecvToAllEvent newEvent(Distribution d) {
		return newEvent(d, Global.hosts.getType(DHTInterface.class).iterator(),
			 Global.hosts.getType(DHTInterface.class).iterator(),
			 null);
	}

	protected static RecvToAllEvent newEvent(Distribution d, Iterator<Host> i, Iterator<Host> j, Host h) {
		RecvToAllEvent e = (RecvToAllEvent) Event.newEvent(RecvToAllEvent.class);
		e.init(d, (int)Math.pow(Global.hosts.getType(DHTInterface.class).size(),2));
		e.i = i;
		e.j = j;
		e.h = h;
		return e;
	}

	@Override
	public void run() throws Exception {

		while (h == null) {
			if (j.hasNext())
				h = j.next();
			else
				return;
		}

		if (i.hasNext()) {

			DHTInterface p0 = (DHTInterface)i.next();
			DHTInterface p1 = (DHTInterface)h;
			if (p0 != p1) {
				Event e = RecvEvent.newEvent(p1, new TestMessage(h.getAddress(), p1.getID(), p0.getID()));
				Events.addNow(e);
			}
		} else {
			if (j.hasNext()) {
				h = j.next();
				i = Global.hosts.getType(DHTInterface.class).iterator();
			} else {
				// This is the only case when we want to finish
				return;
			}
		}

		reschedule();
	}

}
