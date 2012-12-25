package sim.collections;

import java.util.Arrays;

/**
 * Represents a list of ranges, with a value attached to each one
 * @author Andrew
 *
 */
public class ArrayRangeCounts {

	int[] list = new int[0];
	int size = 0;

	public ArrayRangeCounts(int size) {
		expand(size);
	}

	/**
	 * Expand the internal array to atleast newsize big
	 * @param newsize
	 */
	protected void expand(int newsize) {
		if ( list.length < newsize ) {
			list = Arrays.copyOf(list, (int) (newsize * 1.3));
		}
	}

	protected void shrink() {
		list = Arrays.copyOf(list, size);
	}

	public boolean add(final int start, final int end, final int value) {

		assert end > start;
		assert value != 0;
		
		// If we are too small expand
		expand( end );

		if ( end > size)
			size = end;

		for (int i = start; i < end; i++)
			list[i] = list[i] + value;

		return true;
	}

	public boolean add(final RangeValue range) {
		return add((int)range.start, (int)range.end, range.value);
	}

	public boolean add(final Range range) {
		int value = 1;

		if (range instanceof RangeValue) // HACK
			value = ((RangeValue)range).value;

		return add((int)range.start, (int)range.end, value);
	}

	/**
	 * Deletes this range from the list
	 * @param delrange
	 * @return
	 */
	public boolean delete(final Range delrange) {
		throw new RuntimeException("Not implemented");
	}

	/**
	 * Returns the ranges that this range overlaps with the ranges (if any)
	 * @param range
	 * @return The overlapping ranges
	 */
	public ArrayRangeCounts overlap(final Range range) {
		throw new RuntimeException("Not implemented");
	}

	public ArrayRangeCounts overlap(final Ranges ranges) {
		throw new RuntimeException("Not implemented");
	}

	/**
	 * Decrease the value of each range by i, and remove any
	 * that are <= 0
	 * @param val
	 */
	public void decrease(int val) {
		throw new RuntimeException("Not implemented");
	}

	public RangeValue first() {
		throw new RuntimeException("Not implemented");
	}

	public RangeValue get(int index) {
		throw new RuntimeException("Not implemented");
	}

	public RangeValue last() {
		throw new RuntimeException("Not implemented");
	}

	/**
	 * Finds the ranges in the top X percentage
	 * @param percente
	 * @return
	 */
	public ArrayRangeCounts top(double percentage) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		shrink();

		return Arrays.toString( list );
	}
}

