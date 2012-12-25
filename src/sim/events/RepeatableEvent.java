package sim.events;

public abstract class RepeatableEvent extends Event {

	public abstract long getEstimatedRunTime();

	protected void reschedule(final long fromNow) {
		Events.addFromNow(this, fromNow);
	}

	protected void reschedule() {
		reschedule(0);
	}
}
