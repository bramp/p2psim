package sim.net.overlay.cdn.prefetch;

import java.util.List;

import sim.collections.Range;
import sim.net.overlay.cdn.Hotspot;
import sim.net.overlay.cdn.Request;

/**
 * Pre-fetches X seconds ahead
 * @author Andrew Brampton
 *
 */
public class AheadAndPredictPrefetcher extends Prefetcher {

	final int ahead;
	final Prefetcher next;

	public AheadAndPredictPrefetcher(int ahead, Prefetcher next) {
		assert next != null;
		
		this.ahead = ahead;
		this.next = next;
	}

	public AheadAndPredictPrefetcher(Prefetcher next) {
		this ( 30, next );
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

			// Are we still before the bookmark end? If so prefetch until then		
			if ( start < end )
				// Do the normal ahead prefetching, but insert this at the front
				addPrefetchRange(start, end );
			else
				// If we are past the bookmark end, just keep prefetching ahead
				addPrefetchRange(start, media.getLength() );
		}
		
		// Prefech whatever next wants after ours
		next.newRequest( r, why );
		for ( Range range : next.getPrefetchOrder() )
			this.prefetchOrder.add( range );
	}
}
