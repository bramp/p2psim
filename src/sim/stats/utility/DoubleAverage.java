package sim.stats.utility;

public class DoubleAverage extends Average {
	protected double total = 0;
	protected double totalsquare = 0;

	public DoubleAverage(double firstValue) {
		addValue(firstValue);
	}

	public void addValue(double value) {
		count++;
		total += value;
		totalsquare += value * value;
	}

	@Override
	protected void recalc() {
		average = total / count;
		variance = (totalsquare / count) - (average * average);
	}
}
