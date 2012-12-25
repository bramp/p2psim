package sim.stats.utility;

public class LongValue implements Value {

	long value = 0;

	public LongValue(long value) {
		this.value = value;
	}

	public void setValue(int value) 	{this.value = value;}
	public void setValue(long value) 	{this.value = value;}
	public void setValue(double value) 	{this.value = (long) value;}

	public void increment() 			{value++;}
	public void decrement() 			{value--;}

	public void increment(int amount) 			{value+=amount;}
	public void increment(long amount) 			{value+=amount;}
	public void increment(double amount) 		{value+=amount;}

	public int getIntValue() {			return (int) value;}
	public long getLongValue() {		return value;}
	public double getDoubleValue() {	return value; }

	@Override
	public String toString() {
		return Long.toString(value);
	}
}
