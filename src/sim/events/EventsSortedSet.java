package sim.events;

import java.util.TreeSet;

/**
 * And event SortedSet that uses a TreeSet
 * @author Andrew Brampton
 *
 */
class EventsSortedSet extends TreeSet<Event> {

	int requiredCount = 0;

	@Override
	public boolean add(Event o) {
		boolean ret = super.add(o);

		if (ret && o.isRequired())
			requiredCount++;

		return ret;
	}

	public Event removeFirst() {
		Event e = pollFirst();

		if (e != null && e.isRequired())
			requiredCount--;

		return e;
	}

	public boolean remove(Event e) {
		boolean ret = super.remove(e);

		if (ret && e.isRequired())
			requiredCount--;

		return ret;
	}

	public int getRequiredCount() {
		return requiredCount;
	}

	public int getNonRequiredCount() {
		return size() - requiredCount;
	}

}
