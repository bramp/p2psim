package sim.events;

import java.util.Comparator;

import sim.collections.IndexedSortedSet;


class EventsSortedSet2 extends IndexedSortedSet<Event> {

	public EventsSortedSet2() {
		// Make this compare on time ONLY
		super( new Comparator<Event>() {

			public int compare(Event e1, Event e2) {
				if (e2.getTime() == e1.getTime())
					return 0;
				else if (e2.getTime() > e1.getTime())
					return -1;
				else
					return 1;
			}
		});
	}

	@Override
	public boolean add(Event o) {
		//System.out.println("  add " + o + " " + toString());
		return super.add(o);
	}

	@Override
	public boolean remove(Object o) {
		//System.out.println("remove " + o + " " + toString());

		// TODO Auto-generated method stub
		return super.remove(o);
	}

}
