/*
 * Created on 05-Mar-2005
 */
package sim.events;

import sim.main.Global;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;

/**
 * Holds all the events to pass on
 * @author Andrew Brampton
 */
public abstract class Events {

	/* Current simulator time, in milliseconds */
	protected static long time = 0;

	/* The last estimated end time in the event queue */
	protected static long lastTime = 0;

	public static long INVALID_TIME = -1;

	protected static final EventsSortedSet events = new EventsSortedSet();
	//protected static final EventsSortedSet2 events = new EventsSortedSet2();

	protected static boolean bRunning = true;

	/**
	 * Add a event at time t
	 * @param t The time
	 * @param e The event
	 * @throws EventException
	 */
	public static void add(final Event e, final long t) {
		try {
			e.setTime( t );

			events.add(e);

			if (lastTime < e.getEstimatedFinishedTime())
				lastTime = e.getEstimatedFinishedTime();

		} catch (EventException ee) {
			ee.printStackTrace();
			Global.fatalExit();
			return;
		}

	}

	public static void clear() {
		events.clear();
		time = 0;
		lastTime = 0;
		bRunning = true;
		Event.totalEvents = 0;
	}

	/**
	 * Adds a event after the last event's estimated time
	 * @param e
	 */
	public static Event addAfterLastEvent(final Event e) {
		addAfterLastEvent(e, 0);
		return e;
	}

	/**
	 * Adds a event after event e has finished running
	 * @param e The first event
	 * @param newE The second event
	 */
	public static Event addAfterEvent(final Event e, final Event newE) {
		return addAfterEvent(e, newE, 0);
	}

	/**
	 * Adds a event after event e has finished running
	 * @param e The first event
	 * @param newE The second event
	 * @param delaytime
	 */
	public static Event addAfterEvent(final Event e, final Event newE, long delaytime) {
		e.addAfter(newE, delaytime);

		long endtime = e.getEstimatedFinishedTime() + newE.getEstimatedRunTime() + delaytime;

		if (lastTime < endtime)
			lastTime = endtime;

		return newE;
	}

	/**
	 * Adds a event after the last event's estimated time + inTime
	 * @param e
	 * @param inTime
	 */
	public static Event addAfterLastEvent(final Event e, final long inTime) {

		if (lastTime < Events.getTime())
			lastTime = Events.getTime();

		// set event for execution directly after last event in queue
		add(e, lastTime + inTime);

		return e;
	}

	/**
	 * Adds a event to occur in inTime ms from now
	 * @param inTime
	 * @param e
	 */
	public static Event addFromNow(final Event e, final long inTime) {
		if (inTime < 0)
			throw new RuntimeException(e + " Can't add in a negitive time (" + inTime + ")");

		add(e, Events.getTime() + inTime);
		return e;
	}

	public static Event addNow(final Event e) {
		addFromNow(e, 0);
		return e;
	}

	/**
	 * Gets the current time in milliseconds
	 */
	public static long getTime() {
		return time;
	}

	public static long getLastTime() {
		return lastTime;
	}

	public static boolean runNextEvent() throws Exception {

		if (!events.isEmpty()) {
			// Get the first event
			//Event e = events.first();

			// Remove it from the event table
			//events.remove(e);

			Event e = events.removeFirst();

			if (e.getTime() < time) {
				System.err.println(events.toString());
				throw new RuntimeException("We have gone back in time! CurrentTime: " + time + " NewTime:" + e.getTime() + " Bad Event " + e);
			}

			// Move the time forward
			time = e.getTime();

			if (Global.debug_log_events) {
				Trace.println(LogLevel.DEBUG, e.toString());
			}

			// Reset the event's time, so it can later be rescheduled (maybe)
			e.resetTime();

			//Now run the event
			e.run();

			// If it hasn't been rescheduled
			if (e.getTime() == INVALID_TIME) {

				// Schedules all events waiting for this to finish to run
				e.runAfterEvents();

				//, then free it
				e.free();
			}
		}

		return bRunning && !events.isEmpty() && events.getRequiredCount() > 0;
	}

	public static int getRequiredCount() {
		return events.getRequiredCount();
	}

	public static int getNonRequiredCount() {
		return events.getNonRequiredCount();
	}

	/**
	 * Stops the execution of the Events
	 *
	 */
	public static void stop() {
		bRunning = false;
	}

	/**
	 *  debug method only
	 */
	public static void setTime(long t) {
		time = t;
	}
}
