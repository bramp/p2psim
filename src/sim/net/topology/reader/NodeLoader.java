/**
 *
 */
package sim.net.topology.reader;

import sim.net.Host;

/**
 * @author macquire
 *
 */
public interface NodeLoader {
	public Host createHost(String type, int address);
}
