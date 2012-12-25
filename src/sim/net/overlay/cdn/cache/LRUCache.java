package sim.net.overlay.cdn.cache;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import sim.collections.Range;
import sim.collections.Ranges;
import sim.main.Global;


/**
 * A Least Recently Used cache of Media
 * Stores which ranges of which media the cache has
 * @author Andrew Brampton
 *
 */
public class LRUCache extends LimitedCache {

	class MediaRanges extends Ranges {
		int mediaID;

		MediaRanges(int mediaID) {
			this.mediaID = mediaID;
		}

		MediaRanges(int mediaID, Range range) {
			super(range);
			this.mediaID = mediaID;
		}
	}

	List<MediaRanges> recently = new ArrayList<MediaRanges>();

	public LRUCache (long maxSize) {
		super ( maxSize );
	}

	protected void request(int mediaID, Range range) {
		// Now search the list for any range that overlaps with this new one
		// and remove them (because they are about to be placed on the end of the list)
		Iterator<MediaRanges> i = recently.iterator();
		while (i.hasNext() ) {
			MediaRanges r = i.next();
			if (r.mediaID == mediaID) {
				if ( r.remove(range) ) {
					if (r.isEmpty())
						i.remove();
				}
			}
		}

		// Now add this range onto the end
		recently.add( new MediaRanges( mediaID, range ) );
	}

	@Override
	protected void evict() {
		// Check the cache is not full
		do {
			long over = getSize() - maxSize;

			if (over <= 0)
				return;

			Iterator<MediaRanges> i = recently.iterator();

			while (over > 0) {
				assert i.hasNext() : "We are storing too much, but have nothing to evict";

				// Find the least used segment of video
				MediaRanges r = i.next();

				// If over < r, then don't remove all of r
				if (over < r.length()) {
					over = evictMinimum(r.mediaID, r, over);
				} else {
					assert (has(r.mediaID, r));
					i.remove();
					remove(r.mediaID, r);
					over -= r.length();
					evicted(r.mediaID, r);
				}
			}

		} while (true);
	}

	public static void main(String[] args) {
		LRUCache c = new LRUCache(500);

		c.add(0, new Range(0, 10));
		System.out.println(c);

		c.add(0, new Range(50, 100));
		System.out.println(c);

		c.add(0, new Range(100, 200));
		System.out.println(c);

		c.add(0, new Range(100, 200));
		System.out.println(c);

		for (int i = 0; i < 1000; i++) {
			Range r = null;

			long a = Global.rand.nextInt(1000);
			long b = Global.rand.nextInt(1000);

			if (a < b)
				r = new Range(a, b);
			else if (a > b)
				r = new Range(b, a);

			if (r != null) {
				c.add(0, r);
				System.out.println(c);
			}
		}

		//c.add(0, new Range(25, 50));
		//System.out.println(c);

	}
}
