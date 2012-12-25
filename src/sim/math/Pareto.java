package sim.math;

public class Pareto extends Distribution {

	final double alpha;
	final double alpha1; // = 1 / alpha
	final double min;

	public Pareto(double alpha, double min) {
		this.alpha = alpha;
		this.alpha1 = 1 / alpha;
		this.min = min;
	}

	@Override
	public double nextDouble() {
		return min/Math.pow(getRandom(), alpha1);
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getMean()
	 */
	@Override
	public double getMean() {
		return Math.abs((min * alpha) / (1 - alpha));

	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getMin()
	 */
	@Override
	public double getMin() {
		return min;
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
