package sim.stats.utility;

public interface Value {

	public void setValue(int value);
	public void setValue(long value);
	public void setValue(double value);

	public int getIntValue();
	public long getLongValue();
	public double getDoubleValue();

	public void increment();
	public void decrement();

	public void increment(int amount);
	public void increment(long amount);
	public void increment(double amount);

}
