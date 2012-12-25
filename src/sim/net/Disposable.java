package sim.net;

public interface Disposable {
	/**
	 * Used to help the Garabage Collector by dereferencing as much
	 * as possible
	 */
	public void dispose();
}
