package sim.net.overlay.cdn.prefetch;

import sim.main.Helper;
import sim.net.overlay.cdn.Request;

/**
 * Request the bookmarks in a random order
 * @author Andrew Brampton
 *
 */
public class RandomPrefetcher extends Prefetcher {

	@Override
	public void newRequest( final Request r, final String why ) {
		if ( r != null && setMedia( r.getMedia() ) ) {
			clearPrefetchRange();
			addPrefetchRanges( media.getHotspots() );

			Helper.shuffle(prefetchOrder);
		}
	}
}
