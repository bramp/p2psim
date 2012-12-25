package sim.events;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.ArrayList;

class EventsSortedSet implements SortedSet<Event> {

	TreeMap<Long, ArrayList<Event>> times = new TreeMap<Long, ArrayList<Event>>();
	int size = 0;

	long firstLong = Long.MAX_VALUE;
	ArrayList<Event> first = null;

	long lastLong = Long.MIN_VALUE;
	ArrayList<Event> last = null;

	/* (non-Javadoc)
	 * @see java.util.SortedSet#first()
	 */
	public Event first() {
		if (isEmpty())
			return null;

		return first.firstElement();

	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#last()
	 */
	public Event last() {
		if (isEmpty())
			return null;

		return last.lastElement();
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#comparator()
	 */
	public Comparator<Event> comparator() {
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#headSet(java.lang.Object)
	 */
	public SortedSet<Event> headSet(Event toElement) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#tailSet(java.lang.Object)
	 */
	public SortedSet<Event> tailSet(Event fromElement) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.SortedSet#subSet(java.lang.Object, java.lang.Object)
	 */
	public SortedSet<Event> subSet(Event fromElement, Event toElement) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.Set#size()
	 */
	public int size() {
		return size;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#clear()
	 */
	public void clear() {
		times.clear();
		size = 0;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#isEmpty()
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#toArray()
	 */
	public Event[] toArray() {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.Set#add(java.lang.Object)
	 */
	public boolean add(Event o) {
		Long l = new Long(o.getTime());

		// Get the vector for this long
		//ArrayList<Event> v = (ArrayList)times.get(l);
		ArrayList<Event> v = fastGet(l);

		// If this vector doesn't exist, create it
		if (v == null) {
			// Create the vector, and place it in the TreeMap
			v = new ArrayList<Event>();
			times.put(l, v);

			// If this new vector is larger than our last, change last
			if (l.longValue() > lastLong) {
				last = v;
				lastLong = l.longValue();
			}
			// If this new vector is smaller than our first, change first
			if (l.longValue() < firstLong) {
				first = v;
				firstLong = l.longValue();
			}
		}

		if (v.add(o)) {
			size++;

			//Global.stats.log("EventsArrayListSize", v.size());

			return true;
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		ArrayList v = times.get(new Long(((Event) o).getTime()));

		if (v == null)
			return false;

		return v.contains(o);
	}

	private ArrayList<Event> fastGet(Long l) {
		// If this is the first long, then do a shortcut
		if (l.longValue() == firstLong) {
			return first;
		} else {
			return times.get(l);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {

		ArrayList v;
		Event e = (Event) o;
		Long l = new Long(e.getTime());

		v = fastGet(l);

		if (v == null)
			return false;

		boolean ret = v.remove(o);

		// If the vector is empty, remove it from the TreeMap
		if (v.isEmpty()) {
			times.remove(l);

			if (!times.isEmpty()) {
				// If v was also the first, pick a new first
				if (v == first) {
					// Find the first long
					l = times.firstKey();
					firstLong = l.longValue();
					first = times.get(l);
				}

				//If v was also the first, pick a new first
				if (v == last) {
					// Find the first long
					l = times.lastKey();
					lastLong = l.longValue();
					last = times.get(l);
				}

			} else {
				first = null;
				last = null;
				firstLong = Long.MAX_VALUE;
				lastLong = Long.MIN_VALUE;
			}
		}

		// If the remove was ok, then size--
		if (ret)
			size--;

		return ret;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection c) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection c) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection c) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection c) {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.Set#iterator()
	 */
	public Iterator<Event> iterator() {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.Set#toArray(java.lang.Object[])
	 */
	public <Event> Event[] toArray(Event[] a) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String toString() {
		return times.toString();
	}

}
