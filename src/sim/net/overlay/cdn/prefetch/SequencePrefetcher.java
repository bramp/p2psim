package sim.net.overlay.cdn.prefetch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import sim.collections.Range;
import sim.net.overlay.cdn.Request;

/**
 * Request the bookmarks in sequential order
 * @author Andrew Brampton
 *
 */
public class SequencePrefetcher extends Prefetcher {

	/**
	 * Should we prefetch sequentially, but only after the playback point?
	 */
	final boolean onlyAfterPlaybackPoint;

	public SequencePrefetcher(boolean onlyAfterPlaybackPoint) {
		this.onlyAfterPlaybackPoint = onlyAfterPlaybackPoint;
	}

	@Override
	public void newRequest( final Request r, final String why ) {

		if ( (r != null && setMedia ( r.getMedia() )) || onlyAfterPlaybackPoint ) {
			clearPrefetchRange();
			addPrefetchRanges( media.getHotspots() );

			// Should sort the ranges based on their start times
			Collections.sort(prefetchOrder);

			// If we prefetch only things after our currently playback point
			if (onlyAfterPlaybackPoint && r != null) {

				// Make sure this prefetch range is after our current playback point
				List<Range> afterwards = new ArrayList<Range>();
				int playbackSecond = media.getSecondOffset( r.getPosition() );
				
				// HACK Now tweak this a little (to make sure the bookmark isn't within 90 seconds of us)
				long playbackByte = media.getByteOffset( playbackSecond + 90);

				Iterator<Range> i = prefetchOrder.iterator();
				while (i.hasNext()) {
					Range range = i.next();
					if( range.start <= playbackByte ) {
						// If this before the playback point remove it
						i.remove();
						afterwards.add(range);
					}
				}

				// Now just in case, add all the removed places to the end of the prefetch list
				prefetchOrder.addAll( afterwards );
			}
		}
	}

}
