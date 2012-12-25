package sim.net.overlay.cdn.prefetch;

import static sim.stats.StatsObject.SEPARATOR;

import java.util.List;

import sim.main.Global;
import sim.net.overlay.cdn.Hotspot;
import sim.net.overlay.cdn.Request;

/**
 * Pre-fetches X seconds ahead
 * @author Andrew Brampton
 *
 */
public class AheadToBookmarkEndPrefetcher extends Prefetcher {

	final int ahead;

	public AheadToBookmarkEndPrefetcher(int ahead) {
		this.ahead = ahead;
	}

	public AheadToBookmarkEndPrefetcher() {
		this ( 30 );
	}

	@Override
	public void newRequest( final Request r, final String why ) {

		clearPrefetchRange();

		if ( r != null ) {
			setMedia( r.getMedia() );

			int start = media.getSecondOffset( r.getPosition() );
			int end = media.getLength();			

			// Figure out what bookmark we are near
			List<Hotspot> hotspots = media.getHotspots();
			Hotspot best = null;

			assert ( ! hotspots.isEmpty() );

			// TODO this could be optimised to stop searching once we go past the best
			for (Hotspot h : hotspots)
				if ( best == null || Math.abs( h.start - start ) < Math.abs( h.start - best.start ) )
					best = h;

			// Pre-fetch this far ahead of the playback point
			start += ahead;

			end = (int) best.end;

			if ( end < start ) {
				// If we are outside of any bookmark
				end = media.getLength();

				Global.stats.logCount("DEBUG" + SEPARATOR + "PrefetchOUTBookmark");
			} else {
				Global.stats.logCount("DEBUG" + SEPARATOR + "PrefetchINBookmark");
			}

			// Does this prefetch make sense?
			if ( start < end )
				addPrefetchRange(start, end );
		}
	}
}
