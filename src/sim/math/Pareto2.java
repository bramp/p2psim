package sim.math;

public class Pareto2 extends Distribution {

	final double alpha;
	final double alpha1; // = 1 / alpha
	final double m;
	final double scale;

	public Pareto2(double alpha, double mean) {
		this.alpha = alpha;
		this.alpha1 = 1 / alpha;
		this.m = mean;
		scale = (alpha-1)*m/alpha;
	}

	@Override
	public double nextDouble() {
		return scale*(1/Math.pow(getRandom(), alpha1));
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
		return scale;
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
