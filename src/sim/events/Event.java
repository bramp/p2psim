/*
 * Created on 05-Mar-2005
 */
package sim.events;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import sim.collections.ClassLists;
import sim.collections.LinkListItem;
import sim.collections.Pair;
import sim.main.Global;
import sim.main.Helper;

import static sim.events.Events.INVALID_TIME;

public abstract class Event extends LinkListItem implements Comparable<Event> {

	/**
	 * The time at which this event runs
	 */
	protected long time;

	/* The number of events before this event
	 * Used so each event is different and in order of time (then event #)
	 */
	public int number;

	/**
	 * List of events that will be scheduled after this one
	 */
	protected List<Pair<Long, Event>> nextEvents = null;

	/* Number of events ever! */
	protected static int totalEvents = 0;

	/**
	 * Is this event required to be run, or can the simulator end early?
	 */
	protected boolean required;

	protected static ClassLists<Event> freeEvents = new ClassLists<Event>();
	protected static Event newEvent(final Class<? extends Event> c) {

		List<? extends Event> events = freeEvents.getType(c);

		// If we have no Events of this type, try and make a new one
		if (events.isEmpty()) {
			try {
				return c.newInstance();
			} catch (Exception ee) {

				System.err.println("Unable to create " + c);
				ee.printStackTrace();
				Global.fatalExit();
			}

		}

		// Otherwise return the first old one
		// Get a free event (we get the last one, to minimise reshuffling)
		Event e = events.get(events.size() - 1);
		freeEvents.remove(e); // This line makes sure its removed from freeEvents

		// Reset this event and return
		e.init();
		return e;

	}

	public void free() {
		freeEvents.add(this);
	}

	protected Event() {
		init();
	}

	protected void init() {
		init(true);
	}

	protected void init(final boolean required) {
		number = totalEvents;
		totalEvents++;
		this.required = required;

		if (nextEvents != null)
			nextEvents.clear();

		resetTime();
	}

	public long getEstimatedFinishedTime() {
		return time + getEstimatedRunTime();
	}

	public abstract long getEstimatedRunTime();

	/**
	 * This method resets the time for this event to a default value
	 * Normally time can't be changed, but after a event is removed from the
	 * Events queue it can be (incase someone wants to readd the event later)
	 */
	void resetTime() {
		time = INVALID_TIME;
	}

	public void setTime(final long time) throws EventException {

		if (this.time != INVALID_TIME)
			throw new EventException("Event time is being changed after its been set (time:" + this.time + " newtime:" + time + ")");

		if (time < Events.getTime())
			throw new EventException("Event time is in the past EventTime:" + time + " < CurrentTime:" + Events.getTime());

		this.time = time;
	}

	public long getTime() {
		return time;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final Event o) {
		long time = o.time;

		if (this.time == time)
			return (number - o.number);
		else if (this.time > time)
			return 1;
		else
			return -1;
	}

	/* Method to run when event occurs */
	public abstract void run() throws Exception;

	@Override
	public String toString() {
		return Helper.getShortName(this) + "(" + number + ") " + getTime() + (required ? " Required" : "");
	}

	/**
	 * @return
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Add this event to run once this event has finished
	 * @param e Event to add
	 * @param delayTime The time to wait after e before running
	 */
	public void addAfter(Event e, long delayTime) {
		if (nextEvents == null)
			nextEvents = new ArrayList<Pair<Long, Event>>();

		nextEvents.add(new Pair<Long, Event>(delayTime, e));
	}

	/**
	 * Runs all the events scheduled to be run once this has finished
	 *
	 */
	public void runAfterEvents() {
		if (nextEvents == null)
			return;

		if (nextEvents.isEmpty())
			return;

		Iterator<Pair<Long, Event>> i = nextEvents.iterator();
		while (i.hasNext()) {
			Pair<Long, Event> p = i.next();
			Events.addFromNow(p.right, p.left);
		}

		nextEvents.clear();
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
}