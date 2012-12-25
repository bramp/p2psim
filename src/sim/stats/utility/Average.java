package sim.stats.utility;

import sim.main.Global;

public class Average implements Value {
	protected long count = 0;
	protected long total = 0;
	protected long totalsquare = 0;

	protected double average = 0;
	protected double variance = 0;

	protected Average() {}

	public Average(long firstValue) {
		addValue(firstValue);
	}

	public void addValue(long value) {
		count++;
		total += value;
		totalsquare += value * value;
	}

	protected void recalc() {
		average = (double)total/(double)count;
		variance = ((double)totalsquare/(double)(count))-(average * average);
		//sd = Math.sqrt(variance);
	}

	public double getAverage() {
		recalc();
		return average;
	}

	public double getVariance() {
		recalc();
		return variance;
	}

	@Override
	public String toString() {
		return Global.decimal.format(getAverage());
	}

	/* (non-Javadoc)
	 * @see sim.stats.utility.Value#getIntValue()
	 */
	public int getIntValue() {
		return (int) getAverage();
	}

	/* (non-Javadoc)
	 * @see sim.stats.utility.Value#getLongValue()
	 */
	public long getLongValue() {
		return (long) getAverage();
	}

	public double getDoubleValue() {
		return getAverage();
	}

	public void increment() { throw new RuntimeException("Can't increment average"); }
	public void increment(int amount) { throw new RuntimeException("Can't increment average"); }
	public void increment(long amount) { throw new RuntimeException("Can't increment average"); }
	public void increment(double amount) { throw new RuntimeException("Can't increment average"); }
	public void decrement() { throw new RuntimeException("Can't decrement average"); }

	// TODO Maybe considering allowing these?
	public void setValue(int value) { throw new RuntimeException("Can't set average"); }
	public void setValue(long value) { throw new RuntimeException("Can't set average"); }
	public void setValue(double value) { throw new RuntimeException("Can't set average"); }

}
