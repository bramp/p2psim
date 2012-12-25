/*
 * Created on 13-May-2005
 */
package sim.stats;

import sim.events.Event;
import sim.main.Global;

public class StatsNewPeriodEvent extends Event {

	public String name = null;

	public static StatsNewPeriodEvent newEvent() {
		return newEvent((String)null);
	}

	public static StatsNewPeriodEvent newEvent(String name) {
		StatsNewPeriodEvent e = (StatsNewPeriodEvent) Event.newEvent(StatsNewPeriodEvent.class);
		e.name = name;
		return e;
	}

	@Override
	public void run() throws Exception {
		if (name != null)
			Global.stats.newStatsPeriod(name);
		else
			Global.stats.newStatsPeriod();
	}

	@Override
	public long getEstimatedRunTime() {
		return 0;
	}

}
