package sim.collections;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public final class Common {

	/**
	 * Checks that the list is sorted
	 * @param list
	 * @param c
	 * @return
	 */
	public static <E> boolean isSorted(List<E> list, Comparator<? super E> c) {
		if (list.isEmpty())
			return true;

		Iterator<E> i = list.iterator();

		E lastR = i.next();

		while ( i.hasNext()) {
			E r = i.next();

			if (c.compare(lastR, r) > 0 )
				return false;
		}

		return true;
	}

	/**
	 * Inserts an element into a sorted list
	 * @param list
	 * @param e
	 * @param c
	 */
	@SuppressWarnings("unchecked")
	public static <E> void sortedInsert ( List<? extends E> list, E e, Comparator<? super E> c) {

		// We assume the list is sorted
		assert isSorted(list, c);

		int index = Collections.binarySearch(list, e, c);

		if (index >= 0)
			((List<E>)list).add(index, e);
		else
			((List<E>)list).add(-index - 1, e);

		assert isSorted(list, c) : list;
	}
}
