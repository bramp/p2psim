package sim.collections;

/**
 * Represents a range
 * @author Andrew Brampton
 *
 */
public class Range implements Cloneable, Comparable<Range> {
	public long start;
	public long end;

	private Range(Range range) {
		setDimensions(range.start, range.end);
	}

	public Range(long start, long end) {
		setDimensions(start, end);
	}

	public void setDimensions(long start, long end) {
		if (start >= end)
			throw new RuntimeException("range start >= end (" + start + " >= " + end + ")");

		this.start = start;
		this.end = end;
	}

	public void setLength(long length) {
		if ( length < 0 )
			throw new RuntimeException("length must be greater than zero");

		end = start + length;
	}

	public long length() {
		assert end > start;
		return end - start;
	}

	public String toString() {
		return "[" + start + "-" + end + ")";
	}

	/**
	 * Returns the overlapping regions (if any)
	 * @param range
	 * @return
	 */
	public Range overlap(Range range) {
		boolean startsWithin = false;
		boolean endsWithin = false;

		if (range.start >= start && range.start < end)
			startsWithin = true;

		if (range.end > start && range.end <= end)
			endsWithin = true;

		// We clone this range, so any class
		// that inherite from us will have thier members
		// copied
		Range ret = clone();

		if (startsWithin && endsWithin) {
			ret.setDimensions(range.start, range.end);
			return ret;
		}

		if (startsWithin) {
			ret.setDimensions(range.start, end);
			return ret;
		}

		if (endsWithin) {
			ret.setDimensions(start, range.end);
			return ret;
		}

		// Check if we are within range
		if (range.start <= start && range.end >= end) {
			return ret;
		}

		ret = null;
		return null;
	}

	/**
	 * Deletes a Range from this range.
	 * The deletion must not disect the range, otherwise two ranges would be formed, AND it must not
	 * delete the full range
	 * @param range
	 * @return
	 */
	public boolean delete(Range range) {
		final Range overlap = overlap( range );

		// If we don't overlap do nothing
		if (overlap == null)
			return false;

		if (overlap.length() >= length())
			throw new RuntimeException("Can't delete full range");

		if (overlap.start > start && overlap.end < end)
			throw new RuntimeException("Can't delete middle range " + range);

		if (overlap.start <= start)
			start = Math.min(overlap.end, end);

		if (overlap.end >= end)
			end = Math.max(overlap.start, start);

		return true;
	}

	public Range clone() {
		return new Range(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (obj instanceof Range) {
			Range r = (Range)obj;
			return start == r.start && end == r.end;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Range o) {
		if (start > o.start)
			return 1;
		else if (start < o.start)
			return -1;
		else {
			if (end > o.end)
				return 1;
			else if (end < o.end)
				return -1;
			else
				return 0;
		}
	}
}
