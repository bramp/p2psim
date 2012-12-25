/**
 *
 */
package sim.net.overlay.meridian;

import sim.events.Event;

/**
 * @author Andrew Brampton
 *
 */
public class RequestEvent extends Event {

	public Client c;
	public int s;

	/**
	 * This client will make a request to that server
	 * @param c Client making the request
	 * @param s Address of the server requested
	 */
	public RequestEvent(Client c, int s) {
		this.c = c;
		this.s = s;
	}

	/* (non-Javadoc)
	 * @see sim.events.Event#getEstimatedRunTime()
	 */
	@Override
	public long getEstimatedRunTime() {
		return 10000;
	}

	/* (non-Javadoc)
	 * @see sim.events.Event#run()
	 */
	@Override
	public void run() throws Exception {
		c.makeRequest(s);
	}

}
