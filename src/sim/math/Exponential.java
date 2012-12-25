package sim.math;

public class Exponential extends Distribution {

	final double m;

	public Exponential(double mean) {
		this.m = mean;
	}

	@Override
	public double nextDouble() {
		return -1 * Math.log( 1 - getRandom() ) * m;
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getMean()
	 */
	@Override
	public double getMean() {
		return m;
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getMin()
	 */
	@Override
	public double getMin() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getMax()
	 */
	@Override
	public double getMax() {
		return Double.POSITIVE_INFINITY;
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getCDF(double)
	 */
	@Override
	public double getCDF(double f) {
		throw new RuntimeException("Not implemented");
	}

}
