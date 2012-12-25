package sim.stats;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import sim.main.Global;
import sim.stats.trace.LogLevel;
import sim.stats.trace.Trace;
import sim.stats.utility.Average;
import sim.stats.utility.DoubleAverage;
import sim.stats.utility.DoubleValue;
import sim.stats.utility.IntValues;
import sim.stats.utility.LongValue;
import sim.stats.utility.LongValues;
import sim.stats.utility.Value;

/**
 * @author macquire
 *
 */
public class StatsObject {
	public static final String SEPARATOR = "_";
	public static final String GLOBAL = "Global";

	public static final String AVERAGE 	= SEPARATOR + "Avg";
	public static final String COUNT   	= SEPARATOR + "Count";
	public static final String MAX     	= SEPARATOR + "Max";
	public static final String MIN     	= SEPARATOR + "Min";
	public static final String VALUES  	= SEPARATOR + "Values";
	public static final String VARIANCE = SEPARATOR + "Var";

	private static StatsObject instance = null;
	public static StatsObject getInstance() {
		if (instance == null) {
			instance = new StatsObject();
		}
		return instance;
	}

	// typedef ;)
	public class StatsPeriod extends TreeMap<String,Object> {}

	private boolean enabled = true; // enabled by default

	private int total_periods = 0;
	private StatsPeriod global_period;
	private StatsPeriod current_period;

	private Map<String, StatsPeriod> periods;
	protected Map<String, Long> timers = new TreeMap<String, Long>();

	// only one global stats object may exist
	private StatsObject() {
		clear();
	}

	public void clear() {
		// lose all old info
		periods = new TreeMap<String, StatsPeriod>();

		// Create the global period
		global_period = newStatsPeriod(GLOBAL);
		total_periods = 0;

		// Setup the current period
		if ( Global.debug_stats_periods ) {
			newStatsPeriod();
		}
	}

	/**
	 * @param value the required recording status of the stats object
	 */
	public void enable(boolean value) {
		enabled = value;
	}

	public Map<String, StatsPeriod > getData() {
		return periods;
	}

	/**
	 * @return the recording status of the stats object
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Returns the value for this global stat
	 * @param stat
	 * @return
	 */
	public double getValue(String stat) {
		Value v = (Value)global_period.get(stat);
		if (v == null)
			return 0.0;
		return v.getDoubleValue();
	}

	public void log(String string, long value) {
		logAverage(string, value);
		logMin(string, value);
		logMax(string, value);
	}

	public void logAverage(String stat, double value) {
		if(isEnabled()) {
			logAverage(global_period, stat, value);
			if (Global.debug_stats_periods)
				logAverage(current_period, stat, value);
		}
	}

	protected void logAverage(StatsPeriod period, String stat, double value) {
		final String stat_avg = stat + AVERAGE;
		DoubleAverage average = (DoubleAverage)period.get(stat_avg);

		if (average != null) {
			average.addValue(value);
			((Value)period.get(stat + VARIANCE)).setValue(average.getVariance());
		}
		else {
			// create new statistic from first value given
			average = new DoubleAverage(value);
			put(period, stat_avg, average);
			put(period, stat + VARIANCE, new DoubleValue(average.getVariance()));
		}
	}

	public void logAverage(String stat, long value) {
		if(isEnabled()) {
			//stat += AVERAGE;
			logAverage(global_period, stat, value);
			if (Global.debug_stats_periods)
				logAverage(current_period, stat, value);
		}
	}

	protected void logAverage(StatsPeriod period, String stat, long value) {
		final String stat_avg = stat + AVERAGE;
		Average average = (Average)period.get(stat_avg);

		if (average != null) {
			average.addValue(value);
			((Value)period.get(stat + VARIANCE)).setValue(average.getVariance());
		}
		else {
			// create new statistic from first value given
			average = new Average(value);
			put(period, stat_avg, average);
			put(period, stat + VARIANCE, new DoubleValue(average.getVariance()));
		}
	}

	/**
	 * Minus one from a count value
	 * @param stat
	 */
	public void logMinusCount(String stat) {
		if (isEnabled()) {
			stat += COUNT;
			logMinusCount(global_period, stat);
			if (Global.debug_stats_periods)
				logMinusCount(current_period, stat);
		}
	}

