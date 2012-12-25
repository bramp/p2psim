package sim.net.overlay.cdn.cache;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import sim.collections.Range;
import sim.collections.RangeCounts;
import sim.collections.RangeValue;
import sim.collections.Ranges;
import sim.main.Global;


/**
 * A Least Frequently Used cache of Media
 * Stores which ranges of which media the cache has
 * @author Andrew Brampton
 *
 */
public class LFUCache extends LimitedCache {

	/**
	 * A Map of MediaIDs to Frequencies
	 */
	Map<Integer, RangeCounts> frequency = new TreeMap<Integer, RangeCounts>();

	public LFUCache (long maxSize) {
		super(maxSize);
	}

	protected void request(int mediaID, Range range) {
		// Now log that this range was recently used
		RangeCounts ranges = frequency.get(mediaID);
		if (ranges == null) {
			ranges = new RangeCounts();
			frequency.put(mediaID, ranges);
		}

		ranges.add( range );
	}

	public void evict() {

		long over = getSize() - maxSize;

		if (over <= 0 )
			return;

		Map<Integer, SortedSet<RangeValue>> chunks = new TreeMap<Integer, SortedSet<RangeValue>>();

		final Comparator<RangeValue> c = new Comparator<RangeValue>() {

			public int compare(RangeValue o1, RangeValue o2) {
				// We want the smallest values first
				int ret = o1.value - o2.value;
				if (ret == 0) {
					// Then we want the highest values first
					ret = (int) (o2.start - o1.start);
					if (ret == 0)
						ret = (int) (o2.end - o1.end);
				}
				return ret;
			}
		};

		for (Map.Entry<Integer, Ranges> e : cache.entrySet()) {
			assert( frequency.containsKey ( e.getKey() ));

			final int mediaID = e.getKey();
			final Ranges cachedRanges = e.getValue();

			SortedSet<RangeValue> s = new TreeSet<RangeValue>(c);

			// Get only the areas we are caching
			//System.out.println("A" + frequency.get( mediaID ));

			RangeCounts ranges = frequency.get( mediaID ).overlap( cachedRanges );

			//System.out.println("B" + cachedRanges);
			//System.out.println("C" + ranges);

			if (ranges == null || ranges.isEmpty())
				continue;

			s.addAll( ranges.getRanges() );

			if (!s.isEmpty())
				chunks.put(mediaID, s);
		}

		// Check the cache is not full
		while (getSize() > maxSize) {

			assert (!chunks.isEmpty());

			// Find the smallest entry from each set of mediaIDs
			Iterator<Map.Entry<Integer, SortedSet<RangeValue> >> i;
			i = chunks.entrySet().iterator();

			Map.Entry<Integer, SortedSet<RangeValue> > e = i.next();
			int smallestMediaID = e.getKey();
			RangeValue smallestRange = e.getValue().first();

			while (i.hasNext()) {
				e = i.next();
				if (smallestRange.value > e.getValue().first().value ) {
					smallestMediaID = e.getKey();
					smallestRange = e.getValue().first();
				}
			}

			// Evict the least requested peice of content
			over = evictMinimum(smallestMediaID, smallestRange, over);

			// Now remove this piece from the list of pieces
			SortedSet<RangeValue> s = chunks.get( smallestMediaID );
			s.remove( smallestRange );
			if (s.isEmpty())
				chunks.remove(smallestMediaID);
		}
	}

	protected void evicted(int mediaID, final Range range) {
		//System.out.println("Evicted " + range + " " + this);
	}

	public String toString() {
		return cache + " "+ getSize();// + " " + frequency;
	}

	public static void main(String[] args) {
		LFUCache c = new LFUCache(500);

		c.add(0, new Range(0, 10));
		c.add(0, new Range(0, 10));
		System.out.println(c);

		c.add(0, new Range(50, 100));
		System.out.println(c);

		c.add(0, new Range(100, 200));
		System.out.println(c);

		long start = System.currentTimeMillis();

		for (int i = 0; i < 100; i++) {
			Range r = null;

			long a = Global.rand.nextInt(1000);
			long b = Global.rand.nextInt(1000);

			if (a < b)
				r = new Range(a, b);
			else if (a > b)
				r = new Range(b, a);

			if (r != null) {
				c.add(0, r);
				//System.out.println(c);
			}
		}

		System.out.println("Took " + (System.currentTimeMillis() - start) + " ms");

		//c.add(0, new Range(25, 50));
		System.out.println(c);
	}

}
