/**
 *
 */
package sim.net.links;

import sim.main.Global;
import sim.net.Host;

/**
 *	A link that can't make up its mind on its delay value
 *  It will change during the simulations
 * @author Andrew Brampton
 *
 */
public class CrapLink extends NormalLink {

	int mindelay;
	int maxdelay;

	/**
	 * @param host1
	 * @param host2
	 * @param bandwidth
	 * @param mindelay
	 * @param maxdelay
	 */
	public CrapLink(Host host1, Host host2, int bandwidth, int mindelay, int maxdelay) {
		super(host1, host2, bandwidth, (mindelay + maxdelay) / 2);
		this.mindelay = mindelay;
		this.maxdelay = maxdelay;
	}

	/**
	 * Returns a random delay between mindelay and maxdelay
	 */
	@Override
	public int getDelay() {
		return Global.rand.nextInt(maxdelay - mindelay) + mindelay;
	}
}
