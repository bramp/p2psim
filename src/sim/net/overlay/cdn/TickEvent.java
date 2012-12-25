package sim.net.overlay.cdn;

import sim.events.Event;
import sim.events.RepeatableDistributionEvent;
import sim.math.Distribution;

public class TickEvent<T> extends RepeatableDistributionEvent {

	Tickable<T> node;
	T data;

	@SuppressWarnings("unchecked")
	public static <T> TickEvent<T> newEvent(Distribution d, int count, Tickable<T> node, T data) {
		TickEvent<T> e = (TickEvent<T>) Event.newEvent(TickEvent.class);
		e.init(d, count);
		e.node = node;
		e.data = data;
		return e;
	}

	@Override
	public void run() throws Exception {
		// Make the node process this ClientRequest tick, and if it returns true
		//reschedule for later use
		if (node.tick(data))
			reschedule();
	}
}
