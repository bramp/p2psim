package sim.net.overlay.cdn.prefetch;

import sim.net.overlay.cdn.Request;

/**
 * Do no prefetching
 * @author Andrew Brampton
 *
 */
public class NullPrefetcher extends Prefetcher {

	@Override
	public void newRequest( final Request r, final String why ) {
		// Do nothing
	}

}
