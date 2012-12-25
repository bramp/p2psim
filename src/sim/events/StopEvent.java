package sim.events;

import sim.events.Event;

/**
	Stops the simulator
*/
public class StopEvent extends Event {

	public static StopEvent newEvent() {
		StopEvent e = (StopEvent) Event.newEvent(StopEvent.class);
		return e;
	}

	@Override
	public void run() throws Exception {
		Events.stop();
	}

	@Override
	public long getEstimatedRunTime() {
		return 0;
	}
}
