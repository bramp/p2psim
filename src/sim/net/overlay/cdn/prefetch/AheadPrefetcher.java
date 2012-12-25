package sim.net.overlay.cdn.prefetch;

import sim.net.overlay.cdn.Request;

/**
 * Pre-fetches X seconds ahead
 * @author Andrew Brampton
 *
 */
public class AheadPrefetcher extends Prefetcher {

	final int ahead;

	public AheadPrefetcher(int ahead) {
		this.ahead = ahead;
	}

	public AheadPrefetcher() {
		this ( 30 );
	}

	@Override
	public void newRequest( final Request r, final String why ) {

		clearPrefetchRange();

		if ( r != null ) {
			setMedia( r.getMedia() );

			int start = media.getSecondOffset( r.getPosition() );
			int end = media.getLength();

			// Pre-fetch this far ahead of the playback point
			start += ahead;

			// Pre-fetch from here to the end of the file
			if ( start < end )
				addPrefetchRange(start, end );
		}
	}
}
