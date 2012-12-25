package sim.math;

public class ParetoII extends Distribution {

	final double alpha;
	final double alpha1; // = 1 / alpha
	final double m;
	final double scale;

	public ParetoII(double alpha, double mean) {
		this.alpha = alpha;
		this.alpha1 = 1 / alpha;
		this.m = mean;
		scale = (alpha-1)*m;
	}

	@Override
	public double nextDouble() {
		return scale*(1/Math.pow(getRandom(), alpha1)-1);
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
		throw new RuntimeException("ParetoII doesn't know his own min :(");
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getMax()
	 */
	@Override
	public double getMax() {
		throw new RuntimeException("ParetoII doesn't know his own max :(");
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getCDF(double)
	 */
	@Override
	public double getCDF(double f) {
		throw new RuntimeException("Not implemented");
	}
}
