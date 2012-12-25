package sim.collections;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a list of ranges, with some additional methods
 * @author Andrew
 *
 */
public class Ranges {

	public static class RangeStartComparator implements Comparator<Range> {
		public int compare(Range r0, Range r1) {
			assert (r0 != null);
			assert (r1 != null);
			if (r0.start == r1.start)
				return (int) (r0.end - r1.end);
			return (int) (r0.start - r1.start);
		}
	}

	static Comparator<? super Range> rangeStartCmp = new RangeStartComparator();
	public static void main (String[] args) {
		Ranges ranges = new Ranges();

		ranges.add(new Range(135,220));
		System.out.println(ranges);

		ranges.add(new Range(580,595));
		System.out.println(ranges);

		ranges.add(new Range(117,773));
		System.out.println(ranges);

		if (1 < 2)
			return;

		/*
		Range r1 = new Range(160, 210);
		Range r2 = new Range(150, 170);

		System.out.println(r1);
		System.out.println(r2);
		System.out.println(r1.overlap(r2));
		System.out.println(r2.overlap(r1));
		*/

		ranges.add( new Range(0, 10) );
		System.out.println(ranges);

		ranges.add( new Range(10, 20) );
		System.out.println(ranges);

		ranges.add( new Range(-10, 100) );
		System.out.println(ranges);

		ranges.add( new Range(200, 300) );
		System.out.println(ranges);

		ranges.add( new Range(150, 170) );
		System.out.println(ranges);

		//System.out.println( ranges.overlap(new Range(0, 1000)) );

		ranges.remove( new Range(160, 210) );
		System.out.println(ranges);

		ranges.remove( new Range(90, 100) );
		System.out.println(ranges);

		ranges.remove( new Range(210, 220) );
		System.out.println(ranges);

		ranges.remove( new Range(90, 100) );
		System.out.println(ranges);
	}

	long length = 0;

	private List<Range> ranges = new ArrayList<Range>();

	public Ranges() {}

	public Ranges(Range range) {
		add(range);
	}

	/**
	 * Add this range to this list of ranges
	 * @param range
	 */
	public boolean add(Range range) {
		assert range != null;
		assert checkSort();

		// Allways create a copy of range (for safety)
		range = range.clone();

		long length = length();

		Common.sortedInsert ( getRanges(), range, rangeStartCmp );
		//ranges.add( range );

		this.length += range.length();

		// Keep merging until no changes are being made
		while (merge()) {};

		assert this.length >= 0;
		assert checkLength() == this.length;
		assert checkSort();

		// Check if we have changed length (if so we have changed)
		if (length() != length)
			return true;
		else
			return false;
	}

	public boolean add(final Ranges ranges) {
		boolean changed = false;
		for (int i = 0; i < ranges.size(); i++)
			changed |= add( ranges.get(i) );
		return changed;
	}

	protected long checkLength() {
		long l = 0;
		for (Range r: getRanges())
			l += r.length();
		return l;
	}

	protected boolean checkOverlap() {
		Range last = null;
		for (Range r: getRanges()) {
			if (last != null)
				if (last.end > r.start)
					return false;
			last = r;
		}
		return true;
	}

	protected boolean checkSort() {
		return Common.isSorted(getRanges(), rangeStartCmp);
	}

	/**
	 * Returns the first consective range in this set of ranges
	 * @return
	 */
	public Range first() {
		final List<? extends Range> ranges = getRanges();

		if (ranges.isEmpty())
			return null;

		assert checkSort();

		return ranges.get( 0 ).clone();
	}

	public Range get(int index) {
		return getRanges().get(index);
	}

	// Finds the first range that could overlap with this one
	protected int findFirstOverlap(Range range) {
		assert checkSort();

		final List<? extends Range> ranges = getRanges();

		// TODO this could be changed to use a binary search
		for (int a = 0; a < ranges.size(); a++) {
			Range r = ranges.get(a);
			if (r.end > range.start) {
				return a;
			}
		}
		return ranges.size();
	}

	public List<? extends Range> getRanges() {
		return ranges;
	}

	public boolean isEmpty() {
		return getRanges().isEmpty();
	}

