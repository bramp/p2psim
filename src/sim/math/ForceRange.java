package sim.math;

/**
 * This forces a Distribution to stay within the specified range
 * @author Andrew Brampton
 *
 */
public class ForceRange extends Distribution {

	Distribution d;
	double min, max;

	/**
	 * min <= nextDouble() < max
	 * @param d
	 * @param min
	 * @param max
	 */
	public ForceRange(Distribution d, double min, double max) {
		this.d = d;
		this.min = min;
		this.max = max;
	}

	public ForceRange(Distribution d, double min) {
		this.d = d;
		this.min = min;
		this.max = Double.POSITIVE_INFINITY;
	}

	@Override
	public double getMax() {
		return max;
	}

	@Override
	public double getMean() {
		throw new RuntimeException("I don't know my own mean");
	}

	@Override
	public double getMin() {
		return min;
	}

	@Override
	public double nextDouble() {
		double ret;
		int count = 10000;

		do {
			ret = d.nextDouble();

			if ( --count <= 0 )
				throw new RuntimeException("Trying to force a random number between some values, just won't work!");

		} while (ret <= min || ret > max);

		return ret;
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getCDF(double)
	 */
	@Override
	public double getCDF(double f) {
		throw new RuntimeException("Not implemented");
	}

}
