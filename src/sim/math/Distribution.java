package sim.math;

import java.util.Arrays;

import sim.main.Global;

/**
 *
 * @author Andrew Brampton
 *
 */
public abstract class Distribution {

	/**
	 * Generates the next double in this Distribution
	 * @return
	 */
	public abstract double nextDouble();

	/**
	 * Returns the next Integer in this distribution. If the generated number
	 * is outside of the range of a int, then Integer.MIN_VALUE or Integer.MAX_VALUE is returned
	 * @return
	 */
	public int nextInt() {
		return (int)nextDouble();
	}

	public long nextLong() {
		return (long) nextDouble();
	}

	/**
	 * Returns a number uniformly randomly between 0.0 (inclusive) and 1.0 (exclusive)
	 * @return random double
	 */
	protected static double getRandom() {
		return Global.rand.nextDouble();
	}

	public static void fill(Distribution d, double[] array) {
		for (int i = 0; i < array.length; i++)
			array[i] = d.nextDouble();
	}

	public static double[] fill(Distribution d, int N) {
		double[] array = new double[N];
		fill(d, array);
		return array;
	}

	public static void main(String[] args) {
		Distribution dists[] = {	//new Constant(10.0),
									//new Exponential(200),
									//new Pareto(0.8598, 26.7993),
									//new Pareto2(1.1, 3.0),
									//new ParetoII(1.1, 3.0),
									//new Uniform(0, 10),
									//new Zipf(1.0, 1000000)
									//new Weibull(250, 0.6),
									//new Normal(9300.0 * 1000000.0, 1500.0 * 1000000.0),
									new LogNormal(0, 1)
								};

		int N = 10;

		// Loop all the distribution
		for (int i = 0; i<dists.length; i++) {

			long totalStartTime = System.currentTimeMillis();

			System.out.print(dists[i].getClass().getName());

			try {
				System.out.print(" Min:" + dists[i].getMin());
			} catch (Exception e) {}

			try {
				System.out.print(" Mean:" + dists[i].getMean());
			} catch (Exception e) {}

			try {
				System.out.print(" Max:" + dists[i].getMax());
			} catch (Exception e) {}

			System.out.println();
			System.out.println(" CDF(0.00):" + dists[i].getCDF(0.00) );
			System.out.println(" CDF(0.25):" + dists[i].getCDF(0.25) );
			System.out.println(" CDF(0.50):" + dists[i].getCDF(0.50) );
			System.out.println(" CDF(0.75):" + dists[i].getCDF(0.75) );
			System.out.println(" CDF(1.00):" + dists[i].getCDF(1.00) );

			System.out.println();

			// Generate 100 numbers for this dist
			for (int ii = 0; ii < N; ii++) {
				double d = dists[i].nextDouble();
				// Print the next number in this dist
				System.out.print( d + ", ");
			}

			System.out.println();
			System.out.println("Time taken: " + (System.currentTimeMillis() - totalStartTime));
			System.out.println();
		}

	}

	/**
	 * Returns the mean of this distribution
	 * @return
	 */
	public abstract double getMean();

	public abstract double getMin();

	public abstract double getMax();

	/**
	 * Gets the value on the X axis based on the CDF value
	 * ie the Inverse CDF
	 * @param f Must be 0.0 <= f =< 1.0
	 * @return
	 */
	public double getCDF(double f) {
		assert ( f >= 0.0 );
		assert ( f <= 1.0 );

		// If we don't have a a CDF function, lets cheat!
		if (ecdf == null)
			generateCDF();

		return ecdf [ (int)( (ecdf.length - 1) * f ) ];
	}

	private double ecdf[] = null;
	/**
	 * Generates X random variables, and creates a ecdf table for them
	 */
	public void generateCDF() {
		assert ( ecdf == null );

		ecdf = new double[20];

		double values[] = new double[1000];

		for (int i = 0; i < values.length; i++)
			values[i] = nextDouble();

		// Sort the values into order
		Arrays.sort(values);

		// Now count how many values are below each number
		for (int i = 0; i < ecdf.length - 1; i++) {
			ecdf[ i ] = values [ (values.length * i) / (ecdf.length) ];
		}
		ecdf [ ecdf.length - 1 ] = values[ values.length - 1];
	}

}
