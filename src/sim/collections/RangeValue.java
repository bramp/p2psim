/**
 *
 */
package sim.collections;

public class RangeValue extends Range {

	public int value;

	public RangeValue(long start, long end, int value) {
		super(start, end);
		this.value = value;
	}

	public RangeValue(Range range, int value) {
		this(range.start, range.end, value);
	}

	public String toString() {
		return super.toString() + "=" + value;
	}

	public RangeValue overlap(Range r) {
		Range r2 = super.overlap(r);
		if (r2 == null)
			return null;

		return new RangeValue(r2, value);
	}

	@Override
	public RangeValue clone() {
		return new RangeValue(start, end, value);
	}
}