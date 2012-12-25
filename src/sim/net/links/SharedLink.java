/*
 * Created on Feb 8, 2005
 */
package sim.net.links;

import sim.net.Host;

/**
 * @author Andrew Brampton
 */
public class SharedLink extends Link {

	// The delay isn't counted for SharedLinks, it depends on the SharedLinkBase
	public final static int DELAY_DEFAULT = DELAY_NA;
	public final static int BANDWIDTH_DEFAULT = BANDWIDTH_NA;

	SharedLinkBase base = null;

	public SharedLink(Host host1, Host host2) {
		super(host1, host2, BANDWIDTH_DEFAULT, DELAY_DEFAULT);
	}

	/**
	 * Combines this link with another
	 * @param link
	 */
	public void shareWith(SharedLink link) {
		shareWith(link.base);
	}


	/**
	 * @param base
	 */
	public void shareWith(SharedLinkBase base) {
		this.base = base;
		this.bandwidth = base.bandwidth;
		this.cost = base.delay;
	}

	/* (non-Javadoc)
	 * @see sim.net.links.Link#getNextFreeTime()
	 */
	@Override
	protected long getNextFreeTime() {
		if (base != null)
			return base.getNextFreeTime();

		return super.getNextFreeTime();
	}

	/* (non-Javadoc)
	 * @see sim.net.links.Link#setNextFreeTime(long)
	 */
	@Override
	protected void setNextFreeTime(long nextFreeTime) {
		if (base != null)
			base.setNextFreeTime(nextFreeTime);
		else
			super.setNextFreeTime(nextFreeTime);
	}

	/* (non-Javadoc)
	 * @see sim.net.links.Link#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		base = null;
	}
}
