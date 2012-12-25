package sim.net.overlay.cdn.cache;

import java.util.List;

import sim.collections.Range;
import sim.collections.Ranges;
import sim.main.Global;


/**
 * A cache of Media
 * Stores which ranges of which media the cache has
 * @author Andrew Brampton
 *
 */
public class RandomCache extends LimitedCache {

	public RandomCache(long maxSize) {
		super(maxSize);
	}


	@Override
	protected void evict() {

		// Check the cache is not full
		while (getSize() > maxSize) {

			int rand = Global.rand.nextInt(cache.size());

			Ranges ranges = cache.get(rand);
			List<? extends Range> list = ranges.getRanges();

			// Randomly throw something away
			rand = Global.rand.nextInt(list.size());

			Range r = list.get(rand);
			long change = (getSize() - maxSize);

			change = Math.min(r.length(), change);

			if (r.length() <= change)
				ranges.remove(r);
			else
				ranges.remove( new Range(r.end - change, r.end));

			size -= change;
		}
	}


	@Override
	protected void request(int mediaID, Range range) {}


	public String toString() {
		return cache + " " + getSize();
	}

	public static void main(String[] args) {
		RandomCache c = new RandomCache(100);

		c.add(0, new Range(0, 10));
		System.out.println(c);

		c.add(0, new Range(50, 100));
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
