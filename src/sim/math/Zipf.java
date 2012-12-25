package sim.math;

public class Zipf extends Distribution {

	final double ALP;
	final int max;

	// Used to cache the 1.0 / ( c * powers[j] )
	final double[] cache;

	public Zipf(double ALP, int max) {
		this.ALP = ALP;
		this.max = max;

		if ( max < 1 )
			throw new RuntimeException( "Zipf's max must be >=1");

		cache = new double[max + 1];
		cache[0] = 0.0;

		double c = 0;
		for (int i=1; i<=max; i++){
			c = c + 1.0 / Math.pow(i, ALP);
		}

		double sum_prob = 0;
		for (int i = 1; i <= max; i++) {
			sum_prob += 1.0 / ( c * Math.pow(i, ALP) );
			cache[i] = sum_prob;
		}
	}

	// Does a binary search for target
	public int search(double [] array, double target) {
	    int high = array.length;
	    int low = -1;
	    int probe;

	    while (high - low > 1) {
	        probe = (high + low) / 2;
	        if (array[probe] < target)
	            low = probe;
	        else
	            high = probe;
	    }

        return high;
	}

	/*
	// Does a linear search for target
	public int search(double [] array, double target) {

		for (int j=1; j<=max; j++) {
			if (array[j] >= target){
				return j;
			}
		}

		throw new RuntimeException("Zipf did not work as expected");
	}
	*/

	@Override
	public double nextDouble() {
		return search(cache, getRandom());
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getMean()
	 */
	@Override
	public double getMean() {
		throw new RuntimeException("Zipf doesn't know his own mean :(");
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getMin()
	 */
	@Override
	public double getMin() {
		return 1;
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getMax()
	 */
	@Override
	public double getMax() {
		return max;
	}

	/* (non-Javadoc)
	 * @see sim.math.Distribution#getCDF(double)
	 */
	@Override
	public double getCDF(double f) {
		throw new RuntimeException("Not implemented");
	}

}
