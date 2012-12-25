/*
 * Created on 12-Feb-2005
 */
package sim.net.topology.state;

import sim.net.topology.reader.NodeLoader;

/**
 * @author Andrew Brampton
 */
public interface NodeReader {

	/**
	 * Loads a set of nodes from file
	 * @param loader
	 * @return A list of loaded nodes
	 */
	public void load(NodeLoader loader) throws Exception;
}