	/**
	 * Returns the last consective range in this set of ranges
	 * @return
	 */
	public Range last() {
		final List<? extends Range> ranges = getRanges();

		if (ranges.isEmpty())
			return null;

		assert checkSort();

		return ranges.get( ranges.size() - 1 ).clone();
	}

	/**
	 * Merges the ranges together if they overlap or touch
	 * @param list
	 */
	private boolean merge() {
		boolean changed = false;

		assert checkSort();

		final List<? extends Range> ranges = getRanges();

		for (int i = ranges.size() - 1; i > 0; i--) {
			Range a = ranges.get(i - 1);
			Range b = ranges.get(i);

			// If they overlap, extend a to include B, and remove b from the list
			if (a.end >= b.start) {
				// remove b
				length -= ranges.remove(i).length();

				// extend a to overlap b
				long oldEnd = a.end;
				a.end = Math.max(a.end, b.end);
				length += a.end - oldEnd;

				changed = true;
			}
		}

		if (changed)
			Collections.sort(ranges, rangeStartCmp);

		assert this.length >= 0;
		assert ranges.isEmpty() == (this.length == 0) : "This ranges is empty, but length isn't zero, OR the range has elements but the length is zero";

		return changed;
	}

	/**
	 * Returns the ranges that this range overlaps with the ranges (if any)
	 * @param range
	 * @return The overlapping ranges
	 */
	public Ranges overlap(final Range range) {

		assert(range != null);
		assert checkSort();

		final List<? extends Range> ranges = getRanges();

		final Ranges ret = new Ranges();

		for(int i = findFirstOverlap(range); i < ranges.size(); i++) {
			final Range r = ranges.get(i);

			// Since the list is sorted, if we got this far break
			if (range.end < r.start)
				break;

			final Range overlap = range.overlap( r );

			if (overlap != null)
				ret.add( overlap );

		}

		return ret;
	}

	public Ranges overlap(final Ranges ranges) {
		Ranges ret = new Ranges();
		for (int i = 0; i < ranges.size(); i++) {
			Ranges o = this.overlap( ranges.get(i) );
			if (o != null)
				ret.add( o );
		}
		return ret;
	}

	/**
	 * Deletes this range from the list
	 * @param delrange
	 * @return
	 */
	public boolean remove(final Range delrange) {

		assert delrange != null;
		assert checkLength() == this.length;

		@SuppressWarnings("unchecked")
		final List<Range> ranges = (List<Range>)getRanges();

		boolean changed = false;

		for(int i = findFirstOverlap(delrange); i < ranges.size(); i++) {
			final Range r = ranges.get(i);

			// Since the list is sorted, if we got this far break
			if (delrange.end < r.start)
				break;

			final Range overlap = r.overlap(delrange);

			if (overlap == null)
				continue;

			//Check if we need to delete the full range
			if (overlap.equals( r )) {
				//System.out.println("A" + r + " " + overlap);

				ranges.remove(i);
				i--;

			// Check if this del will chop this range in half
			} else if (overlap.start > r.start && overlap.end < r.end){
				//System.out.println("B" + r + " " + overlap);

				ranges.add( new Range( overlap.end, r.end ) );
				r.end = overlap.start;

			// Finally just try and delete the overlaop
			} else {
				//System.out.println("C" + r + " " + overlap);

				r.delete(overlap);
			}

			//System.out.println("D" + r + " " + overlap);

			length -= overlap.length();
			changed = true;

		}

		if (changed)
			Collections.sort(ranges, rangeStartCmp);

		assert this.length >= 0 : "Invalid length " + this.length + " when removing " + delrange;
		assert checkLength() == this.length : "Invalid length " + this.length + " does not match calced length";
		assert checkSort();

		return changed;
	}

	/**
	 * Returns the number of Range elements in this Ranges
	 * @return
	 */
	public int size() {
		return getRanges().size();
	}

	/**
	 * Returns the length of the combined Range elements
	 * @return
	 */
	public long length() {
		return length;
	}

	public String toString() {
		return getRanges().toString() + " " + length();
	}

	public Range[] toArray() {
		Range[] r = new Range[ size() ];
		return getRanges().toArray ( r );
	}
}

