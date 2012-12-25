package sim.math;

import sim.main.Global;

/**
 * Generates a Normaly distributed number
 * @author Andrew Brampton
 *
 */
public class Normal extends Distribution {

	final double mean;
	final double stdev;

	public Normal(double mean, double stdev) {
		this.mean = mean;
		this.stdev = stdev;
	}

	@Override
	public double nextDouble() {
        return Global.rand.nextGaussian() * stdev + mean;
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getMean()
	 */
	@Override
	public double getMean() {
		return mean;
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getMin()
	 */
	@Override
	public double getMin() {
		return Double.NEGATIVE_INFINITY;
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
