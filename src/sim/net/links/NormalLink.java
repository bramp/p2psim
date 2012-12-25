/*
 * Created on Feb 8, 2005
 */
package sim.net.links;

import sim.net.Host;


/**
 * @author Andrew Brampton
 */
public class NormalLink extends Link {

	public NormalLink(Host host1, Host host2) {
		super(host1, host2);
	}

	public NormalLink(Host host1, Host host2, int bandwidth, int delay) {
		super(host1, host2, bandwidth, delay);
	}
}