	protected void logMinusCount(StatsPeriod period, String stat) {
		Value count = (Value)period.get(stat);
		// check to see if statistic already exists
		if (count != null) {
			count.decrement();
		}
		// if it doesn't, create it with a value of 1
		else {
			put(period, stat, new LongValue(-1));
		}
	}

	/**
	 * Adds one to a count value
	 * @param stat
	 */
	public void logCount(String stat) {
		if (isEnabled()) {
			stat += COUNT;
			logCount(global_period, stat);
			if (Global.debug_stats_periods)
				logCount(current_period, stat);
		}
	}

	protected void logCount(StatsPeriod period, String stat) {
		Value count = (Value)period.get(stat);
		// check to see if statistic already exists
		if (count != null) {
			count.increment();
		}
		// if it doesn't, create it with a value of 1
		else {
			put(period, stat, new LongValue(1));
		}
	}

	public void logMax(String stat, long value) {
		if (isEnabled()) {
			stat += MAX;
			logMax(global_period, stat, value);
			if (Global.debug_stats_periods)
				logMax(current_period, stat, value);
		}
	}

	protected void logMax(StatsPeriod period, String stat, long value) {
		Value max = (Value)period.get(stat);

		if (max != null) {
			if (value > max.getLongValue()) {
				max.setValue(value);
			}
		}
		else {
			put(period, stat, new LongValue(value));
		}

	}

	public void logMin(String stat, long value) {
		if (isEnabled()) {
			stat += MIN;
			logMin(global_period, stat, value);
			if (Global.debug_stats_periods)
				logMin(current_period, stat, value);
		}
	}

	protected void logMin(StatsPeriod period, String stat, long value) {
		Value min = (Value)period.get(stat);

		if (min != null) {
			if (value < min.getLongValue()) {
				min.setValue(value);
			}
		}
		else {
			put(period, stat, new LongValue(value));
		}
	}

	public void logRunningTotal(String stat, long value) {
		if (isEnabled() && value != 0) {
			logRunningTotal(global_period, stat, value);
			if (Global.debug_stats_periods)
				logRunningTotal(current_period, stat, value);
		}
	}

	protected void logRunningTotal(StatsPeriod period, String stat, long value) {
		Value v = (Value)period.get(stat);
		if (v == null)
			put(period, stat, new LongValue(value));
		else
			v.increment(value);
	}

	public void logRunningTotal(String stat, double value) {
		if (isEnabled() && value != 0) {
			logRunningTotal(global_period, stat, value);
			if (Global.debug_stats_periods)
				logRunningTotal(current_period, stat, value);
		}
	}

	protected void logRunningTotal(StatsPeriod period, String stat, double value) {
		Value v = (Value) period.get(stat);
		if (v == null)
			put(period, stat, new DoubleValue(value));
		else
			v.increment(value);
	}

	public void logValue(String stat, long value) {
		if (isEnabled()) {
			logValue(global_period, stat, value);
			logValue(current_period, stat, value);
		}
	}

	protected void logValue(StatsPeriod period, String stat, long value) {
		put(period, stat, new LongValue(value));
	}

	public void logValue(String stat, double value) {
		if (isEnabled()) {
			logValue(global_period, stat, value);
			if (Global.debug_stats_periods)
				logValue(current_period, stat, value);
		}
	}

	protected void logValue(StatsPeriod period, String stat, double value) {
		put(period, stat, new DoubleValue(value));
	}

	public void logValues(String stat, int value) {
		if (isEnabled()) {
			stat += VALUES;
			logValues(global_period, stat, value);
			if (Global.debug_stats_periods)
				logValues(current_period, stat, value);
		}
	}

	public void logValues(String stat, long value) {
		if (isEnabled()) {
			stat += VALUES;
			logValues(global_period, stat, value);
			if (Global.debug_stats_periods)
				logValues(current_period, stat, value);
		}
	}

	protected void logValues(StatsPeriod period, String stat, int value) {
		IntValues values = (IntValues)period.get(stat);

		if (values != null) {
			values.addValue(value);
		}
		else {
			// create new statistic from first value given
			put(period, stat, new IntValues(value));
		}
	}

