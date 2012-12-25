package sim.events;

import sim.collections.FibonacciHeap;

/**
 * And event SortedSet that uses a FibonacciHeap
 * @author Andrew Brampton
 *
 */
class EventsSortedSet2 extends FibonacciHeap<Event> {

	int requiredCount = 0;

	@Override
	public boolean add(Event e) {
		boolean ret = super.add(e);

		if (ret && e.isRequired())
			requiredCount++;

		return ret;
	}

	public Event first() {
		return super.first();
	}

	public Event removeFirst() {
		Event e = super.removeFirst();

		if (e.isRequired())
			requiredCount--;

		return e;
	}

	public int getRequiredCount() {
		return requiredCount;
	}

	public int getNonRequiredCount() {
		return size() - requiredCount;
	}

}
