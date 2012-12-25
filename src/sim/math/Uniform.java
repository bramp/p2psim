package sim.math;

public class Uniform extends Distribution {

	final double b;
	final double a;

	final double ba; // = b-a;

	public Uniform(double a, double b) {
		this.b = b;
		this.a = a;
		this.ba = b - a;
	}

	@Override
	public double nextDouble() {
		return ba * getRandom() + a;
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getMean()
	 */
	@Override
	public double getMean() {
		return (a + b) / 2;
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getMin()
	 */
	@Override
	public double getMin() {
		return a;
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getMax()
	 */
	@Override
	public double getMax() {
		return b;
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getCDF(double)
	 */
	@Override
	public double getCDF(double f) {
		throw new RuntimeException("Not implemented");
	}
}
