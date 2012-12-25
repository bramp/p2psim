package sim.collections;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import sim.main.Global;

/**
 * Represents a list of ranges, with a value attached to each one
 * @author Andrew
 *
 */
public class RangeCounts extends Ranges {

	public static class RangeValuesComparator implements Comparator<RangeValue> {
		public int compare(RangeValue r0, RangeValue r1) {
			assert (r0 != null);
			assert (r1 != null);
			if (r0.value == r1.value) {
				if (r0.start == r1.start) {
					return (int) -(r0.end - r1.end);
				}
				return (int) -(r0.start - r1.start);
			}
			return (int) -(r0.value - r1.value);
		}
	}

	private List<RangeValue> ranges = new ArrayList<RangeValue>();

	public List<? extends RangeValue> getRanges() {
		return ranges;
	}

	public boolean add(final RangeValue range) {
		return add(range, range.value);
	}

	public boolean add(final Range range) {
		if (range instanceof RangeValue) // HACK
			return add ( (RangeValue) range );
		else
			return add(range, 1);
	}

	/**
	 * Add this range to this list of ranges
	 * @param range
	 */
	public boolean add(final Range range, int value) {

		assert checkLength() == length;
		assert checkOverlap();
		assert checkSort();

		@SuppressWarnings("unchecked")
		final List<RangeValue> ranges = (List<RangeValue>) getRanges();

		boolean changed = false;
		length += range.length();

		//int size = ranges.size();
		RangeValue r = !ranges.isEmpty() ? ranges.get(0) : null;

		// Check if we need to insert ona anges is empty, OR range is before the first range
		long lastEnd = range.start;

		int size = ranges.size();

		// We might be overlapping, so check that and fill in the gaps
		for (int i = findFirstOverlap(range); i < size; i++) {
			r = ranges.get(i);

			if (r.start > range.end) {
				break;
			}

			//    /-----\   range
			// /-----\      r
			if (range.start > r.start && range.start < r.end && range.end >= r.end) {
				//System.out.println("A" + i + " " + r + " " + range);

				lastEnd = r.end;
				length -= (r.end - range.start);

				// Add the middle bit
				ranges.add(new RangeValue(range.start, r.end, r.value + value));
				//ranges.add(new RangeValue(r.end, range.end, 1));

				// Adjust the first bit
				r.end = range.start;

				// The last bit will be fixed later on

				changed = true;

			// /-----\     range
			//    /-----\  r
			} else if (range.end > r.start && range.end < r.end && range.start <= r.start) {
				//System.out.println("B" + i + " " + r + " " + range);

				length -= (range.end - r.start);

				if (r.start > Math.max(lastEnd, range.start))
					ranges.add(new RangeValue(Math.max(lastEnd, range.start), r.start, value));

				ranges.add(new RangeValue(r.start, range.end, r.value + value));
				r.start = range.end;

				lastEnd = r.end;
				changed = true;

				//System.out.println(this);

			// /----------\ range
			//     /--\     r
			} else if (r.start >= range.start && r.end <= range.end) {
				//System.out.println("C" + i + " " + r + " " + range);

				if (r.start > Math.max(lastEnd, range.start)) {
					//System.out.println("CZ" + i + " " + r + " " + range);

					ranges.add(new RangeValue(Math.max(lastEnd, range.start), r.start, value));
				}

				r.value += value;
				length -= r.length();

				lastEnd = r.end;
				changed = true;

				//System.out.println(this);

			//     /--\    range
			// /---------\ r
			} else if (range.start > r.start && range.end < r.end) {
				//System.out.println("D" + i + " " + r + " " + range);

				lastEnd = r.end;

				ranges.add(new RangeValue(r.start, range.start, r.value));
				ranges.add(new RangeValue(range.end, r.end, r.value));

				r.start = range.start;
				r.end = range.end;
				r.value += value;

				length -= range.length();

				changed = true;

				//System.out.println(this);

			// /----\        range
			//        /----\ r
			} else if (lastEnd < range.start && range.end < r.start) {
				//System.out.println("E" + i + " " + lastEnd + " " + r.start + " " + r.end + " " + range.start + " " + range.end);

				ranges.add(new RangeValue(Math.max(lastEnd, r.start), range.end, value));

				lastEnd = r.end;
				changed = true;

				//System.out.println(this);
			} else {
				//System.out.println("Unknown");
			}

		}

		// Check if there is room left on the end
		if (lastEnd < range.end) {
			//System.out.println("F " + lastEnd + " " + range.start + " " + range.end);

			ranges.add(new RangeValue(Math.max(lastEnd, range.start), range.end, value));
			changed = true;

			//System.out.println(this);
		}

		if (changed) {
			Collections.sort(ranges, Ranges.rangeStartCmp);

			// Now try and merge continous regions with the same value
			for (int i = ranges.size() -1; i > 0; --i) {
				RangeValue r1 = ranges.get(i - 1);
				RangeValue r2 = ranges.get(i);

				if (r1.end == r2.start && r1.value == r2.value) {
					r1.end = r2.end;
					ranges.remove(i);
				}
			}
		}

		assert checkLength() == length;
		assert checkOverlap();
		assert checkSort();

		return changed;
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
	public RangeCounts overlap(final Range range) {

		assert(range != null);
		assert checkSort();

		@SuppressWarnings("unchecked")
		final List<RangeValue> ranges = (List<RangeValue>)getRanges();
		final RangeCounts ret = new RangeCounts();

		for(int i = findFirstOverlap(range); i < ranges.size(); i++) {
			final RangeValue r = ranges.get(i);

			if (range.end < r.start)
				break;

			final RangeValue overlap = r.overlap( range );

			//System.out.println("0" + overlap);

			if (overlap != null)
				ret.add( overlap );
		}

		return ret;
	}

	public RangeCounts overlap(final Ranges ranges) {
		RangeCounts ret = new RangeCounts();
		for (int i = 0; i < ranges.size(); i++) {
			Ranges o = this.overlap( ranges.get(i) );
			if (o != null)
				ret.add( o );
		}
		return ret;
	}

	/**
	 * Decrease the value of each range by i, and remove any
	 * that are <= 0
	 * @param val
	 */
	public void decrease(int val) {
		Iterator<? extends Range> i = getRanges().iterator();

		while (i.hasNext()) {
			RangeValue r = (RangeValue) i.next();
			if (r.value > val)
				r.value -= val;
			else
				i.remove();
		}
	}

	public RangeValue first() {
		return (RangeValue) super.first();
	}

	public RangeValue get(int index) {
		return (RangeValue) super.get(index);
	}

	public RangeValue last() {
		return (RangeValue) super.last();
	}

	/**
	 * Finds the ranges in the top X percentage
	 * @param percente
	 * @return
	 */
	public RangeCounts top(double percentage) {
		assert percentage >= 0 && percentage <= 1;

		if ( percentage == 1)
			return this;

		RangeCounts r = new RangeCounts();

		if ( percentage == 0)
			return r;

		// Figure out how much we want
		long top = (long) (this.length() * percentage);

		// Now sort all the ranges by value
		List<RangeValue> sortedRanges = new ArrayList<RangeValue>(ranges);

		Collections.sort(sortedRanges, new RangeValuesComparator());
		int i = 0;

		while (top > 0 && i < sortedRanges.size()) {
			Range range = sortedRanges.get( i );

			r.add(range);
			top -= range.length();

			i++;
		}

		return r;

	}

	public static void main (String[] args) {
		RangeCounts ranges = new RangeCounts();

		/*
		ranges.add( new RangeValue(5, 10, 2) );
		System.out.println(ranges);
		ranges.add( new RangeValue(10, 15, 1) );
		System.out.println(ranges);

		System.exit(0);

		ranges.add( new Range(0, 10) );
		System.out.println(ranges);
		ranges.add( new Range(10, 20) );
		System.out.println(ranges);
		ranges.add( new Range(0, 10) );
		System.out.println(ranges);

		Range tmp = new Range(5, 15);

		System.out.println( ranges.overlap(tmp) );

		System.exit(0);
		*/

		Global.rand.setSeed(1);

		for (int i = 0; i < 10000; i++) {
			int a = Global.rand.nextInt(1000000);
			int b = Global.rand.nextInt(1000000);

			Range r;
			if (a < b)
				r = new Range(a, b);
			else if (b < a)
				r = new Range(b, a);
			else
				continue;

			ranges.add(r);
			//System.out.println(ranges);
		}

		System.out.println("Done");

	}
}

