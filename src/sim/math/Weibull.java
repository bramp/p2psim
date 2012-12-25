package sim.math;

public class Weibull extends Distribution {

	final double lambda;
	final double k;
	final double k1; // 1 over k

	public Weibull(double lambda, double k) {
		if (lambda <= 0)
			throw new RuntimeException("lambda must be > 0");

		if (k <= 0)
			throw new RuntimeException("k must be > 0");

		this.lambda = lambda;
		this.k = k;

		this.k1 = 1 / k;
	}

	@Override
	public double nextDouble() {
		return lambda * Math.pow(  - Math.log( getRandom() ) , k1 );
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getMean()
	 */
	@Override
	public double getMean() {
		 return lambda * Common.gamma(1.0 / k + 1.0);
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
	/*
	@Override
	public double getCDF(double f) {
		assert ( f >= 0.0 );
		assert ( f <= 1.0 );

		if ( f == 1.0 )
			return Double.POSITIVE_INFINITY;

		return lambda * Math.pow( - Math.log(1-f) , k1);
	}
	*/

}
