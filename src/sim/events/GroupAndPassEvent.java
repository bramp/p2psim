package sim.events;

import java.util.Iterator;
import java.util.List;

import sim.math.Distribution;

/**
 * Used to run many events at the same time
 * @author Andrew Brampton
 *
 */
public class GroupAndPassEvent extends RepeatableDistributionEvent {

	List<Event> events;

	public static GroupAndPassEvent newEvent(Distribution d, int count, List<Event> events) {
		GroupAndPassEvent e = (GroupAndPassEvent) Event.newEvent(GroupAndPassEvent.class);
		e.init(d, count);
		e.events = events;

		return e;
	}

	@Override
	public void run() throws Exception {

		Iterator<Event> i = events.iterator();
		while (i.hasNext()) {
			Event e = i.next();
			e.run();
		}

		reschedule();
	}

}