	protected void logValues(StatsPeriod period, String stat, long value) {
		LongValues values = (LongValues)period.get(stat);

		if (values != null) {
			values.addValue(value);
		}
		else {
			// create new statistic from first value given
			put(period, stat, new LongValues(value));
		}
	}

	public void newStatsPeriod() {
		newStatsPeriod(Integer.toString(total_periods));
	}

	public StatsPeriod newStatsPeriod(String name) {
		total_periods++;

		if ( !Global.debug_stats_periods && total_periods > 1 )
			throw new RuntimeException("Only one stats period supported, when stats periods are turned off!");

		StatsPeriod new_period = periods.get(name);

		if (new_period == null) {

			// A bit of a hack, but copy any Sim_ parameters across to the new period
			new_period = new StatsPeriod();

			if ( current_period != null ) {
				for ( Entry<String, Object> pair : current_period.entrySet() ) {
					if ( pair.getKey().startsWith( "Sim" + SEPARATOR ) )
						new_period.put( pair.getKey(), pair.getValue() );
				}
			}

			periods.put(name, new_period);
		}

		current_period = new_period;

		// Log this
		Trace.println(LogLevel.INFO, "newStatsPeriod " + name);

		return current_period;
	}

	public void renameCurrentStatsPeriod(String name) {

		assert current_period != null;
		assert ! periods.isEmpty();

		Iterator<Entry<String, StatsPeriod>> i = periods.entrySet().iterator();

		String currentName = null;

		// Find the current period based on our name
		while (i.hasNext() && currentName == null) {
			Entry<String, StatsPeriod> e = i.next();
			if (e.getValue() == current_period)
				currentName = (String)e.getKey();
		}

		// Remove the old name
		periods.remove(currentName);

		// Insert the new one
		periods.put(name, current_period);
	}

	protected void put(StatsPeriod period, String stat, Object value) {
		period.put(stat, value);
	}

	/**
	 * Timing method for profiling
	 * @param string
	 */
	public void startTimer(String string) {
		timers.put(string, System.nanoTime() );
	}

	public void stopTimer(String string) {
		long time =  System.nanoTime() - timers.get(string);
		log(string, time);
	}

	@Override
	public String toString() {
		// return alphabetically ordered contents of 'data' hashtable
		String s = "";
		Iterator<String> i = new TreeSet<String>(periods.keySet()).iterator();

		while(i.hasNext()) {

			String period = i.next();
			Map<String,Object> h = periods.get(period);
			Iterator<String> ii = new TreeSet<String>(h.keySet()).iterator();

			while(ii.hasNext()) {
				String key = ii.next();

				// Test if there is only 1 period, therefore only show global stats
				if (total_periods <= 1) {
					if (!period.equals(GLOBAL))
						continue;
				}

				s += period + SEPARATOR + key + " = " + h.get(key) + "\n";
			}
		}
		return s;
	}

	/**
	 * A quick benchmark method
	 * @param args
	 */
	public static void main(String[] args) {
		final int MAX = 1000000;

		StatsObject stats = StatsObject.getInstance();
		long start;

		for (int loop = 0; loop < 10; loop ++) {

			start = System.currentTimeMillis();
			for (int i = 0; i < MAX; i++) {
				stats.logRunningTotal("Test", 1);
			}

			System.out.println("Integer\t" + (System.currentTimeMillis() - start) + "\t" + stats.getValue("Test"));
			stats.clear();

			start = System.currentTimeMillis();
			for (int i = 0; i < MAX; i++) {
				stats.logRunningTotal("Test", 1l);
			}

			System.out.println("Long\t" + (System.currentTimeMillis() - start) + "\t" + stats.getValue("Test"));
			stats.clear();

			start = System.currentTimeMillis();
			for (int i = 0; i < MAX; i++) {
				stats.logRunningTotal("Test", 1.0d);
			}

			System.out.println("Double\t" + (System.currentTimeMillis() - start) + "\t" + stats.getValue("Test"));
			stats.clear();
		}
	}
}
