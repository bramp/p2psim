package sim.math;

/**
 * Generates a LogNormaly distributed number
 * @author Andrew Brampton
 *
 */
public class LogNormal extends Distribution {

	final Normal norm;

	public LogNormal(double mu, double sigma) {
		norm = new Normal(mu, sigma);
	}

	@Override
	public double nextDouble() {
        return Math.exp( norm.nextDouble() );
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getMean()
	 */
	@Override
	public double getMean() {
		return Math.exp( norm.mean + (norm.stdev * norm.stdev / 2) );
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


}
