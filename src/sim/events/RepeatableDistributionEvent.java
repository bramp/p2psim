package sim.events;

import sim.math.Distribution;

public abstract class RepeatableDistributionEvent extends Event {

	protected int countleft = 0;
	protected Distribution d = null;

	protected void init(Distribution d, int count) {
		init(required);

		assert d != null;
		assert count > 0;

		this.d = d;
		this.countleft = count;
	}

	protected static RepeatableDistributionEvent newEvent(Distribution d, int count) {
		return newEvent(d, count, true);
	}

	protected static RepeatableDistributionEvent newEvent(Distribution d, int count, boolean required) {
		RepeatableDistributionEvent e = (RepeatableDistributionEvent) Event.newEvent(RepeatableDistributionEvent.class);
		e.init(d, count);
		return e;
	}

	@Override
	public long getEstimatedRunTime() {
		// We estimate this message will take mean * countleft to send
		return (long) ( d.getMean() * countleft );
	}

	protected void reschedule(final long fromNow) {
		countleft--;

		if (countleft > 0) {
			Events.addFromNow(this, fromNow);
		}
	}

	protected void reschedule() {
		reschedule(d.nextLong());
	}
}
