package sim.net.overlay.cdn.prefetch;

import static sim.stats.StatsObject.SEPARATOR;
import sim.collections.Range;
import sim.main.Global;
import sim.net.overlay.cdn.Hotspot;
import sim.net.overlay.cdn.Request;

/**
 * Retrieves all the prefetch data before the simulation starts
 * @author Andrew Brampton
 *
 */
public class BeforeStartPrefetcher extends Prefetcher {

	/**
	 * Should we prefetch everything? Or just the bookmarks?
	 */
	final boolean prefetchAll;

	public BeforeStartPrefetcher(boolean prefetchAll) {
		this.prefetchAll = prefetchAll;
	}

	@Override
	public void newRequest( final Request r, final String why ) {

		if ( r == null )
			return;
		
		if ( setMedia( r.getMedia() ) ) {

			// If we are cheating prefetch ahead of time, then store all the bookmark
			clearPrefetchRange();

			if ( prefetchAll ) {

				// If we are cheating prefetch ahead of time, then store everything
				Range range = new Range(0, media.getByteLength());

				client.getCache().add(media.getID(), range);
				client.getPrefetchCache().add( media.getID(), range);
				Global.stats.logRunningTotal("CDN" + SEPARATOR + "Prefetch" + SEPARATOR + "Total", media.getByteLength());

			} else {

				// Prefetch just the hotspots
				for (Hotspot h : media.getHotspots()) {
					Range range = new Range(media.getByteOffset( (int)h.start ), media.getByteOffset( (int)h.end ));

					client.getCache().add(media.getID(), range );
					client.getPrefetchCache().add( media.getID(), range );

					Global.stats.logRunningTotal("CDN" + SEPARATOR + "Prefetch" + SEPARATOR + "Total", range.length() );
				}
			}
		}
	}

	public boolean isEmpty() {
		return true;
	}
}
