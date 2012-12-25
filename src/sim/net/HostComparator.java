/**
 *
 */
package sim.net;

import java.util.Comparator;

/**
 * Compare these two Hosts simply as hosts
 * @author Andrew Brampton
 *
 */
public class HostComparator implements Comparator<Host> {

	public int compare(Host o1, Host o2) {
		return o1.address - o2.address;
	}

}
