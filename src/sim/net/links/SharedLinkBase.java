/**
 *
 */
package sim.net.links;

import sim.events.Events;

public class SharedLinkBase {

	public int delay;
	public int bandwidth;
	long nextFreeTime;

	public SharedLinkBase(int bandwidth, int delay) {
		nextFreeTime = 0;

		if (bandwidth <= 0)
			throw new RuntimeException("Bandwidth can't be <= zero");

		if (delay < 0)
			throw new RuntimeException("Delay can't be < zero");

		this.bandwidth = bandwidth;
		this.delay = delay;
	}

	public long getNextFreeTime() {
		if (nextFreeTime < Events.getTime())
			nextFreeTime = Events.getTime();

		return nextFreeTime;
	}

	protected void setNextFreeTime(long nextFreeTime) {
		this.nextFreeTime = nextFreeTime;
	}
}