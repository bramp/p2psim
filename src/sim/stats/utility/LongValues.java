package sim.stats.utility;

public class LongValues extends Values {
	private long[] values;

	public LongValues(long firstValue) {
		values = new long[INITIAL_SIZE];
		currPos = 0;
		this.addValue(firstValue);
	}

	public void addValue(long value) {
		// check to see if array needs resizing
		if (currPos >= values.length -1) {
			expand();
		}
		values[currPos] = value;
		currPos++;
	}

	private void expand() {
		// grow array exponentially
		long[] newValues = new long[values.length * 2];
		// fast array copy
		System.arraycopy(values,0,newValues,0,values.length);
		values = newValues;
	}

	private void shrink() {
		// trim array down to required size only
		long[] newValues = new long[currPos];
		System.arraycopy(values,0,newValues,0,currPos);
		values = newValues;
	}

	public long[] getValues() {
		shrink();
		return values;
	}

	@Override
	public String toString() {
		String result = "[";
		for (int i=0;(i<currPos && i<PRINT_SIZE);i++) {
			result += values[i] + ",";
		}
		result = result.substring(0,result.length()-1);
		if (currPos > PRINT_SIZE) {
			result += "...";
		}
		result += "]";

		return result;
	}
}
