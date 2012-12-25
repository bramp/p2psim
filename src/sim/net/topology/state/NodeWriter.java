/*
 * Created on 12-Feb-2005
 */
package sim.net.topology.state;

import sim.net.HostSet;

/**
 * @author Andrew Brampton
 */
public interface NodeWriter {

	/**
	 * Saves a list of nodes (including state)
	 * @param nodes The nodes to save
	 */
	void save(HostSet nodes) throws Exception;
}
