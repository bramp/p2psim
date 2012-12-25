package sim.stats.utility;

public class IntValues extends Values {
	private int[] values;

	public IntValues(int firstValue) {
		values = new int[INITIAL_SIZE];
		currPos = 0;
		this.addValue(firstValue);
	}

	public void addValue(int value) {
		// check to see if array needs resizing
		if (currPos >= values.length -1) {
			expand();
		}
		values[currPos] = value;
		currPos++;
	}

	private void expand() {
		// grow array exponentially
		int[] newValues = new int[values.length * 2];
		// fast array copy
		System.arraycopy(values,0,newValues,0,values.length);
		values = newValues;
	}

	private void shrink() {
		// trim array down to required size only
		int[] newValues = new int[currPos];
		System.arraycopy(values,0,newValues,0,currPos);
		values = newValues;
	}

	public int[] getValues() {
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
