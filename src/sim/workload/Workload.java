/**
 *
 */
package sim.workload;

/**
 * @author brampton
 * Represents a workload
 */
public interface Workload {

	/**
	 * Called just before the simulation starts
	 */
	public void simulationStart();

	/**
	 * Called just after the simulation finishes
	 * Do any post processing in here, such as adding more stats, etc
	 */
	public void simulationFinished();

}
