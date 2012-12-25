package sim.net.overlay.cdn.prefetch;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sim.net.overlay.cdn.Hotspot;
import sim.net.overlay.cdn.Request;

/**
 *  Request the bookmarks based on the order of their popularity
 * @author Andrew Brampton
 *
 */
public class PopularityPrefetcher extends Prefetcher {

	@Override
	public void newRequest( final Request r, final String why ) {
		if ( r != null && setMedia( r.getMedia() ) ) {
			List<Hotspot> hotspots = media.getHotspots();

			// Sort the hotspots into their order of popularity
			Collections.sort(hotspots, new Comparator<Hotspot>() {
				//@Override
				public int compare(Hotspot o1, Hotspot o2) {
					return o2.hits - o1.hits;
				}
			} );

			clearPrefetchRange();
			addPrefetchRanges( hotspots );
		}
	}

}
