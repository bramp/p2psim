package sim.stats.utility;

public class IntValue implements Value {

	int value = 0;

	public IntValue(int value) {
		this.value = value;
	}

	public void setValue(int value) 	{this.value = value;}
	public void setValue(long value) 	{this.value = (int) value;}
	public void setValue(double value) 	{this.value = (int) value;}

	public void increment() 			{value++;}
	public void decrement() 			{value--;}

	public void increment(int amount) 			{value+=amount;}
	public void increment(long amount) 			{value+=amount;}
	public void increment(double amount) 		{value+=amount;}

	public int getIntValue() {			return value;}
	public long getLongValue() {		return value;}
	public double getDoubleValue() {	return value; }

	@Override
	public String toString() {
		return Integer.toString(value);
	}
}
